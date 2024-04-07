from datapool import DataPool
import asyncio
from inference import Inference
from common import *
import json
from websocket import create_connection
import requests
import datetime
import concurrent.futures
import sys



def get_auth_token():
    try_time = 0
    token = None
    while try_time < 10:
        output = requests.post(url, headers=headers, json=auth_data).json()
        # print(output)
        if output["msg"] == "成功":
            token = output["result"]["access_token"]
            print(f"get new token: {token}", flush=True)
            break
    return token

async def async_get_auth_token():
    global token
    while True:
        try_time = 0
        
        while try_time < 10:
            output = requests.post(url, headers=headers, json=auth_data).json()
            if output["msg"] == "成功":
                token = output["result"]["access_token"]
                expires_in = output["result"]["expires_in"]
                break
        await asyncio.sleep(expires_in - 10)


def get_headers():
    return {"Content-Type": "application/json;charset=UTF-8", "Authorization": f"Bearer {token}"}


def submit_data(obj_id, data, time_str = None):
    try_time = 0
    while try_time < 10:
        headers = get_headers()
        if time_str is None:
            time_str = (datetime.datetime.now()).strftime("%Y-%m-%d %H:%M:%S")
        post_data = {
            "block_mapping":"collect",
            "action": "add_collect_value",
            "object_id": obj_id,
            "collect_id":"COL4153217034991",
            "value": { "sp": data  },
            "collect_at": time_str,
            "collector": "ai-test"}
        res = requests.post(url, json=post_data, headers=headers).json()
        if res["msg"] == '成功':
            break
        elif res["msg"] == "请求-鉴权错误":
            print(res, flush=True)
            get_auth_token()
        else:
            print(res, flush=True)
            pass
        try_time += 1
    return res


async def get_store_data(data_pool, loop):
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
    def receive_data():
        data = ws.recv()
        data = json.loads(data)
        if data.get("block", "") == "mosaic":
            data_pool.push(data["object_id"], data["data"], data["datetime"])
            # print(f"Received Data: {data['object_id']} at {data['datetime']}", flush=True)

    with concurrent.futures.ThreadPoolExecutor() as pool:
        while True:
            loop.run_in_executor(pool, receive_data)
            await asyncio.sleep(0.1)  # Adjust the sleep time as needed



async def process_data(model, data_pool):
    while True:
        obj_list, data_list, last_time_list = data_pool.get(window_size=20)
        # if not obj_list:
        #     await asyncio.sleep(3)
        #     continue
        
        # handle invalid data
        new_obj_list = []
        new_data_list = []
        for obj_id, data in zip(obj_list, data_list):
            set_power = [d["S6_039"] for d in data]
            if any([d.get("S6_072") < 0 or d.get("S6_007") < 998 for d in data]) or max(set_power) - min(set_power) > 100:
                res = submit_data(obj_id, "-1")
                # if res["msg"] != '成功':
                #     print(res, flush=True)
            else:
                new_obj_list.append(obj_id)
                new_data_list.append(data)

        if not new_obj_list:
            await asyncio.sleep(5)
            continue

        pred_list = model.predict(new_data_list)
        for obj_id, pred in zip(new_obj_list, pred_list):
            res = submit_data(obj_id, str(pred))
            if res["msg"] == "成功":
                print(f"{datetime.datetime.strftime(datetime.datetime.now(), '%Y-%m-%d %H:%M:%S')} predict data submitted for {obj_id}, data: {pred}", flush=True)
            else:
                print(res, flush=True)


        await asyncio.sleep(5)  # Adjust the sleep time as needed

async def main():
    max_data_pool_size = 50
    data_pool = DataPool(max_size=max_data_pool_size)
    model = Inference(model_path)

    loop = asyncio.get_event_loop()

    token_task = asyncio.create_task(async_get_auth_token())

    # Create tasks for storing and getting data
    push_task = asyncio.create_task(get_store_data(data_pool, loop))
    get_task = asyncio.create_task(process_data(model, data_pool))

    # Wait for both tasks to complete
    await asyncio.gather(token_task, push_task, get_task)
    # await asyncio.gather(token_task, push_task)


if __name__ == "__main__":
    sys.stdout = open("log-new.txt", "w")
    
    token = get_auth_token()

    asyncio.run(main())

    sys.stdout.close()