import 'package:flutter/material.dart';
import 'package:sistem_klasifikasi_sampah_otomatis/routes.dart';

import 'screen/home/home_screen.dart';

void main() {
  runApp(const Application());
}

class Application extends StatelessWidget {
  const Application({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.blueAccent),
        useMaterial3: true,
      ),
      initialRoute: Routes.root,
      onGenerateRoute: Routes.onGenerateRoute,
    );
  }
}