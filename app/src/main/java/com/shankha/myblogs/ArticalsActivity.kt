package com.shankha.myblogs

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shankha.myblogs.adapter.ArticalAdapter
import com.shankha.myblogs.databinding.ActivityArticalsBinding
import com.shankha.myblogs.model.BlogItemModel

class ArticalsActivity : AppCompatActivity() {
    private val binding : ActivityArticalsBinding by lazy {
        ActivityArticalsBinding.inflate(layoutInflater)
    }
    private lateinit var databaseReference: DatabaseReference
    private  val auth= FirebaseAuth.getInstance()
    private lateinit var articalAdapter: ArticalAdapter
    private val EDIT_BLOG_REQUEST_CODE=100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }
        val currentUser= auth.currentUser?.uid
        val recyclerView= binding.recycleView
        recyclerView.layoutManager=LinearLayoutManager(this)

        if(currentUser!=null){
            articalAdapter= ArticalAdapter(this, emptyList(),object :ArticalAdapter.OnItemClickListener{
                override fun onReadMoreClick(blogItem: BlogItemModel) {
                    val intent=Intent(this@ArticalsActivity,ReadMoreActivity::class.java)
                    intent.putExtra("blogItem",blogItem)
                    startActivity(intent)

                }

                override fun onEditClick(blogItem: BlogItemModel) {
                    val intent=Intent(this@ArticalsActivity,EditBlogActivity::class.java)
                    intent.putExtra("blogItem",blogItem)
                    startActivityForResult(intent,EDIT_BLOG_REQUEST_CODE)
                }

                override fun onDeleteClick(blogItem: BlogItemModel) {
                    deleteBlogPost(blogItem)
                }
            })
        }



        recyclerView.adapter=articalAdapter

        databaseReference=FirebaseDatabase.getInstance().reference.child("blogs")
        databaseReference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val blogList=ArrayList<BlogItemModel>()
                for(postSnapshot in snapshot.children){
                    val blogSaved= postSnapshot.getValue(BlogItemModel::class.java)
                    if(blogSaved!=null && currentUser==blogSaved.userId){
                        blogList.add(blogSaved)
                    }

                }
                articalAdapter.setData(blogList)
            }

            override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ArticalsActivity,"Error Loading Data",Toast.LENGTH_SHORT).show()
            }
        })


    }

    private fun deleteBlogPost(blogItem: BlogItemModel) {
        val postId= blogItem.postId
        val blogPostReference= databaseReference.child(postId!!)
        blogPostReference.removeValue().addOnSuccessListener {
            Toast.makeText(this, " Blog Post Deleted Sucessfully",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, " Failed To Delete Blog Post",Toast.LENGTH_SHORT).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==EDIT_BLOG_REQUEST_CODE && resultCode==Activity.RESULT_OK){
            
        }
    }
}