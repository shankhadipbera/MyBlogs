package com.shankha.myblogs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.shankha.myblogs.databinding.ActivityEditBlogBinding
import com.shankha.myblogs.model.BlogItemModel

class EditBlogActivity : AppCompatActivity() {
    private val binding: ActivityEditBlogBinding by lazy {
        ActivityEditBlogBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val blogItemModel= intent.getParcelableExtra<BlogItemModel>("blogItem")

        binding.blogT.editText?.setText(blogItemModel?.heading)
        binding.blogD.editText?.setText(blogItemModel?.blogText)

        binding.back.setOnClickListener {
            finish()
        }

        binding.saveblogBtn.setOnClickListener {
            val updatedTitle= binding.blogT.editText?.text.toString().trim()
            val updatedDescription=binding.blogD.editText?.text.toString().trim()
            if(updatedTitle.isEmpty()||updatedDescription.isEmpty()){
                Toast.makeText(this,"Please Fill All The Details",Toast.LENGTH_SHORT).show()
            }else{
                blogItemModel?.heading=updatedTitle
                blogItemModel?.blogText=updatedDescription
                if(blogItemModel!=null){
                    updateDataInFirebase(blogItemModel)
                }
            }


        }

    }

    private fun updateDataInFirebase(blogItemModel: BlogItemModel) {
        val databaseReference=FirebaseDatabase.getInstance().getReference("blogs")
        val postId=blogItemModel.postId
        databaseReference.child(postId.toString()).setValue(blogItemModel).addOnSuccessListener {
            Toast.makeText(this,"Blog Updated Successfully",Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(this,"Failed To Updated  Details",Toast.LENGTH_SHORT).show()
        }
    }
}