import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:sistem_klasifikasi_sampah_otomatis/screen/history/history_screen.dart';
import 'package:sistem_klasifikasi_sampah_otomatis/screen/home/home_screen.dart';

class Routes {
  static const String root = '/';
  static const String history = '/history';

  static Route<dynamic> onGenerateRoute(RouteSettings settings) {
    switch (settings.name) {
      case root:
        return MaterialPageRoute(builder: (_) => HomeScreen());
      case history:
        return MaterialPageRoute(builder: (_) => HistoryScreen());
      default:
        return MaterialPageRoute(builder: (_) => HomeScreen());
    }
  }
}