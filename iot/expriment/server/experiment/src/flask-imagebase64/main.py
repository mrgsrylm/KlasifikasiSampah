from flask import Flask, request, jsonify
import os
import base64
from datetime import datetime

app = Flask(__name__)

UPLOAD_FOLDER = 'uploads'
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg', 'gif'}

app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

def get_unique_filename(filename):
    timestamp = datetime.now().strftime("%Y%m%d%H%M%S")
    basename, extension = os.path.splitext(filename)
    return f"{timestamp}{extension}"

def save_base64_to_file(base64_data, filename):
    try:
        binary_data = base64.b64decode(base64_data)
        filepath = os.path.join(app.config['UPLOAD_FOLDER'], filename)
        with open(filepath, 'wb') as f:
            f.write(binary_data)
        return True, filepath
    except Exception as e:
        return False, str(e)

@app.route("/test", methods=['POST'])
def test():
    if 'image' not in request.json:
        return jsonify({'error': 'No image part'})
    
    response = request.json['image']
    
    return jsonify({'message': response})
    

@app.route('/upload', methods=['POST'])
def upload_file():
    if 'image' not in request.json:
        return jsonify({'error': 'No image part'})

    image_base64 = request.json['image']

    filename = get_unique_filename('uploaded_image.jpg')

    success, filepath = save_base64_to_file(image_base64, filename)

    if success:
        return jsonify({'message': 'Image successfully uploaded', 'filename': filename, 'filepath': filepath})
    else:
        return jsonify({'error': 'Failed to save image'})

if __name__ == '__main__':
    if not os.path.exists(UPLOAD_FOLDER):
        os.makedirs(UPLOAD_FOLDER)
    app.run(debug=True)
