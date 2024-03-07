package com.shankha.myblogs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shankha.myblogs.databinding.ActivityProfileBinding
import com.shankha.myblogs.register.WelcomeActivity

class ProfileActivity : AppCompatActivity() {
    private val binding :ActivityProfileBinding by lazy{
        ActivityProfileBinding.inflate(layoutInflater)
    }
    private lateinit var  databaseReference: DatabaseReference
    private lateinit var auth :FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backbtn.setOnClickListener {
            finish()
        }
        binding.newArticals.setOnClickListener{
            startActivity(Intent(this,AddArticalActivity::class.java))
        }

        binding.logout.setOnClickListener{
            auth.signOut()
            startActivity(Intent(this,WelcomeActivity::class.java))
            finish()
        }

        binding.myarticals.setOnClickListener{
            startActivity(Intent(this,ArticalsActivity::class.java))
        }

        auth=FirebaseAuth.getInstance()
        databaseReference=FirebaseDatabase.getInstance().reference.child("Users")

        val userId =auth.currentUser?.uid
        if(userId!=null){
            loadUserProfileData(userId)
        }

    }

    private fun loadUserProfileData( userId: String) {
            val userReference =databaseReference.child(userId)
        userReference.child("profileImage").addValueEventListener(object :ValueEventListener{
                   override fun onDataChange(snapshot: DataSnapshot) {
                       val profileImageUrl= snapshot.getValue(String::class.java)
                       if(profileImageUrl!=null){
                           Glide.with(this@ProfileActivity).load(profileImageUrl).into(binding.userProfile)
                       }
                   }

                   override fun onCancelled(error: DatabaseError) {
                      Toast.makeText(this@ProfileActivity,"Failed to load Profile Image",Toast.LENGTH_SHORT).show()
                   }
               })

        userReference.child("name").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName= snapshot.getValue(String::class.java)
                if(userName!=null){
                    binding.userNames.text=userName
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}