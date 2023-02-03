import os
import tempfile
import pytest
import coverage
import json
from Lab2 import server


@pytest.fixture
def client():
    db_fd, name = tempfile.mkstemp()
    server.app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///'+str(name)
    server.app.config['TESTING'] = True
    with server.app.test_client() as client:
        with server.app.app_context():
            server.init_db()
        yield client
    os.close(db_fd)
    os.unlink(name)


def test_new_user(client):
    username = "Philip"
    rv = client.post(f'/messages/new_user/{username}', json="")
    assert 200 == rv.status_code


def test_duplicate_user(client):
    username = "Philip"
    rv = client.post(f'/messages/new_user/{username}', json="")
    assert 200 == rv.status_code
    rv = client.post(f'/messages/new_user/{username}', json="")
    assert 400 == rv.status_code


def test_main(client):
    rv = client.post('/messages/new_user/' + "Philip")
    assert 200 == rv.status_code
    philip_id = json.loads(rv.data)['id']

    rv = client.post('/messages/new_user/' + "Theo")
    assert 200 == rv.status_code
    theo_id = json.loads(rv.data)['id']

    rv = client.post('/messages/new_user/' + "Philip")
    assert 400 == rv.status_code

    payload = {"user_id": philip_id, "msg": "Msg from mr philip"}
    rv = client.post('/messages', json=payload)
    assert 200 == rv.status_code
    philip_msg_id = json.loads(rv.data)['id']

    payload = {"user_id": theo_id, "msg": "Msg from the other guy named theo"}
    rv = client.post('/messages', json=payload)
    assert 200 == rv.status_code
    theo_msg_id = json.loads(rv.data)['id']

    rv = client.get('/messages' + '/' + philip_msg_id)
    assert 200 == rv.status_code

    rv = client.post('/messages/' + philip_msg_id + "/read/" + philip_id)
    assert 200 == rv.status_code

    rv = client.get('/messages')
    assert 200 == rv.status_code

    rv = client.get('/messages' + "/unread/" + philip_id)
    assert 200 == rv.status_code
    temp_ids = []
    temp_msgs = json.loads(rv.data)
    for message in temp_msgs:
        temp_ids.append(message['id'])

    assert philip_msg_id not in temp_ids
    assert theo_msg_id in temp_ids

    rv = client.get('/messages' + "/unread/" + theo_id)
    assert 200 == rv.status_code
    temp_ids = []
    temp_msgs = json.loads(rv.data)
    for message in temp_msgs:
        temp_ids.append(message['id'])
    assert philip_msg_id in temp_ids
    assert theo_msg_id in temp_ids


