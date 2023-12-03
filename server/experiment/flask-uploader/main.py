import argparse
import requests

def post_image(file_path, url):
    try:
        with open(file_path, "rb") as image_file:
            response = requests.post(url, files={"file": image_file})

        # Print the response body for both success and failure
        print("Response body:", response.text)

        # Check the response status
        if response.status_code == 200:
            print("Image successfully uploaded.")
        else:
            print(f"Failed to upload image. Status code: {response.status_code}")

    except FileNotFoundError:
        print(f"File not found: {file_path}")
    except Exception as e:
        print(f"An error occurred: {e}")

def main():
    parser = argparse.ArgumentParser(description="Upload an image to a server.")
    parser.add_argument("--url", required=True, help="URL to upload the image to.")
    parser.add_argument("--file", required=True, help="Path to the image file.")

    args = parser.parse_args()

    post_image(args.file, args.url)

if __name__ == "__main__":
    main()
