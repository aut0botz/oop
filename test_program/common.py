url = "http://ssczlc.mixyun.com/apiproxy/api"
wss_url = "ws://ssczlc.mixyun.com/apiproxy/ws/api"

headers = {"Content-Type": "application/json;charset=UTF-8"}
auth_data = {
    "action": "get_access_token",
    "actionid": "",
    "app_id": "SGN4125406500001",
    "app_secret": "92ed09a9-c55c-fc14-6989-31598051d783"
}
# feature_names = ['S6_001', 'S6_002', 'S6_003', 'S6_004', 'S6_005', 'S6_006', 'S6_007', 'S6_008', 'S6_009', 'S6_010', 'S6_011', 'S6_012', 'S6_013', 'S6_014', 'S6_015', 'S6_016', 'S6_017', 'S6_018', 'S6_019', 'S6_020', 'S6_021', 'S6_022', 'S6_023', 'S6_024', 'S6_025', 'S6_026', 'S6_028', 'S6_029', 'S6_034', 'S6_035', 'S6_037', 'S6_038', 'S6_039', 'S6_041', 'S6_042', 'S6_043', 'S6_044', 'S6_046', 'S6_047', 'S6_048', 'S6_049', 'S6_050', 'S6_051', 'S6_052', 'S6_053', 'S6_055', 'S6_056', 'S6_058', 'S6_059', 'S6_066', 'S6_067', 'S6_068', 'S6_072', 'S6_073', 'S6_074', 'S6_076', 'S6_077', 'S6_079', 'S6_080', 'S6_081', 'S6_083', 'S6_084', 'S6_085', 'S6_092', 'S6_093', 'S6_094', 'S6_095', 'S6_096', 'S6_099', 'S6_100', 'S6_103']
feature_names = [l.strip() for l in open("../src/selected_features.txt") if l.strip()]
obj_id_list = ['OBJ3840268503651', 'OBJ3842329903658', 'OBJ3011896207975', 'OBJ3014916307977', 'OBJ3845566603645', 'OBJ3843760703653', 'OBJ3845610003655']

model_path = "/home/qhdev/p3pinot/electric/out-15-26feature/best.pt"
input_size = 26  # Number of features
hidden_size = 32  # Number of features in hidden state
num_layers = 2  # Number of stacked rnn layers
num_classes = 2  # Number of output classes 