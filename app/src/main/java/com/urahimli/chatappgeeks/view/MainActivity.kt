package com.urahimli.chatappgeeks.view

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.urahimli.chatappgeeks.R
import com.urahimli.chatappgeeks.adapter.MessageAdapter
import com.urahimli.chatappgeeks.model.User
import com.urahimli.chatappgeeks.adapter.UserAdapter
import com.urahimli.chatappgeeks.databinding.ActivityMainBinding
import com.urahimli.chatappgeeks.model.Message

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        /*
//UserAdapterdan gonderidiyimiz melumatlari burada aliriq
        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
        //ustdeki toolbar
        supportActionBar?.title = "You are $name"

         */
        supportActionBar?.title = "WhatsHappen"




        //initialize db
        mAuth = Firebase.auth
        mDbRef = FirebaseDatabase.getInstance().getReference()

        //initialize recyclerView
        userList = ArrayList()
        adapter = UserAdapter(this, userList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        //database'in icindekini RealtimeDatabase'den gostermek
        mDbRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()

                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)

                    //ozun ozunle catlasmayasan deye
                    if (mAuth.currentUser?.uid != currentUser?.uid) {
                        userList.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    //Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                mAuth.signOut()
                val intent = Intent(this@MainActivity, Login::class.java)
                finish()
                startActivity(intent)
                true
            }
            R.id.aboutPage -> {
                val intent2 = Intent(this@MainActivity, AboutPage::class.java)
                startActivity(intent2)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}