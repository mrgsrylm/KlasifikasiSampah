import 'dart:convert';
import 'dart:io';

class ImageConverters {
  static final ImageConverters constants = ImageConverters._();
  factory ImageConverters() => constants;
  ImageConverters._();
  dynamic convertToBase64(File file) {
    List<int> imageBytes = file.readAsBytesSync();
    String base64Image = base64Encode(imageBytes);

    return 'data:image/jpeg;base64,$base64Image';
  }
  dynamic decodeBase64(String encoded) {
    String decoded = utf8.decode(base64Url.decode(encoded));
    return decoded;
  }
}