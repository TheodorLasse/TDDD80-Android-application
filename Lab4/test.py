import requests
import json

# address = "http://127.0.0.1:5050/"
address = "https://tddd80-theo-philip.herokuapp.com/"


def run_tests():

    # Create a new user
    payload = {
        'username': 'Kalle Anka',
        'password': 'KajsaArSot'
    }

    response = requests.post(address + "user", data=json.dumps(payload))
    assert(response.status_code == 200 or response.status_code == 400)
    print(response)

    # Login with said user
    response = requests.post(address + "user/login", data=json.dumps(payload))
    assert(response.status_code == 200)
    print(response.json())
    auth_token = response.json()['access_token']
    headers = {'Authorization': 'Bearer ' + auth_token}

    # Post messages
    payload = {"username": 'Kalle Anka', "msg": "Msg from mr philip"}
    response = requests.post(address + '/messages', data=json.dumps(payload), headers=headers)
    assert(response.status_code == 200)
    msg_id = response.json()['id']
    print(response.json())

    # Get messages
    response = requests.get(address + '/messages/' + msg_id)
    assert(response.status_code == 200)
    print(response.json())

    # Get unread messages
    response = requests.get(address + '/messages/unread/Kalle Anka', headers=headers)
    assert(response.status_code == 200)
    print(response.json())

    # Read messages
    response = requests.post(address + '/messages/' + msg_id + "/read/Kalle Anka", headers=headers)
    assert(response.status_code == 200)

    # Get unread messages again
    response = requests.get(address + '/messages/unread/Kalle Anka', headers=headers)
    assert(response.status_code == 200)
    print(response.json())

    # Delete messages
    response = requests.delete(address + '/messages/' + msg_id, headers=headers)
    assert(response.status_code == 200)
    response = requests.delete(address + '/messages/' + msg_id, headers=headers)
    assert(response.status_code == 400)
    print(response.json())

    # Get all messages (should be empty)
    response = requests.get(address + '/messages')
    assert(response.status_code == 400)
    print(response.json())

    # Log out
    response = requests.delete(address + "user/logout", headers=headers)
    print(response)

    # Post message after being logged out
    payload = {"username": 'Kalle Anka', "msg": "Msg from mr philip"}
    response = requests.post(address + '/messages', data=json.dumps(payload), headers=headers)
    assert(response.status_code == 401)

    print("Code passed all tests.")


if __name__ == '__main__':
    run_tests()
