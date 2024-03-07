package com.shankha.myblogs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.shankha.myblogs.register.SignInUpActivity
import com.shankha.myblogs.register.WelcomeActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        Handler(Looper.getMainLooper()).postDelayed({startActivity(Intent(this, WelcomeActivity::class.java))
                                                    finish();},3000)
    }



}


