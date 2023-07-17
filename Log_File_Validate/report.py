import requests
import json

# GitLab API endpoint and access token
gitlab_api_url = 'https://gitlab.com/api/v4'
access_token = 'glpat-ZHqBCSt7GxFNvzx_o3xv'

# Project and merge request information
project_id = '45195388'
merge_request_iid = '1'

# # Path to the JSON file containing report data
# json_file_path = 'test.json'

# # Load report data from JSON file
# def load_report_data(file_path):
#     with open(file_path, 'r') as file:
#         report_data = json.load(file)
#     return report_data

# # Create a comment with the report data
# def create_comment(report_data):
#     headers = {'PRIVATE-TOKEN': access_token}
#     url = f'{gitlab_api_url}/projects/{project_id}/merge_requests/{merge_request_iid}/notes'
#     data = {'body': json.dumps(report_data, indent=2)}

#     response = requests.post(url, headers=headers, data=data)
#     response.raise_for_status()

#     print('Report comment created successfully.')

# # Main script execution
# def main():
#     try:
#         report_data = load_report_data(json_file_path)
#         create_comment(report_data)
#     except FileNotFoundError:
#         print(f'JSON file not found: {json_file_path}')
#     except json.JSONDecodeError:
#         print(f'Invalid JSON file: {json_file_path}')
#     except requests.exceptions.HTTPError as e:
#         print(f'Error creating report comment: {e}')
#         return

# if __name__ == '__main__':
#     main()


# Path to the CI report file
ci_report_file = 'kube_score_ci.txt'

# # Read CI report from file
# def read_ci_report(file_path):
#     with open(file_path, 'r') as file:
#         ci_report = file.read()
#     return ci_report

# # Create a comment with the CI report
# def create_comment(ci_report):
#     headers = {'PRIVATE-TOKEN': access_token}
#     url = f'{gitlab_api_url}/projects/{project_id}/merge_requests/{merge_request_iid}/notes'
#     data = {'body': ci_report}

#     response = requests.post(url, headers=headers, data=data)
#     response.raise_for_status()

#     print('CI report comment created successfully.')

# # Main script execution
# def main():
#     try:
#         ci_report = read_ci_report(ci_report_file)
#         create_comment(ci_report)
#     except FileNotFoundError:
#         print(f'CI report file not found: {ci_report_file}')
#     except requests.exceptions.HTTPError as e:
#         print(f'Error creating CI report comment: {e}')
#         return

# if __name__ == '__main__':
#     main()

def generate_gitlab_report(input_file, output_file):
    # Read the content from the text file
    with open(input_file, 'r') as file:
        content = file.read()

    # Generate GitLab report in Markdown format
    markdown_report = f"# GitLab Report\n\n{content}"

    # Write the Markdown report to an output file
    with open(output_file, 'w') as file:
        file.write(markdown_report)

# Usage example
input_file = 'kube_score_ci.txt'  # Replace with the path to your input text file
output_file = 'gitlab-report.md'  # Replace with the desired output file path

generate_gitlab_report(input_file, output_file)
