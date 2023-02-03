import requests
from flask import Flask, jsonify, request
from flask_sqlalchemy import SQLAlchemy
import uuid
import json

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///./database.db'
db = SQLAlchemy(app)

user_identifier = db.Table('user_identifier',
                           db.Column('id', db.String(60), db.ForeignKey('messages.id'), primary_key=True),
                           db.Column('name', db.String(60), db.ForeignKey('users.name'), primary_key=True)
                           )


class User(db.Model):
    __tablename__ = 'users'
    id = db.Column(db.String(60), primary_key=True)
    name = db.Column(db.String(60), unique=True)


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
    return "<h1>Home page!</h1>"


@app.route('/messages', methods=['POST'])
def add_message():
    print("Adding message..")
    data = json.loads(request.data)
    user = User.query.filter_by(id=data['user_id']).first()
    msg = data["msg"]

    if not user:
        return {"response": "User is not set"}, 400
    elif not msg:
        return {"response": "Msg is not set"}, 400
    elif len(msg) > 140:
        return {"response": "Message is over 140 characters long"}, 400

    msg_id = str(uuid.uuid4())
    new_msg = Message(id=msg_id, msg=msg, user=user.id)
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

    read_by_list = [user.id for user in msg.read_by]
    return jsonify({"id": msg.id, "msg": msg.msg, "read_by": read_by_list}), 200


@app.route('/messages/<MessageID>', methods={'DELETE'})
def delete_message(MessageID):
    print("Deleting message..")
    msg = Message.query.filter_by(id=MessageID).first()
    if not msg:
        return '{"response":"ID does not match any stored message"}', 400
    db.session.delete(msg)
    db.session.commit()
    return '', 200


@app.route('/messages/new_user/<Username>', methods={'POST'})
def create_user(Username):
    print("Creating user..")
    if Username in [user.name for user in User.query.all()]:
        return '{"response":"This user already exists"}', 400
    user_id = str(uuid.uuid4())
    new_user = User(id=user_id, name=Username)
    db.session.add(new_user)
    db.session.commit()
    return jsonify({'id': user_id}), 200


@app.route('/messages/<MessageID>/read/<UserID>', methods={'POST'})
def mark_read(MessageID, UserID):
    print("Reading message..")
    msg = Message.query.filter_by(id=MessageID).first()
    user = User.query.filter_by(id=UserID).first()
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
        all_msgs.append({"id": message.id, "msg": message.msg, "read_by": [user.id for user in message.read_by]})
    if not all_msgs:
        return '{"response":"There are no messages in the database"}', 400
    return jsonify(all_msgs), 200


@app.route('/messages/unread/<UserID>', methods={'GET'})
def unread_message(UserID):
    unread_msgs = []
    for message in Message.query.all():
        if UserID not in [user.id for user in message.read_by]:
            unread_msgs.append({"id": message.id, "msg": message.msg, "read_by": [user.id for user in message.read_by]})
    return jsonify(unread_msgs), 200


def init_db():
    db.drop_all()
    db.create_all()


if __name__ == "__main__":
    init_db()
    app.debug = True
    app.run(port=5050)
