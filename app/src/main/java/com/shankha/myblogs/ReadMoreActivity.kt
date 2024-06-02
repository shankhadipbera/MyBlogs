package com.shankha.myblogs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shankha.myblogs.databinding.ActivityReadMoreBinding
import com.shankha.myblogs.databinding.BlogItemBinding
import com.shankha.myblogs.model.BlogItemModel

class ReadMoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReadMoreBinding

    //S
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val currentUser = FirebaseAuth.getInstance().currentUser

    //E


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener{

            finish()
        }

        val blog =intent.getParcelableExtra<BlogItemModel>("blogItem")
        if(blog!=null){
            binding.blogHeadi.text=blog.heading
            binding.blogDesc.text=blog.blogText

            binding.authoeNametV.text=blog.userName
            binding.publishD.text=blog.datePublish
            Glide.with(this).load(blog.imageUrl).apply(RequestOptions.circleCropTransform()).into(binding.profileImage)
        }else{
            Toast.makeText(this,"Failed to load Data",Toast.LENGTH_SHORT).show()
        }

        //S
        val postId = blog?.postId
        val postLikeReference: DatabaseReference = databaseReference.child("blogs").child(postId.toString()).child("likes")

        val currentUserLike = currentUser?.uid?.let { uid ->
            postLikeReference.child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            binding.btnlike.setImageResource(R.drawable.thumb_up_fill)
                        } else {
                            binding.btnlike.setImageResource(R.drawable.thumb_up)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }

                })
        }

        binding.btnlike.setOnClickListener {
            if (currentUser != null) {
                handleLikeButtonClicked(postId, blog!!)
            } else {
                Toast.makeText(this, "Login First", Toast.LENGTH_SHORT).show()
            }
        }

        //E

        //S

        val userReference = databaseReference.child("Users").child(currentUser!!.uid)
        val postSaveReference = userReference.child("savePost").child(postId.toString())

        postSaveReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    binding.btnsave.setImageResource(R.drawable.bookmark)
                } else {
                    binding.btnsave.setImageResource(R.drawable.bookmark_border)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        binding.btnsave.setOnClickListener {
            if (currentUser != null) {
                handleSaveButtonClicked(postId, blog!!
                   // , binding
                )
            } else {
                Toast.makeText(this, "Login First", Toast.LENGTH_SHORT).show()
            }
        }


        //E

    }

    //S

    private fun handleLikeButtonClicked(postId: String?, blogItemModel: BlogItemModel, ) {

        val userReference = databaseReference.child("Users").child(currentUser!!.uid)
        val postIdlikeReference = databaseReference.child("blogs").child(postId!!).child("likes")

        postIdlikeReference.child(currentUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        userReference.child("likes").child(postId).removeValue()
                            .addOnSuccessListener {
                                postIdlikeReference.child(currentUser.uid).removeValue()
                                blogItemModel.likedBy?.remove(currentUser.uid)
                                updateImageButtonImage(false)

                                val newLikeCount = blogItemModel.likeCount - 1
                                blogItemModel.likeCount = newLikeCount
                                databaseReference.child("blogs").child(postId).child("likeCount")
                                    .setValue(newLikeCount)

                              //  notifyDataSetChanged()
                            }
                            .addOnFailureListener { e ->
                                Log.e("LikedClicked", "onDataChange:Failed to unlike the blog $e")
                            }
                    } else {
                        userReference.child("likes").child(postId).setValue(true)
                            .addOnSuccessListener {
                                postIdlikeReference.child(currentUser.uid).setValue(true)
                                blogItemModel.likedBy?.add(currentUser.uid)
                                updateImageButtonImage(true)

                                val newLikeCount = blogItemModel.likeCount + 1
                                blogItemModel.likeCount = newLikeCount
                                databaseReference.child("blogs").child(postId).child("likeCount")
                                    .setValue(newLikeCount)

                              //  notifyDataSetChanged()

                            }
                            .addOnFailureListener { e ->
                                Log.e("LikedClicked", "onDataChange:Failed to Like the blog $e")

                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }


    private fun updateImageButtonImage(liked: Boolean) {
        if (liked) {
            binding.btnlike.setImageResource(R.drawable.thumb_up_fill)
        } else {
            binding.btnlike.setImageResource(R.drawable.thumb_up)
        }

    }

    //E

    //S
    private fun handleSaveButtonClicked(
        postId: String?,
        blogItemModel: BlogItemModel,
        //binding: BlogItemBinding
    ) {
        val userReference = databaseReference.child("Users").child(currentUser!!.uid)
        userReference.child("savePost").child(postId!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        userReference.child("savePost").child(postId).removeValue()
                            .addOnSuccessListener {
                               // val clickedBlogItem = items.find { it.postId == postId }
                               // clickedBlogItem?.isSaved = false
                                //notifyDataSetChanged()

                                val context = binding.root.context
                                Toast.makeText(context, "Blog Unsaved!!", Toast.LENGTH_SHORT).show()

                            }.addOnFailureListener {
                                val context = binding.root.context
                                Toast.makeText(context, "Failed to Unsaved!!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        binding.btnsave.setImageResource(R.drawable.bookmark_border)
                    } else {
                        userReference.child("savePost").child(postId).setValue(true)
                            .addOnSuccessListener {
                                //val clickedBlogItem = items.find { it.postId == postId }
                               // clickedBlogItem?.isSaved = true
                               // notifyDataSetChanged()

                                val context = binding.root.context
                                Toast.makeText(context, "Blog Saved!!", Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener {
                                val context = binding.root.context
                                Toast.makeText(context, "Failed to Saved!!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        binding.btnsave.setImageResource(R.drawable.bookmark)
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

    }
    //E
}