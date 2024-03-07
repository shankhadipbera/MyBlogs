package com.shankha.myblogs.register

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.UserData
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.shankha.myblogs.MainActivity
import com.shankha.myblogs.R
import com.shankha.myblogs.databinding.ActivitySignInUpBinding
import com.shankha.myblogs.model.Usersdata

class SignInUpActivity : AppCompatActivity() {
    private val binding: ActivitySignInUpBinding by lazy {
        ActivitySignInUpBinding.inflate(
            layoutInflater
        )
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        val action: String? = intent.getStringExtra("action")
        if (action == "Login") {
            binding.btnLogin.visibility = View.VISIBLE

            binding.loginEmail.visibility = View.VISIBLE
            binding.loginPassword.visibility = View.VISIBLE
            binding.btnRegister.isEnabled = false
            binding.btnRegister.alpha = 0.5f
            binding.RegEmail.visibility = View.GONE
            binding.RegPassword.visibility = View.GONE
            binding.cardView.visibility = View.GONE
            binding.userName.visibility = View.GONE
            binding.btnRegister.background = resources.getDrawable(R.drawable.login_fill)

            binding.btnLogin.setOnClickListener {
                val Lemail = binding.loginEmail.text.toString()
                val Lpassword = binding.loginPassword.text.toString()
                if (Lemail.isEmpty() || Lpassword.isEmpty()) {
                    Toast.makeText(this, "Fill all the details", Toast.LENGTH_SHORT).show()
                } else {
                    auth.signInWithEmailAndPassword(Lemail, Lpassword)
                        .addOnCompleteListener { Task ->
                            if (Task.isSuccessful) {
                                Toast.makeText(this, "Login Sucessfull 😊", Toast.LENGTH_SHORT)
                                    .show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Failed , Enter Correct Details",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }


            }

        } else if (action == "Register") {
            binding.btnLogin.isEnabled = false
            binding.btnLogin.alpha = 0.5f
            binding.btnRegister.background = resources.getDrawable(R.drawable.register_fill)

            binding.btnRegister.setOnClickListener {
                val Rname = binding.userName.text.toString()
                val Remail = binding.RegEmail.text.toString()
                val Rpassword = binding.RegPassword.text.toString()

                val defaultImageResourceId = R.drawable.ic_launcher_background

                if (Rname.isEmpty() || Remail.isEmpty() || Rpassword.isEmpty()) {
                    Toast.makeText(this, "Enter all the fills", Toast.LENGTH_SHORT).show()
                } else {
                    auth.createUserWithEmailAndPassword(Remail, Rpassword)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                auth.signOut()
                                user?.let {
                                    val userReference = database.getReference("Users")
                                    val userId: String = user.uid
                                    val userData = Usersdata(Rname, Remail,Rpassword)

                                    userReference.child(userId).setValue(userData)


                                  // crash when try to upload without picture

                                    val storageReference =
                                        storage.reference.child("profile_image/$userId.jpg")

                                    /*
                                    storageReference.putFile(imageUri!!)
                                        .addOnCompleteListener { task ->
                                            if(task.isSuccessful){
                                                storageReference.downloadUrl.addOnCompleteListener { imageUri ->
                                                    if(imageUri.isSuccessful){
                                                        val imagUrl = imageUri.result.toString()

                                                        userReference.child(userId).child("profileImage")
                                                            .setValue(imagUrl)
                                                    }

                                                }
                                            }

                                        }
                                        */

                                    if (imageUri != null) {
                                        storageReference.putFile(imageUri!!)
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    storageReference.downloadUrl.addOnCompleteListener { imageUri ->
                                                        if (imageUri.isSuccessful) {
                                                            val imageUrl = imageUri.result.toString()

                                                            userReference.child(userId).child("profileImage")
                                                                .setValue(imageUrl)
                                                        }
                                                    }
                                                } else {
                                                    // Handle the case when image upload fails
                                                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                    } else {

                                        val defaultImageResourceId = R.drawable.pericon
                                        val defaultImageUri = Uri.parse("android.resource://$packageName/$defaultImageResourceId")

                                        // Open an InputStream for the default image
                                        val defaultImageInputStream = contentResolver.openInputStream(defaultImageUri)

                                        // Upload the default image to Firebase Storage
                                        val defaultImageTask = defaultImageInputStream?.let {
                                            storageReference.putStream(it)
                                        }

                                        // Handle the default image upload task
                                        defaultImageTask?.addOnCompleteListener { defaultImageTask ->
                                            if (defaultImageTask.isSuccessful) {
                                                // Get the download URL for the default image
                                                storageReference.downloadUrl.addOnCompleteListener { defaultImageUriTask ->
                                                    if (defaultImageUriTask.isSuccessful) {
                                                        val defaultImageUrl = defaultImageUriTask.result.toString()

                                                        // Set the default image URL in Firebase Realtime Database
                                                        userReference.child(userId).child("profileImage")
                                                            .setValue(defaultImageUrl)

                                                        // Continue with the rest of your code for user registration
                                                    }
                                                }
                                            } else {
                                                // Handle the case when default image upload fails
                                                Toast.makeText(this, "Failed to upload default image", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }


                                    Toast.makeText(this, "Sucess", Toast.LENGTH_LONG).show()
                                    startActivity(Intent(this, WelcomeActivity::class.java))
                                    finish()
                                }

                            } else {
                                Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                            }
                        }
                }
            }

        }




        binding.cardView.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            Glide.with(this).load(imageUri).apply(RequestOptions.circleCropTransform())
                .into(binding.profilepic)
        }

    }
}