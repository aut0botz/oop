from main import get_auth_token
from websocket import create_connection
from common import *
import json

token = get_auth_token()

def receive_data():
    data = ws.recv()
    data = json.loads(data)
    print(data)

ws = create_connection('ws://ssczlc.mixyun.com/apiproxy/ws/api')
auth_message = {"action": "ping", "Authorization": f"Bearer {token}"}
# extra_headers = {"Authorization": f"Bearer {token}"}
# async with websockets.connect("ws://ssczlc.mixyun.com/apiproxy/ws/api", extra_headers=extra_headers) as ws:
ws.send(json.dumps(auth_message))
res_code = ws.recv()
print(res_code, flush=True)
for obj_id in obj_id_list:
    subscribe_message = {
            "action": "sub_agent_event",
            "event": {"event": "recv_fv", "object_id": obj_id}
        }
    ws.send(json.dumps(subscribe_message))
    res = ws.recv()
    print(res, flush=True)

while True:
    receive_data()