package com.example.androidgpt

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.EditText
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.DatabaseReference

class ChatActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var editText: EditText
    private lateinit var sendButton: Button
    private lateinit var databaseReference: DatabaseReference
    private val messages = mutableListOf<String>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Initialize Firebase Auth and Database Reference
        val mAuth = FirebaseAuth.getInstance()

        if (mAuth.currentUser == null) {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }


        databaseReference = FirebaseDatabase.getInstance().getReference("messages")

        // Initialize UI components
        recyclerView = findViewById(R.id.recyclerView)
        editText = findViewById(R.id.editText)
        sendButton = findViewById(R.id.sendButton)

        // Initialize RecyclerView and Adapter
        chatAdapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter

        // Click listener for send button
        sendButton.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                // User is authenticated
                val message = editText.text.toString()
                if (message.isNotEmpty()) {
                    databaseReference.push().setValue(message)
                }
            } else {
            val ir = 0;// No user is signed in. Handle accordingly, e.g., prompt login.
            }
        }


        // Listen for changes in Firebase database
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                messages.clear()
                for (snapshot in dataSnapshot.children) {
                    val message = snapshot.getValue(String::class.java)
                    if (message != null) {
                        messages.add(message)
                    }
                }
                chatAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("ChatActivity", "Error: ${databaseError.message}")
            }

        })
    }
}
