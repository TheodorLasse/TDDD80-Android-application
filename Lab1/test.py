import json
import requests

address = "http://localhost:5050/messages"


def run_tests():
    def test_1():
        payload = {"msg": "this is the first message"}
        response = requests.post(address, data=json.dumps(payload))
        assert response.status_code == 200

    def test_2():
        payload = {"msg": "This is the second message and this message is over 140 characterscharacterscharacters' \
                  'characterscharacterscharacterscharacterscharacterscharacterscharacterscharacterscharacter' \
                  'characterscharacterscharacterscharacterscharacterscharacterscharacterscharacterscharacter' \
                  'characterscharacterscharacterscharacterscharacterscharacterscharacterscharacters"}
        response = requests.post(address, data=json.dumps(payload))
        assert response.status_code == 400

    def test_3():
        payload = {"msg": "this is the third message"}
        response = requests.post(address, data=json.dumps(payload))
        assert response.status_code == 200

        my_message_id = response.json()["id"]

        response = requests.get(address + "/" + my_message_id)
        assert response.status_code == 200
        response = requests.get(address)
        assert response.status_code == 200

        user_id = "Philip"
        response = requests.post(address + "/" + my_message_id + "/read/" + user_id)
        assert response.status_code == 200

        response = requests.get(address + "/unread/" + user_id)
        assert response.status_code == 200
        print(response.json())

        # Posting some more messages
        payload = {"msg": "this is the fourth message"}
        response = requests.post(address, data=json.dumps(payload))
        assert response.status_code == 200
        payload = {"msg": "this is the fifth message"}
        response = requests.post(address, data=json.dumps(payload))
        assert response.status_code == 200
        payload = {"msg": "this is the sixth message"}
        response = requests.post(address, data=json.dumps(payload))
        assert response.status_code == 200

    def test_4():
        user_id = "ThisUserHasNotReadAnyMessagesYet"
        response = requests.get(address + "/unread/" + user_id)
        assert response.status_code == 200

        # Delete the last one of the messages that this user has not read
        msg_list = response.json()

        last_msg = msg_list[-1]
        last_msg_id = last_msg["id"]
        response = requests.delete(address + "/" + last_msg_id)
        assert response.status_code == 200
        response = requests.get(address + "/unread/" + user_id)
        assert response.status_code == 200
        new_msg_list = response.json()

        del msg_list[-1]
        assert msg_list == new_msg_list

    test_1()
    test_2()
    test_3()
    test_4()
    print("Code passed all tests.")


if __name__ == "__main__":
    run_tests()


