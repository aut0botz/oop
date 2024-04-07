import torch
import torch.nn as nn
from torch.nn.utils.rnn import pad_sequence
from common import *
import sys
sys.path.append("../")
from src.model import SimpleRNN






class Inference:
    def __init__(self, model_path):
        self.model = SimpleRNN(input_size, hidden_size, num_layers, num_classes)
        self.model.load_state_dict(torch.load(model_path, map_location="cpu"))
        self.device = "cpu"
        if torch.cuda.is_available():
            self.device = "cuda:0"
            self.model = self.model.cuda()

    def convert_data_to_tensor(self, data_list):
        all_data = []
        lengths = []
        for item_list in data_list:
            # print(item_list)
            data = []
            for item in item_list:
                features = []
                for k in feature_names:
                    features.append(item[k])
                data.append(features)
            seq_data = torch.tensor(data)
            all_data.append(seq_data)
            lengths.append(seq_data.size(0))
            # print(all_data[-1].size())
        all_data = pad_sequence(all_data).permute(1,0,2)
        return all_data.to(self.device), lengths
    
    
    def predict(self, data):
        """
        大约15秒连续记录的机组状态数据
        data: [
            {
                "S6_001": 79,
                "S6_002": 203.2,
                ...
                "time": "2021-03-29 17:50:07"
            },
            {
                "S6_001": 79,
                "S6_002": 202.9,
                ...
                "time": "2021-03-29 17:49:36"
            },
            {
                "S6_001": 79,
                "S6_002": 203.6,
                ...
                "time": "2021-03-29 17:43:01"
            },
            {
                "Y02_1": 82,
                "Y03_1": 203.8,
                "time": "2021-03-29 17:31:02"
            }
        ]
        """
        data, lengths = self.convert_data_to_tensor(data)
        output = self.model(data, lengths)
        # predictions = torch.argmax(output, dim=-1)
        predictions = output.squeeze()
        # predictions = torch.round(output).long().squeeze()
        # print(predictions.cpu().tolist())
        return predictions.cpu().tolist()

if __name__ == "__main__":
    model_path = "./best.pt"
    
    example_input_data = torch.rand(1, 10, 71)
    
    if torch.cuda.is_available():
        example_data = example_input_data.cuda()
    
    inference = Inference(model_path)
    predictions = inference.predict(example_data)
    print(predictions)
    