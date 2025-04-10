package com.surendramaran.yolov9tflite

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        //map component
        val btnLogin: Button = findViewById(R.id.btnLogin)
        val btnSignup: Button = findViewById(R.id.btnSignup)
        //end map component

        //app logic
        btnLogin.setOnClickListener {

        }

        btnSignup.setOnClickListener {
            intent = Intent(this, regis::class.java)
            startActivity(intent)
        }
        //end app logic


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}