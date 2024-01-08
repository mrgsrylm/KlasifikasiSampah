import 'package:flutter/material.dart';
import 'package:sistem_klasifikasi_sampah_otomatis/routes.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
          icon: Image.asset('assets/images/logo.png', height: 28.0,), // Replace 'assets/logo.png' with your logo image path
          onPressed: () {
            // Action when logo is pressed
          },
        ),
        title: Text('Home', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 20.0),),
        centerTitle: true,
        actions: [
          IconButton(
            icon: Icon(Icons.notifications, size: 25.0,),
            onPressed: () {
              // Action when notification icon is pressed
            },
          ),
        ],
      ),
      body: Column(
        children: [
          SizedBox(height: 100),
          TextButton(
              onPressed: () {
                Navigator.pushNamed(context, Routes.history);
              },
              child: Text("Go to history"))
        ],
      ),
    );
  }
  
}