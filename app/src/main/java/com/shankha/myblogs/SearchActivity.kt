package com.shankha.myblogs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shankha.myblogs.adapter.SearchAdapter
import com.shankha.myblogs.databinding.ActivitySearchBinding
import com.shankha.myblogs.model.BlogItemModel

class SearchActivity : AppCompatActivity() {
    private val binding :ActivitySearchBinding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }

    private lateinit var databaseReference: DatabaseReference
    private lateinit var searchAdapter: SearchAdapter
    private val originalItems = mutableListOf<BlogItemModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        databaseReference = FirebaseDatabase.getInstance().reference.child("blogs")

        binding.imageButtonBack.setOnClickListener { finish() }

        searchAdapter = SearchAdapter(originalItems)
        binding.blogRecycleV.adapter = searchAdapter
        binding.blogRecycleV.layoutManager = LinearLayoutManager(this)


        binding.searchBlogItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchAdapter.filter(newText.orEmpty())
                return true
            }
        })

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                originalItems.clear()

                for (Snapshot in snapshot.children) {
                    val blogItem = Snapshot.getValue(BlogItemModel::class.java)
                    if (blogItem != null) {
                        originalItems.add(blogItem)
                    }
                }
                originalItems.reverse()

                // Update the RecyclerView only if there is a search query
                if (binding.searchBlogItem.query.isNotBlank()) {
                    searchAdapter.filter(binding.searchBlogItem.query.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SearchActivity, "Blog loading failed", Toast.LENGTH_SHORT).show()
            }
        })


    }
}