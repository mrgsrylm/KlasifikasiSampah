import 'dart:convert';
import 'dart:io';
import 'dart:typed_data';
import 'dart:ui';

import 'package:flutter/cupertino.dart';

class ImageConverters {
  static final ImageConverters constants = ImageConverters._();

  factory ImageConverters() => constants;

  ImageConverters._();

  dynamic convertToBase64(File file) {
    List<int> imageBytes = file.readAsBytesSync();
    String base64Image = base64Encode(imageBytes);

    return 'data:image/jpeg;base64,$base64Image';
  }

  dynamic decodeBase64(String base64String) {
    // Remove data URI prefix
    final commaIndex = base64String.indexOf(',');
    if (commaIndex != -1) {
      base64String = base64String.substring(commaIndex + 1);
    }
    // URL decode if needed
    String decodedString = Uri.decodeComponent(base64String);
    try {
      // Decode Base64
      Uint8List decodedBytes = base64.decode(decodedString);
      return decodedBytes;
    } catch (e) {
      print("Error decoding Base64: $e");
      // Return null or handle the error as needed
      return null;
    }
  }
}
