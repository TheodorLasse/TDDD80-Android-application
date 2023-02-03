import requests
from flask import Flask, jsonify, request
from flask_sqlalchemy import SQLAlchemy
from flask_bcrypt import Bcrypt
from flask_jwt_extended import (JWTManager, jwt_required, create_access_token, get_jwt_identity, verify_jwt_in_request,
                                set_access_cookies, get_jwt)
from datetime import datetime, timedelta, timezone
from functools import wraps

import uuid
import json
import os

debug = True

app = Flask(__name__)

if 'NAMESPACE' in os.environ and os.environ['NAMESPACE'] == 'heroku':
    db_uri = os.environ['DATABASE_URL']
    debug_flag = False
else:
    db_path = os.path.join(os.path.dirname(__file__), 'app.db')
    db_uri = 'sqlite:///{}'.format(db_path)
    debug_flag = True

app.config['SQLALCHEMY_DATABASE_URI'] = db_uri
app.config['JWT_SECRET_KEY'] = 'mysecretkey'
app.config['JWT_TOKEN_LOCATION'] = ["headers", "cookies", "json", "query_string"]

ACCESS_EXPIRES = timedelta(weeks=1)
app.config['JWT_ACCESS_TOKEN_EXPIRES'] = ACCESS_EXPIRES

db = SQLAlchemy(app)
bcrypt = Bcrypt(app)
jwt = JWTManager(app)


class TokenBlockList(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    jti = db.Column(db.String(36), nullable=False)
    created_at = db.Column(db.DateTime, nullable=False)


association_table_post_user = db.Table('association_table_post_user',
                                       db.Column('username', db.String(60), db.ForeignKey('user.username'),
                                                 primary_key=True),
                                       db.Column('post', db.String(60), db.ForeignKey('post.id'), primary_key=True)
                                       )

association_table_user_followed_users = db.Table('association_table_user_followed_users',
                                       db.Column('follower_username', db.String(60), db.ForeignKey('user.username'),
                                                 primary_key=True),
                                       db.Column('followed_username', db.String(60), db.ForeignKey('user.username'),
                                                 primary_key=True)
                                       )

association_table_post_comment = db.Table('association_table_post_comment',
                                          db.Column('id', db.String(60), db.ForeignKey('post.id'), primary_key=True),
                                          db.Column('comment', db.String(60), db.ForeignKey('comment.id'),
                                                    primary_key=True)
                                          )

association_table_user_comment = db.Table('association_table_user_comment',
                                          db.Column('username', db.String(60), db.ForeignKey('user.username'),
                                                    primary_key=True),
                                          db.Column('comment', db.String(60), db.ForeignKey('comment.id'),
                                                    primary_key=True)
                                          )

association_liked_by_user = db.Table('association_liked_by_user',
                                          db.Column('username', db.String(60), db.ForeignKey('user.username'),
                                                    primary_key=True),
                                          db.Column('post', db.String(60), db.ForeignKey('post.id'),
                                                    primary_key=True)
                                          )


class User(db.Model):
    __tablename__ = 'user'
    username = db.Column(db.String(60), db.ForeignKey('user.username'), primary_key=True, unique=True)
    password = db.Column(db.String(60), unique=False, nullable=False)
    posts = db.relationship('Post', secondary=association_table_post_user)
    comments = db.relationship('Comment', secondary=association_table_user_comment)
    liked_posts = db.relationship('Post', secondary=association_liked_by_user)
    followed_users = db.relationship('User', secondary=association_table_user_followed_users,
                                     primaryjoin=username==association_table_user_followed_users.c.follower_username,
                                     secondaryjoin=username==association_table_user_followed_users.c.followed_username,
                                     backref='followers')


class Post(db.Model):
    __tablename__ = 'post'
    id = db.Column(db.String(60), primary_key=True, unique=True)
    user = db.relationship('User', secondary=association_table_post_user)
    title = db.Column(db.String(120), nullable=False)
    description = db.Column(db.String(2500), nullable=False)
    liked_by = db.relationship('User', secondary=association_liked_by_user)
    comments = db.relationship('Comment', secondary=association_table_post_comment)


class Comment(db.Model):
    __tablename__ = 'comment'
    id = db.Column(db.String(60), primary_key=True, unique=True)
    user = db.relationship('User', secondary=association_table_user_comment)
    content = db.Column(db.String(60), nullable=False)
    post = db.relationship('Post', secondary=association_table_post_comment)


@jwt.token_in_blocklist_loader
def check_if_token_revoked(jwt_header, jwt_payload):
    jti = jwt_payload["jti"]
    token = db.session.query(TokenBlockList.id).filter_by(jti=jti).scalar()
    return token is not None

@app.route('/users/follow_user/<Username>', methods={"POST"})
@jwt_required()
def follow_user(Username):
    if debug:
        print("Attempting to follow user:", Username)
    """
    This method makes the user in the request data follow the <Username>
    """

    other_user = User.query.filter_by(username=Username).first()

    if not other_user:
        return '{"response":"Username does not exist"}', 400

    data = json.loads(request.data)
    if 'username' not in data.keys():
        return '{"response": "Data must contain username"}', 400
    main_user = User.query.filter_by(username=data['username']).first()
    if not other_user:
        return '{"response":"Username from request data does not exist"}', 400

    followed_users = main_user.followed_users
    if other_user not in followed_users:
        followed_users.append(other_user)
        print("Making", main_user.username, "follow", other_user.username)
        print("Setting", main_user.username, "'s followed_users to", followed_users)
        setattr(main_user, 'followed_users', followed_users)
        db.session.commit()
        return "", 200
    else:
        print("User is already following this user.")
        return '{"response": "User is already following this user"}', 400


@app.route('/users/unfollow_user/<Username>', methods={"POST"})
@jwt_required()
def unfollow_user(Username):
    if debug:
        print("Attempting to unfollow user:", Username)
    """
    This method makes the user in the request data unfollow the <Username>
    """

    other_user = User.query.filter_by(username=Username).first()

    if not other_user:
        return '{"response":"Username does not exist"}', 400

    data = json.loads(request.data)
    if 'username' not in data.keys():
        return '{"response": "Data must contain username"}', 400
    main_user = User.query.filter_by(username=data['username']).first()
    if not other_user:
        return '{"response":"Username from request data does not exist"}', 400

    followed_users = main_user.followed_users
    if other_user in followed_users:
        followed_users.remove(other_user)
        setattr(main_user, 'followed_users', followed_users)
        db.session.commit()
        return "", 200
    else:
        print("User is not following this user.")
        return '{"response": "User is not following this user"}', 400


@app.route('/users/view/<Username>', methods={'GET'})
@jwt_required()
def get_userinfo(Username):
    if debug:
        print("Getting user information. Username:", Username)
    """
    This method returns information about a user, such as posts made, liked posts etc..
    :return:
    """
    user = User.query.filter_by(username=Username).first()

    if not user:
        return '{"response":"Username does not exist"}', 400

    comments = {}
    for comment in user.comments:
        comments[comment.id] = {
            "post": comment.post[0].id,
            "content": comment.content
        }

    posts = [post.id for post in user.posts]
    liked_posts = [liked_post.id for liked_post in user.liked_posts]
    followed_users = [followed_user.username for followed_user in user.followed_users]

    data = {
        "posts": posts,
        "comments": comments,
        "liked_posts": liked_posts,
        "followed_users": followed_users
    }
    print("Gathered data from user:", user.username, "\n", data)
    return jsonify(data), 200


@app.route('/post/view/<PostID>', methods={'GET'})
@jwt_required()
def get_post(PostID):
    if debug:
        print("Getting post information. Post ID:", PostID)
    """
    This method returns information about a post, such as the title, the author etc..
    :return:
    """
    post = Post.query.filter_by(id=PostID).first()

    if not post:
        return '{"response":"PostID does not exist"}', 400

    comments = {}
    for comment in post.comments:
        comments[comment.id] = {
            "user": comment.user[0].username,
            "content": comment.content
        }

    data = {
        "author": post.user[0].username,
        "title": post.title,
        "description": post.description,
        "likes": len([user for user in post.liked_by]),
        "comments": comments
    }
    print("Gathered data from post:", post.id, "\n", data)
    return jsonify(data), 200


@app.route('/post/new_comment/<PostID>', methods={'POST'})
@jwt_required()
def comment_on_post(PostID):
    """
    This method creates a new comment on a post
    :return:
    """
    if debug:
        print("Attempting to comment on post", PostID)
    post = Post.query.filter_by(id=PostID).first()
    if not post:
        return '{"response":"PostID does not exist"}', 400

    data = json.loads(request.data)
    if 'username' not in data.keys() or 'content' not in data.keys():
        return {"response": "Data must contain username and content."}, 400

    user = User.query.filter_by(username=data['username']).first()
    if not user:
        return {"response": "User does not exist"}, 400

    comment_id = str(uuid.uuid4())
    new_comment = Comment(id=comment_id, user=[user], content=data['content'], post=[post])
    db.session.add(new_comment)
    db.session.commit()

    if debug:
        print(data['username'], "successfully commented on post", PostID)
    return "", 200


@app.route('/post/like_post/<PostID>', methods={'POST'})
@jwt_required()
def like_post(PostID):
    """
    This method makes a user like a post
    :return:
    """
    data = json.loads(request.data)
    if debug:
        print("Attempting to like post", PostID)
    if 'username' not in data.keys():
        return {"response": "Data must contain username."}, 400

    user = User.query.filter_by(username=data['username']).first()
    if not user:
        return {"response": "User does not exist"}, 400

    post = Post.query.filter_by(id=PostID).first()
    if not post:
        return {"response": "Post does not exist"}, 400

    # Check if user is already liking the post, if not, like it.
    liked_by = post.liked_by
    if user not in liked_by:
        print("Liked post.")
        liked_by.append(user)
        setattr(post, 'liked_by', liked_by)
        db.session.commit()
        return "", 200
    else:
        print("User is already liking post.")
        return {"response": "User already likes this post"}, 400


@app.route('/post/unlike_post/<PostID>', methods={'POST'})
@jwt_required()
def unlike_post(PostID):
    """
    This method makes a user unlike a post
    :return:
    """
    data = json.loads(request.data)
    if debug:
        print("Attempting to unlike post", PostID)
    if 'username' not in data.keys():
        return {"response": "Data must contain username."}, 400

    user = User.query.filter_by(username=data['username']).first()
    if not user:
        return {"response": "User does not exist"}, 400

    post = Post.query.filter_by(id=PostID).first()
    if not post:
        return {"response": "Post does not exist"}, 400

    # Check if user is already liking the post, if so, unlike it
    liked_by = post.liked_by
    if user in liked_by:
        print("Unliked post.")
        liked_by.remove(user)
        setattr(post, 'liked_by', liked_by)
        db.session.commit()
        return "", 200
    else:
        print("User does not like this post.")
        return {"response": "User does not like this post"}, 400


@app.route('/post/new', methods={'POST'})
@jwt_required()
def create_post():
    """
    This method creates a new post.
    Requires the user to be logged in.

    Package must contain the user's name, a title and a description:
    {
        'username': 'hi Mark',
        'title': 'oh hi',
        'description': 'I didnt see you there...'
    }

    A unique id is generated for the post.
    Liked_by is null by default.

    Returns nothing.
    """
    if debug:
        print("Attempting to create a new post")
    data = json.loads(request.data)

    if 'username' not in data.keys() or 'title' not in data.keys() or 'description' not in data.keys():
        return {"response": "Data must contain username, title and description."}, 400

    user = User.query.filter_by(username=data['username']).first()
    print(data['username'])

    if user is None:
        return {"response": "User does not exist"}, 400
    else:
        post_id = str(uuid.uuid4())
        new_post = Post(id=post_id, user=[user], title=data['title'], description=data['description'])
        db.session.add(new_post)
        db.session.commit()
        if debug:
            print("New post created:", post_id)
        return jsonify({"post_id": post_id}), 200


@app.route('/user/new', methods={'POST'})
def create_user():
    """
    This method creates a new user.

    Package must contain a 'username' and a 'password' value.
    A unique ID is not required- as the username is required to be unique.

    Returns nothing.
    """
    data = json.loads(request.data)

    if debug:
        print("Attempting to create a new user.")

    if 'username' not in data.keys() or 'password' not in data.keys():
        return {"response": "Data must contain username and password."}, 400

    user = User.query.filter_by(username=data['username']).first()

    if user is None:
        hash_pwd = bcrypt.generate_password_hash(data['password']).decode('utf-8')

        new_user = User(username=data['username'], password=hash_pwd)
        db.session.add(new_user)
        db.session.commit()
        if debug:
            print("New user created:", new_user.username)
        return "", 200
    else:
        return {"response": "User already exists"}, 400


@app.route('/user/login', methods={'POST'})
def user_login():
    """
    This method handles logins.
    Package must contain a 'username' and a 'password' value.

    Returns a jwt token that proves a user is logged in.
    NOTE: Fetched using:
    auth_token = response.json()['access_token']
    Used with:
    headers = {'Authorization': 'Bearer ' + auth_token}
    """
    data = json.loads(request.data)
    if debug:
        print("Login attempt started.")

    if 'username' not in data.keys() or 'password' not in data.keys():
        return {"response": "Data must contain username and password."}, 400

    user = User.query.filter_by(username=data['username']).first()

    if user is not None and bcrypt.check_password_hash(user.password, data['password']):
        token = create_access_token(identity=user.username)
        response = jsonify(access_token=token)
        set_access_cookies(response, token)
        if debug:
            print("User logged in successfully.")
        return response
    else:
        return {"response": "Username or password is incorrect"}, 400


@app.route("/user/logout", methods=["DELETE"])
@jwt_required()
def modify_token():
    jti = get_jwt()["jti"]
    now = datetime.now(timezone.utc)
    db.session.add(TokenBlockList(jti=jti, created_at=now))
    db.session.commit()
    return "", 200


def init_db():
    db.drop_all()
    db.create_all()


# if __name__ == "__main__":
#     init_db()
#     app.debug = True
#     app.run(port=5050)
