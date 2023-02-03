import requests
import json
import time

# address = "http://127.0.0.1:5050/"
address = "https://tddd80-theo-philip.herokuapp.com/"

headers = {'Authorization': 'null'}


def create_user(data):
    print("Creating new user", data)
    response = requests.post(address + "user/new", data=json.dumps(data))
    print(response.status_code)
    assert(response.status_code == 200 or response.status_code == 400)
    print(response)


def login_user(data):
    print("Login user", data)
    response = requests.post(address + "user/login", data=json.dumps(data))
    assert(response.status_code == 200)
    print(response.json())
    auth_token = response.json()['access_token']
    headers['Authorization'] = 'Bearer ' + auth_token


def logout_user():
    print("Logging out user")
    response = requests.delete(address + "user/logout", headers=headers)
    print(response)


def create_post(data):
    print("Creating post", data)
    response = requests.post(address + "post/new", data=json.dumps(data), headers=headers)
    assert (response.status_code == 200 or response.status_code == 400)
    print(response.json())
    return response.json()["post_id"]


def follow_user(username, data):
    print("Making", data['username'], "follow:", username)
    response = requests.post(address + "users/follow_user/" + username, data=json.dumps(data), headers=headers)
    print(response.status_code)
    assert (response.status_code == 200 or response.status_code == 400)


def unfollow_user(username, data):
    print("Making", data['username'], "unfollow:", username)
    response = requests.post(address + "users/unfollow_user/" + username, data=json.dumps(data), headers=headers)
    print(response.status_code)
    assert (response.status_code == 200 or response.status_code == 400)


def like_post(post_id, data):
    print("Liking post", post_id, data)
    response = requests.post(address + "post/like_post/" + post_id, data=json.dumps(data), headers=headers)
    print(response.status_code)
    assert (response.status_code == 200 or response.status_code == 400)


def unlike_post(post_id, data):
    print("Unliking post", post_id, data)
    response = requests.post(address + "post/unlike_post/" + post_id, data=json.dumps(data), headers=headers)
    print(response.status_code)
    assert (response.status_code == 200 or response.status_code == 400)


def get_userinfo(user):
    print("Getting info about user", user)
    response = requests.get(address + "users/view/" + user, headers=headers)
    assert (response.status_code == 200 or response.status_code == 400)
    return response.json()


def get_post(post_id):
    print("Getting info about post", post_id)
    response = requests.get(address + "post/view/" + post_id, headers=headers)
    print(response.json())
    assert (response.status_code == 200 or response.status_code == 400)
    return response.json()


def comment_on_post(post_id, data):
    print("Commenting on post", post_id, data)
    response = requests.post(address + "post/new_comment/" + post_id, data=json.dumps(data), headers=headers)
    assert (response.status_code == 200 or response.status_code == 400)


def test1():

    username = 'Kalle_Anka'

    # Create a new user
    payload = {
        'username': username,
        'password': 'KajsaArSot'
    }
    create_user(payload)

    time.sleep(2)

    # Login with user
    login_user(payload)

    time.sleep(2)

    # Create a post
    payload = {
        'username': username,
        'title': 'oh hi Mark',
        'description': 'I didnt see you there...'
    }

    post_id = create_post(payload)
    post_id_copy = post_id

    time.sleep(2)

    # Get information bout a post
    post_info = get_post(post_id)

    time.sleep(2)

    # Comment on post
    payload = {
        "username": username,
        "content": "hello there\ngeneral kenobi"
    }
    response = comment_on_post(post_id, payload)

    # Get information bout a post
    print("Getting post info for post: " + post_id)
    post_info = get_post(post_id)

    time.sleep(2)

    # Create a post
    payload = {
        'username': username,
        'title': 'oh hi Mark, this is the 2nd post',
        'description': 'I didnt see you there... two times in a row..'
    }
    time.sleep(2)
    post_id = create_post(payload)
    # Get information bout a post
    print("Getting post info for post: " + post_id)
    post_info = get_post(post_id)

    # Comment on post
    payload = {
        "username": username,
        "content": "hello there\ngeneral kenobi"
    }
    comment_on_post(post_id, payload)
    time.sleep(1)
    comment_on_post(post_id, payload)
    time.sleep(1)
    comment_on_post(post_id, payload)
    time.sleep(1)
    comment_on_post(post_id, payload)
    time.sleep(1)

    # Get info
    post_info = get_post(post_id)

    # Get info
    post_info = get_post(post_id_copy)


def test2():

    username = 'Kalle_Anka'

    # Create a new user
    payload = {
        'username': username,
        'password': 'KajsaArSot'
    }
    create_user(payload)

    time.sleep(1)

    # Login with user
    login_user(payload)

    time.sleep(1)

    # Create a post
    payload = {
        'username': username,
        'title': 'oh hi Mark',
        'description': 'I didnt see you there...'
    }

    post_id = create_post(payload)

    time.sleep(1)

    payload = {
        'username': username
    }
    # Like the post
    like_post(post_id, payload)

    time.sleep(1)
    # Like the post again
    like_post(post_id, payload)

    time.sleep(1)
    # Get information about the user
    print(get_userinfo(username))

    time.sleep(1)
    # Unlike the post
    unlike_post(post_id, payload)

    time.sleep(1)
    # Get information about the user
    print(get_userinfo(username))

    # Logout
    logout_user()

    time.sleep(1)
    # Create a 2nd user
    username2 = 'Farmor_Anka'
    payload = {
        'username': username2,
        'password': 'KajsaArSot'
    }
    create_user(payload)

    # Login with new user
    time.sleep(1)
    login_user(payload)

    # Follow the old user
    payload = {
        'username': username2,
    }
    follow_user(username, payload)

    # Follow the old user again
    payload = {
        'username': username2,
    }
    follow_user(username, payload)

    # Get information about the new user
    time.sleep(1)
    print(get_userinfo(username2))

    # Get information about the old user
    time.sleep(1)
    print(get_userinfo(username))

    # Unfollow the old user
    time.sleep(1)
    payload = {
        'username': username2,
    }
    unfollow_user(username, payload)

    # Get information about the new user
    time.sleep(1)
    print(get_userinfo(username2))


if __name__ == '__main__':
    test1()
    test2()