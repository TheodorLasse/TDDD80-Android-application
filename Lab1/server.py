import requests
from flask import Flask, jsonify, request
import uuid
import json

app = Flask(__name__)
messages = {}
read_by = {}


def get_dict(res):
    return res.json()


def get_status_code(res):
    return res.status_code


@app.route('/')
def start():
    return "Home page!"


@app.route('/messages', methods=['POST'])
def add_message():
    data = json.loads(request.data)
    msg = data["msg"]
    if len(msg) > 140:
        return '{"response":"Message is over 140 characters long"}', 400
    msg_id = str(uuid.uuid4())
    messages[msg_id] = msg
    read_by[msg_id] = []
    return jsonify({"id": msg_id}), 200


@app.route('/messages/<MessageID>', methods={'GET'})
def get_message(MessageID):
    if MessageID in messages:
        return jsonify({"id": MessageID, "msg": messages[MessageID], "read_by": read_by[MessageID]}), 200
    return '{"response":"ID does not match any stored message"}', 404


@app.route('/messages/<MessageID>', methods={'DELETE'})
def delete_message(MessageID):
    if MessageID in messages:
        del messages[MessageID]
        del read_by[MessageID]
        return "", 200
    return '{"response":"ID does not match any stored message"}', 404


@app.route('/messages/<MessageID>/read/<UserId>', methods={'POST'})
def mark_read(MessageID, UserId):
    if MessageID in messages:
        if UserId not in read_by[MessageID]:
            read_by[MessageID].append(UserId)
        return "", 200
    return '{"response":"ID does not match any stored message"}', 404


@app.route('/messages', methods={'GET'})
def all_message():
    all_msgs = []
    for ID in messages:
        all_msgs.append({"id": ID, "msg": messages[ID], "read_by": read_by[ID]})
    return jsonify(all_msgs), 200


@app.route('/messages/unread/<UserID>', methods={'GET'})
def unread_message(UserID):
    unread_msgs = []
    for ID in messages:
        if UserID not in read_by[ID]:
            unread_msgs.append({"id": ID, "message": messages[ID], "readby": read_by[ID]})
    return jsonify(unread_msgs), 200


if __name__ == "__main__":
    app.debug = True
    app.run(port=5050)




