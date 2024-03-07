package com.shankha.myblogs.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.shankha.myblogs.MainActivity

import com.shankha.myblogs.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private val binding : ActivityWelcomeBinding by lazy { ActivityWelcomeBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth= FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            val intent= Intent(this,SignInUpActivity::class.java)
            intent.putExtra("action","Login")
            startActivity(intent)

        }

        binding.btnRegister.setOnClickListener {
            val intent= Intent(this,SignInUpActivity::class.java)
            intent.putExtra("action","Register")
            startActivity(intent)

        }

    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}