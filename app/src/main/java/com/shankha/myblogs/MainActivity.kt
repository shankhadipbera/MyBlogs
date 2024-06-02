package com.shankha.myblogs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shankha.myblogs.adapter.BlogAdapter
import com.shankha.myblogs.databinding.ActivityMainBinding
import com.shankha.myblogs.model.BlogItemModel
import com.shankha.myblogs.register.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private val blogItems = mutableListOf<BlogItemModel>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.searchButton.setOnClickListener {
            startActivity(Intent(this,SearchActivity::class.java))
        }

        binding.saveArtical.setOnClickListener {
            startActivity(Intent(this, SaveArticalActivity::class.java))
        }
        binding.userPic.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }



        auth = FirebaseAuth.getInstance()

        databaseReference = FirebaseDatabase.getInstance().reference.child("blogs")

        val userid = auth.currentUser?.uid
        if (userid != null) {
            loadUserProfileImage(userid)
        }
        val blogAdapter = BlogAdapter(blogItems)
        println("BlogItems size: ${blogItems.size}")
        val recyclerView = binding.blogRecycleV
        recyclerView.adapter = blogAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                blogItems.clear()
                for (Snapshot in snapshot.children) {
                    val blogitem = Snapshot.getValue(BlogItemModel::class.java)
                    if (blogitem != null) {
                        blogItems.add(blogitem)
                    }

                }
                blogItems.reverse()
                blogAdapter.notifyDataSetChanged()


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Blog loading failed", Toast.LENGTH_SHORT).show()
            }

        })


        binding.btnAddArtical.setOnClickListener {
            startActivity(Intent(this, AddArticalActivity::class.java))

        }



    }

    private fun loadUserProfileImage(userid: String) {
        val userReference = FirebaseDatabase.getInstance().reference.child("Users").child(userid)
        userReference.child("profileImage").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImageUrl = snapshot.getValue(String::class.java)
                if (profileImageUrl != null) {
                    Glide.with(this@MainActivity).load(profileImageUrl).into(binding.userPic)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error Loding Profile Image", Toast.LENGTH_SHORT)
                    .show()
            }

        })

    }
}