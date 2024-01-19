package com.example.myapplication.ui.theme

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.R

class SocialLoginActivity : AppCompatActivity() {

//    val callbackManager = create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        FacebookSdk.sdkInitialize(this)
        setContentView(R.layout.activity_social_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        val loginButton = findViewById<LoginButton>(R.id.facebook_login_button)
//        loginButton.permissions = mutableListOf("email", "public_profile")
//        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
//            override fun onCancel() {
//
//            }
//
//            override fun onError(error: FacebookException) {
//
//            }
//
//            override fun onSuccess(result: LoginResult) {
//
//            }
//
//        })
//
//        val configuration = SignInWithAppleConfiguration(
//            clientId = "com.your.client.id.here",
//            redirectUri = "https://your-redirect-uri.com/callback",
//            scope = "email"
//        )
//
//        val signInWithAppleButton = findViewById<SignInWithAppleButton>(R.id.apple_sign_in_button)
//        signInWithAppleButton.setUpSignInWithAppleOnClick(supportFragmentManager, configuration) { result ->
//            when (result) {
//                is SignInWithAppleResult.Success -> {
//                    // Handle success
//                }
//                is SignInWithAppleResult.Failure -> {
//                    // Handle failure
//                }
//                is SignInWithAppleResult.Cancel -> {
//                    // Handle user cancel
//                }
//            }
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}