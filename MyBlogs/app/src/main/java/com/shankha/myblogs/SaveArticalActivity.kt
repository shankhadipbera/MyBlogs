package com.shankha.myblogs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shankha.myblogs.adapter.BlogAdapter
import com.shankha.myblogs.databinding.ActivitySaveArticalBinding
import com.shankha.myblogs.model.BlogItemModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SaveArticalActivity : AppCompatActivity() {
    private val binding: ActivitySaveArticalBinding by lazy {
        ActivitySaveArticalBinding.inflate(layoutInflater)
    }
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val saveBlogArtical = mutableListOf<BlogItemModel>()
    private lateinit var blogAdapter: BlogAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnback.setOnClickListener {
            finish()
        }

        blogAdapter = BlogAdapter(saveBlogArtical.filter { it.isSaved }.toMutableList())
        binding.recycleV.adapter = blogAdapter
        binding.recycleV.layoutManager = LinearLayoutManager(this)

        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userReference =
                FirebaseDatabase.getInstance().getReference("Users").child(userId).child("savePost")
            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val postId: String? = postSnapshot.key
                        val isSaved = postSnapshot.value as Boolean
                        if (postId != null && isSaved) {
                            CoroutineScope(Dispatchers.IO).launch {
                                val blogItem = fetchBlogItem(postId)
                                if (blogItem != null) {
                                    saveBlogArtical.add(blogItem)
                                    launch(Dispatchers.Main) {
                                        blogAdapter.updateData(saveBlogArtical)
                                    }
                                }
                            }
                        }

                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }


    }

    private suspend fun fetchBlogItem(postId: String): BlogItemModel? {
        val blogReference = FirebaseDatabase.getInstance().getReference("blogs")
        return try {
            val dataSnapshot = blogReference.child(postId).get().await()
            val blogData = dataSnapshot.getValue(BlogItemModel::class.java)
            blogData
        } catch (e: Exception) {
            null
        }
    }
}