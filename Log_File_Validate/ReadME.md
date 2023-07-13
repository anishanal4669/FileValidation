import yaml

def get_true_keys_from_yaml(file_path):
    true_keys = []
    
    with open(file_path, 'r') as file:
        data = yaml.safe_load(file)
        
        if 'checks' in data:
            checks = data['checks']
            
            for key, value in checks.items():
                if isinstance(value, bool) and value:
                    true_keys.append(key)
    
    return true_keys

# Usage example
file_path = 'config.yaml'
keys = get_true_keys_from_yaml(file_path)
keys_str = ', '.join(keys)

print("Keys with true values:", keys_str)
