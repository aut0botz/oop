from common import feature_names, obj_id_list
from collections import defaultdict
import datetime
from datetime import timedelta
import copy

class ObjectData:
    def __init__(self, max_size=10):
        self.data_list = []
        self.max_size = max_size
    
    def push(self, data):
        data["datetime"] = datetime.datetime.now()
        self.data_list.append(data)
        while len(self.data_list) > self.max_size:
            self.data_list.pop(0) 
    
    def _organize_data(self, get_data):
        all_data = []
        for item in get_data:
            data = []
            for k in feature_names:
                data.append(item[k])
            all_data.append(data)
        return all_data
    
    def get(self, window_size=15):
        data_list = copy.deepcopy(self.data_list)
        if not data_list:
            return [], None
        window_end = self.data_list[-1]["datetime"]
        window_start = window_end - timedelta(seconds=window_size)
        get_data = []
        for data in data_list:
            if data["datetime"] > window_start:
                get_data.append(data)
        if len(get_data) > 10:
            # get_data = self._organize_data(get_data)
            return get_data, window_end
        else:
            return [], None
        


class DataPool:
    def __init__(self, max_size=10):
        self.obj2data = {obj: ObjectData(max_size=max_size) for obj in obj_id_list}
    
    def push(self, obj, data, date_time):
        time_str = date_time.split(".")
        date_time = ".".join(time_str[:-1] + [time_str[-1][:3]])
        data["datetime"] = datetime.datetime.strptime(date_time, "%Y-%m-%d %H:%M:%S.%f")
        self.obj2data[obj].push(data)
    
    def get(self, window_size=10):
        # return obj_list, data_list, last_tiime_list
        obj_list = []
        data_list = []
        last_time_list = []
        for obj, objdata in self.obj2data.items():
            data, timestamp = objdata.get(window_size=window_size)
            if data:
                obj_list.append(obj)
                data_list.append(data)
                last_time_list.append(timestamp)
        return obj_list, data_list, last_time_list

            
if __name__ == "__main__":
    import asyncio
    feature_names = ["feature1", "feature2", "feature3"]
    async def push_data(data_pool):
        while True:
            # Simulating the data you want to push
            sample_data = {"data": {"feature1": 1, "feature2": 2, "feature3": 3}}
            for obj_id in obj_id_list:
                data_pool.push(obj_id, sample_data)
            await asyncio.sleep(0.1)  # Adjust the sleep time as needed

    async def get_and_process_data(data_pool):
        while True:
            obj_list, data_list, last_time_list = data_pool.get(window_size=10)
            if obj_list:
                print(f"Retrieved data for objects: {obj_list}")
                print(f"Data: {data_list}")
                print(f"Last Timestamps: {last_time_list}")
            await asyncio.sleep(5)  # Adjust the sleep time as needed

    async def main():
        max_data_pool_size = 20
        data_pool = DataPool(max_size=max_data_pool_size)

        # Create tasks for pushing and getting data
        push_task = asyncio.create_task(push_data(data_pool))
        get_task = asyncio.create_task(get_and_process_data(data_pool))

        # Wait for both tasks to complete
        await asyncio.gather(push_task, get_task)

    asyncio.run(main())