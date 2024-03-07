package com.shankha.myblogs.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shankha.myblogs.R
import com.shankha.myblogs.ReadMoreActivity
import com.shankha.myblogs.databinding.BlogItemBinding
import com.shankha.myblogs.model.BlogItemModel

class BlogAdapter(private val items: MutableList<BlogItemModel>) :
    RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val currentUser = FirebaseAuth.getInstance().currentUser


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = BlogItemBinding.inflate(inflater, parent, false)
        return BlogViewHolder(binding)
    }

    override fun getItemCount(): Int {
         return items.size

    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
         val blogItem = items[position]
        holder.bind(blogItem)

    }


    inner class BlogViewHolder(private val binding: BlogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(blogItemModel: BlogItemModel) {

            val postId = blogItemModel.postId
            val context = binding.root.context

            binding.blogHeading.text = blogItemModel.heading
            binding.blogText.text = blogItemModel.blogText
            binding.authorName.text = blogItemModel.userName
            binding.date.text = blogItemModel.datePublish
            binding.likeNo.text = blogItemModel.likeCount.toString()
            Glide.with(binding.userPic.context).load(blogItemModel.imageUrl).into(binding.userPic)


            binding.root.setOnClickListener {
                val context: Context = binding.root.context
                val intent = Intent(context, ReadMoreActivity::class.java)
                intent.putExtra("blogItem", blogItemModel)
                context.startActivity(intent)
            }

            val postLikeReference: DatabaseReference =
                databaseReference.child("blogs").child(postId.toString())
                    .child("likes") // may occur error for to string
            val currentUserLike = currentUser?.uid?.let { uid ->
                postLikeReference.child(uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                binding.btnLike.setImageResource(R.drawable.thumb_up_fill)
                            } else {
                                binding.btnLike.setImageResource(R.drawable.thumb_up)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })
            }
            binding.btnLike.setOnClickListener {
                if (currentUser != null) {
                    handleLikeButtonClicked(postId, blogItemModel, binding)
                } else {
                    Toast.makeText(context, "Login First", Toast.LENGTH_SHORT).show()
                }
            }


            val userReference = databaseReference.child("Users").child(currentUser!!.uid)
            val postSaveReference = userReference.child("savePost").child(postId.toString())

            postSaveReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        binding.btnSave.setImageResource(R.drawable.bookmark)
                    } else {
                        binding.btnSave.setImageResource(R.drawable.bookmark_border)
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

            binding.btnSave.setOnClickListener {
                if (currentUser != null) {
                    handleSaveButtonClicked(postId, blogItemModel, binding)
                } else {
                    Toast.makeText(context, "Login First", Toast.LENGTH_SHORT).show()
                }
            }


        }

    }


    private fun handleLikeButtonClicked(
        postId: String?,
        blogItemModel: BlogItemModel,
        binding: BlogItemBinding
    ) {

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
                                updateImageButtonImage(binding, false)

                                val newLikeCount = blogItemModel.likeCount - 1
                                blogItemModel.likeCount = newLikeCount
                                databaseReference.child("blogs").child(postId).child("likeCount")
                                    .setValue(newLikeCount)

                                notifyDataSetChanged()
                            }
                            .addOnFailureListener { e ->
                                Log.e("LikedClicked", "onDataChange:Failed to unlike the blog $e")
                            }
                    } else {
                        userReference.child("likes").child(postId).setValue(true)
                            .addOnSuccessListener {
                                postIdlikeReference.child(currentUser.uid).setValue(true)
                                blogItemModel.likedBy?.add(currentUser.uid)
                                updateImageButtonImage(binding, true)

                                val newLikeCount = blogItemModel.likeCount + 1
                                blogItemModel.likeCount = newLikeCount
                                databaseReference.child("blogs").child(postId).child("likeCount")
                                    .setValue(newLikeCount)

                                notifyDataSetChanged()

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


    private fun updateImageButtonImage(binding: BlogItemBinding, liked: Boolean) {
        if (liked) {
            binding.btnLike.setImageResource(R.drawable.thumb_up)
        } else {
            binding.btnLike.setImageResource(R.drawable.thumb_up_fill)
        }

    }


    private fun handleSaveButtonClicked(
        postId: String?,
        blogItemModel: BlogItemModel,
        binding: BlogItemBinding
    ) {
        val userReference = databaseReference.child("Users").child(currentUser!!.uid)
        userReference.child("savePost").child(postId!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        userReference.child("savePost").child(postId).removeValue()
                            .addOnSuccessListener {
                                val clickedBlogItem = items.find { it.postId == postId }
                                clickedBlogItem?.isSaved = false
                                notifyDataSetChanged()

                                val context = binding.root.context
                                Toast.makeText(context, "Blog Unsaved!!", Toast.LENGTH_SHORT).show()

                            }.addOnFailureListener {
                                val context = binding.root.context
                                Toast.makeText(context, "Failed to Unsaved!!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        binding.btnSave.setImageResource(R.drawable.bookmark_border)
                    } else {
                        userReference.child("savePost").child(postId).setValue(true)
                            .addOnSuccessListener {
                                val clickedBlogItem = items.find { it.postId == postId }
                                clickedBlogItem?.isSaved = true
                                notifyDataSetChanged()

                                val context = binding.root.context
                                Toast.makeText(context, "Blog Saved!!", Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener {
                                val context = binding.root.context
                                Toast.makeText(context, "Failed to Saved!!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        binding.btnSave.setImageResource(R.drawable.bookmark)
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

    }

    fun updateData(saveBlogArtical: MutableList<BlogItemModel>) {
        items.clear()
        items.addAll(saveBlogArtical)
        notifyDataSetChanged()
    }








}