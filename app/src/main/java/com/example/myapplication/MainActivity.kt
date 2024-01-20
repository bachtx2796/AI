package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.ui.theme.LocationActivity
import com.example.myapplication.ui.theme.SocialLoginActivity
import com.example.myapplication.ui.theme.UploadActivity

class MainActivity : ComponentActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_act)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val getLocation = findViewById<TextView>(R.id.get_location)
        getLocation.setOnClickListener {
            // Check location permission
            val intent = Intent(
                this,
                LocationActivity::class.java
            );
            startActivity(intent)
        }

        findViewById<TextView>(R.id.upload).setOnClickListener {
            val intent = Intent(
                this,
                UploadActivity::class.java
            )
            startActivity(intent)
        }

        findViewById<TextView>(R.id.socialLogin).setOnClickListener {
            val intent = Intent(
                this,
                SocialLoginActivity::class.java
            )
            startActivity(intent)
        }
    }
}
