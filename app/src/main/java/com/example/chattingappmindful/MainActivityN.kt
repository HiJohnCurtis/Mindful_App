package com.example.chattingappmindful

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Display
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivityN : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var filteredList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var searchUsers:EditText
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_n)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()

        userList = ArrayList()
        filteredList = ArrayList(userList)
        adapter = UserAdapter(this, filteredList)
        //adapter = UserAdapter(this, userList)

        userRecyclerView = findViewById(R.id.userRecyclerView)
        searchUsers = findViewById(R.id.searchUsers)

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener{
            val intent = Intent(this, LoginN::class.java)
            startActivity(intent)
        }

        searchUsers.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No action needed
            }

            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString().trim().lowercase()
                filterUsers(searchText)
            }
        })




        mDbRef.child("user").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()
                for (postSnapshot in snapshot.children){

                    val currentUser = postSnapshot.getValue(User::class.java)

                    if(mAuth.currentUser?.uid != currentUser?.uid){
                            userList.add(currentUser!!)
                    }

                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {



            }

        })

    }


    private fun filterUsers(searchText: String) {
        filteredList.clear()

        if (searchText.isNotEmpty()) {
            for (user in userList) {
                if (user.name?.lowercase()?.contains(searchText)== true) {
                    filteredList.add(user)
                }
            }
        } else {
            filteredList.addAll(userList)
        }

        adapter.notifyDataSetChanged()
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.logout){
            mAuth.signOut()
            val intent = Intent(this@MainActivityN, LoginN::class.java)
            finish()
            startActivity(intent)
            return true
        }

        //return super.onOptionsItemSelected(item)
        return true
    }


}
