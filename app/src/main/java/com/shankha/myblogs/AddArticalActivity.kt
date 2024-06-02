package com.shankha.myblogs


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shankha.myblogs.databinding.ActivityAddArticalBinding
import com.shankha.myblogs.model.BlogItemModel
import com.shankha.myblogs.model.Usersdata
import java.text.SimpleDateFormat
import java.util.Date

class AddArticalActivity : AppCompatActivity() {
    private val binding: ActivityAddArticalBinding by lazy {
        ActivityAddArticalBinding.inflate(layoutInflater)
    }
    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("blogs")
    private val userReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("Users")
    private val auth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }


        binding.saveblogBtn.setOnClickListener {
            val title: String = binding.blogT.editText?.text.toString().trim()
            val description: String = binding.blogD.editText?.text.toString().trim()

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Fill all the Details", Toast.LENGTH_SHORT).show()
            }
            val user: FirebaseUser? = auth.currentUser

            if (user != null) {
                val userId = user.uid
                val name = user.displayName ?: "Anonymous"
                val imageUrl = user.photoUrl ?: ""

                userReference.child(userId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userData = snapshot.getValue(Usersdata::class.java)

                            if (userData != null) {
                                val userNameFromDB = userData.name
                                val userImageFromDB = userData.profileImage
                                val currentDate: String =

                                    SimpleDateFormat("yyyy-MM-dd").format(Date())

                                val blogItem = BlogItemModel(
                                    title,
                                    userNameFromDB,
                                    currentDate,
                                    userId,
                                    description,
                                    0,
                                    userImageFromDB
                                )

                                val key:String? = databaseReference.push().key
                                if (key != null) {

                                    blogItem.postId= key

                                    val blogReference = databaseReference.child(key)
                                    blogReference.setValue(blogItem).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            Toast.makeText(
                                                this@AddArticalActivity,
                                                "Uploded Sucessfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            finish()
                                        } else {
                                            Toast.makeText(
                                                this@AddArticalActivity,
                                                "Failed to Uploded",
                                                Toast.LENGTH_SHORT
                                            ).show()
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
    }
}