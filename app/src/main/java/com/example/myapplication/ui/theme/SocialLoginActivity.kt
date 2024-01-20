package com.example.myapplication.ui.theme

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.R
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleConfiguration
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleResult
import com.willowtreeapps.signinwithapplebutton.view.SignInWithAppleButton

class SocialLoginActivity : AppCompatActivity() {

//    val callbackManager = create()

    private lateinit var callbackManager: CallbackManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        FacebookSdk.sdkInitialize(this)
        setContentView(R.layout.activity_social_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val loginButton = findViewById<LoginButton>(R.id.facebook_login_button)
        FacebookSdk.sdkInitialize(applicationContext)

        // Initialize CallbackManager
        callbackManager = CallbackManager.Factory.create()

        // Set click listener for Facebook login button
        loginButton.setOnClickListener {
            // Request Facebook login permissions
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))

            // Register callback for Facebook login result
            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    // Handle successful Facebook login
                    val accessToken = result.accessToken
                    // TODO: Perform necessary actions with the access token
                }

                override fun onCancel() {
                    // Handle canceled Facebook login
                }

                override fun onError(error: FacebookException) {
                    // Handle error in Facebook login
                }
            })
        }

        val configuration = SignInWithAppleConfiguration(
            clientId = "com.your.client.id.here",
            redirectUri = "https://your-redirect-uri.com/callback",
            scope = "email"
        )
//
        val signInWithAppleButton = findViewById<SignInWithAppleButton>(R.id.apple_sign_in_button)
        signInWithAppleButton.setUpSignInWithAppleOnClick(supportFragmentManager, configuration) { result ->
            when (result) {
                is SignInWithAppleResult.Success -> {
                    // Handle success
                }
                is SignInWithAppleResult.Failure -> {
                    // Handle failure
                }
                is SignInWithAppleResult.Cancel -> {
                    // Handle user cancel
                }
            }
        }

        // Configure Google Sign-In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build GoogleApiClient
        var googleApiClient: GoogleApiClient
        googleApiClient = GoogleApiClient.Builder(this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
        val googleSignInButton = findViewById<LoginButton>(R.id.google_sign_in_button)
        googleSignInButton.setOnClickListener {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                // Handle successful Google login
                val account = result.signInAccount
                // TODO: Perform necessary actions with the account information
            } else {
                // Handle error in Google login
            }
        } else {

        }
    }

    companion object {
        private const val RC_GOOGLE_SIGN_IN = 9001
    }
}