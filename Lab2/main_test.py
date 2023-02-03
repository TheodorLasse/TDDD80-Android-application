import requests
import json

address = "http://localhost:5050/messages"

if __name__ == "__main__":
    """
    payload = {"user": "Philip", "msg": "Msg 1"}
    first_msg = payload['msg']
    response = requests.post(address, data=json.dumps(payload))

    id = response.json()['id']

    response = requests.get(address + '/' + id)
    print(response.json())

    response = requests.delete(address + '/' + id)
    print(response.status_code)

    response = requests.get(address + '/' + id)
    print(response.json())
    """
    response = requests.post(address + '/new_user/' + "Philip")
    philip_id = response.json()['id']
    print(response.json())

    response = requests.post(address + '/new_user/' + "Theo")
    theo_id = response.json()['id']
    print(response.json())

    response = requests.post(address + '/new_user/' + "Philip")
    print(response.json())

    payload = {"user_id": philip_id, "msg": "Msg from mr philip"}
    response = requests.post(address, data=json.dumps(payload))
    philip_msg_id = response.json()['id']
    print(response.json())

    payload = {"user_id": theo_id, "msg": "Msg from the other guy named theo"}
    response = requests.post(address, data=json.dumps(payload))
    theo_msg_id = response.json()['id']
    print(response.json())

    response = requests.get(address + '/' + philip_msg_id)
    print(response.json())

    response = requests.post(address + '/' + philip_msg_id + "/read/" + philip_id)
    print(response.status_code)

    response = requests.get(address)
    print(response.json())

    response = requests.get(address + "/unread/" + philip_id)
    print(response.json())
    response = requests.get(address + "/unread/" + theo_id)
    print(response.json())