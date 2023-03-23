package com.urahimli.chatappgeeks.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.urahimli.chatappgeeks.model.Message
import com.urahimli.chatappgeeks.adapter.MessageAdapter
import com.urahimli.chatappgeeks.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    /*
    * sekil qoymaq funksiyasi getir
    * */

    private lateinit var binding: ActivityChatBinding

    private lateinit var messageList: ArrayList<Message>
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var mDbRef: DatabaseReference

    var receiverRoom: String? = null
    var senderRoom: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //UserAdapterdan gonderidiyimiz melumatlari burada aliriq
        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")

        //mesaj gonderme alma logici
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        mDbRef = FirebaseDatabase.getInstance().getReference()

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid



        //yuxari toolbar'da adin gorsenmesi
        supportActionBar?.title = name

        //initialize adapter
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = messageAdapter


        //showing message on recyclerview
        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()

                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }

                    messageAdapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })



        //send button clicked
        binding.sendButton.setOnClickListener {
            val message = binding.messageBox.text.toString()
            val messageObject = Message(message, senderUid)

            if (!message.equals("") && !messageObject.equals("")) {
                //sender room ucun
                mDbRef.child("chats").child(senderRoom!!).child("messages")
                    .push()
                    .setValue(messageObject)
                    .addOnSuccessListener {
                        //receiver room ucun
                        mDbRef.child("chats").child(receiverRoom!!).child("messages")
                            .push()
                            .setValue(messageObject)
                    }

                binding.messageBox.setText("")
            } else {
                Toast.makeText(this, "You can't send empty message :)", Toast.LENGTH_LONG).show()
            }

        }

    }


}