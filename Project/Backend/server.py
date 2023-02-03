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

user_identifier = db.Table('user_identifier',
                           db.Column('id', db.String(60), db.ForeignKey('messages.id'), primary_key=True),
                           db.Column('name', db.String(60), db.ForeignKey('users.name'), primary_key=True)
                           )


class TokenBlockList(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    jti = db.Column(db.String(36), nullable=False)
    created_at = db.Column(db.DateTime, nullable=False)


@jwt.token_in_blocklist_loader
def check_if_token_revoked(jwt_header, jwt_payload):
    jti = jwt_payload["jti"]
    token = db.session.query(TokenBlockList.id).filter_by(jti=jti).scalar()
    return token is not None


class User(db.Model):
    __tablename__ = 'users'
    name = db.Column(db.String(60), primary_key=True, unique=True)
    id = db.Column(db.String(60), unique=True)
    password = db.Column(db.String(60), unique=False, nullable=False)


class Message(db.Model):
    __tablename__ = 'messages'
    id = db.Column(db.String(60), primary_key=True, unique=True)
    msg = db.Column(db.String(60))
    user = db.Column(db.String(60))
    read_by = db.relationship('User', secondary=user_identifier, lazy="subquery")


def get_dict(res):
    return res.json()


def get_status_code(res):
    return res.status_code


@app.route('/')
def start():
    return "<div style=\"background-color: red;width:10rem;height:10rem;margin:auto;\"></div>"


@app.route('/messages', methods=['POST'])
@jwt_required()
def add_message():
    print("Adding message..")
    data = json.loads(request.data)
    user = User.query.filter_by(name=data['username']).first()
    msg = data["msg"]

    if not user:
        return {"response": "User is not set"}, 400
    elif not msg:
        return {"response": "Msg is not set"}, 400
    elif len(msg) > 140:
        return {"response": "Message is over 140 characters long"}, 400

    msg_id = str(uuid.uuid4())
    new_msg = Message(id=msg_id, msg=msg, user=user.name)
    db.session.add(new_msg)
    db.session.commit()

    print(new_msg.id)
    return jsonify({"id": new_msg.id}), 200


@app.route('/messages/<MessageID>', methods={'GET'})
def get_message(MessageID):
    print("Fetching message..")
    msg = Message.query.filter_by(id=MessageID).first()
    if not msg:
        return '{"response":"ID does not match any stored message"}', 400

    read_by_list = [user.name for user in msg.read_by]
    return jsonify({"id": msg.id, "msg": msg.msg, "read_by": read_by_list}), 200


@app.route('/messages/<MessageID>', methods={'DELETE'})
@jwt_required()
def delete_message(MessageID):
    print("Deleting message..")
    msg = Message.query.filter_by(id=MessageID).first()
    if not msg:
        return '{"response":"ID does not match any stored message"}', 400
    db.session.delete(msg)
    db.session.commit()
    return '', 200


@app.route('/messages/<MessageID>/read/<UserID>', methods={'POST'})
@jwt_required()
def mark_read(MessageID, UserID):
    print("Reading message..")
    msg = Message.query.filter_by(id=MessageID).first()
    user = User.query.filter_by(name=UserID).first()
    if not msg:
        return '{"response":"ID does not match any stored message"}', 400
    elif not user:
        return '{"response":"UserID does not exist"}', 400
    msg.read_by.append(user)
    db.session.commit()
    return '', 200


@app.route('/messages', methods={'GET'})
def all_message():
    all_msgs = []
    for message in Message.query.all():
        all_msgs.append({"id": message.id, "msg": message.msg, "read_by": [user.name for user in message.read_by]})
    if not all_msgs:
        return '{"response":"There are no messages in the database"}', 400
    return jsonify(all_msgs), 200


@app.route('/messages/unread/<UserID>', methods={'GET'})
@jwt_required()
def unread_message(UserID):
    unread_msgs = []
    for message in Message.query.all():
        if UserID not in [user.name for user in message.read_by]:
            unread_msgs.append({"id": message.id, "msg": message.msg, "read_by": [user.name for user in message.read_by]})
    return jsonify(unread_msgs), 200


@app.route('/user', methods={'POST'})
def create_user():
    data = json.loads(request.data)

    user = User.query.filter_by(name=data['username']).first()
    print(user)
    if user is None:
        hash_pwd = bcrypt.generate_password_hash(data['password']).decode('utf-8')

        user_id = str(uuid.uuid4())

        new_user = User(id=user_id, name=data['username'], password=hash_pwd)
        db.session.add(new_user)
        db.session.commit()
        return "", 200
    else:
        return {"response": "User already exists"}, 400


@app.route('/user/login', methods={'POST'})
def user_login():
    data = json.loads(request.data)

    user = User.query.filter_by(name=data['username']).first()

    if user is not None and bcrypt.check_password_hash(user.password, data['password']):
        token = create_access_token(identity=user.name)
        response = jsonify(access_token=token)
        set_access_cookies(response, token)
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


if __name__ == "__main__":
    init_db()
    app.debug = True
    app.run(port=5050)
