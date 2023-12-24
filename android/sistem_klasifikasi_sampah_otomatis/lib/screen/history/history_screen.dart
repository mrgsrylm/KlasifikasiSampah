import 'package:flutter/material.dart';

class HistoryScreen extends StatelessWidget {
  const HistoryScreen({super.key});

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
        title: Text('History', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 20.0),),
        centerTitle: true,
        actions: [
          IconButton(
            icon: Icon(Icons.calendar_month, size: 25.0,),
            onPressed: () {
              // Action when notification icon is pressed
            },
          ),
        ],
      ),
      body: SizedBox(height: 100),
    );
  }
  
}