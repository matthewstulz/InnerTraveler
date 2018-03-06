package com.github.stulzm2.innertraveler

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SinglePostActivity : BaseActivity() {

    private var singelImage: ImageView? = null
    private var singleTitle: TextView? = null
    private var singleDesc: TextView? = null
    internal var post_key: String? = null
    private var mDatabase: DatabaseReference? = null
    private var deleteBtn: Button? = null
    private var mAuth: FirebaseAuth? = null
    private var uId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_post)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        singelImage = findViewById(R.id.singleImageview)
        singleTitle = findViewById(R.id.singleTitle)
        singleDesc = findViewById(R.id.singleDesc)
        mDatabase = FirebaseDatabase.getInstance().reference.child("InnerTraveler")
        post_key = intent.extras!!.getString("PostID")
//        deleteBtn = findViewById(R.id.deleteBtn)
        mAuth = FirebaseAuth.getInstance()
//        deleteBtn!!.visibility = View.INVISIBLE
//        deleteBtn!!.setOnClickListener {
//        }


        mDatabase!!.child(post_key!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val post_title = dataSnapshot.child("title")?.value as String?
                supportActionBar?.title = post_title
                val post_desc = dataSnapshot.child("desc")?.value as String?
                val post_image = dataSnapshot.child("imageUrl")?.value as String?
//                val post_uid = dataSnapshot.child("uid")?.value as String?
                uId = dataSnapshot.child("uid")?.value as String?

                singleTitle!!.text = post_title
                singleDesc!!.text = post_desc
//                Picasso.with(this@SinglePostActivity).load(post_image).fit().centerInside().into(singelImage)
                Glide.with(this@SinglePostActivity).load(post_image).apply(RequestOptions().fitCenter()).into(singelImage!!)

//                if (mAuth!!.currentUser!!.uid == post_uid) {
//
//                    deleteBtn!!.visibility = View.VISIBLE
//                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun delete() {
        mDatabase!!.child(post_key!!).removeValue()
        startActivity(Intent(this@SinglePostActivity, MainActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return if (mAuth!!.currentUser!!.uid == uId) {
            menuInflater.inflate(R.menu.menu_single_post, menu)
            true
        } else
            false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> finish()
            R.id.action_delete -> delete()
        }
        return super.onOptionsItemSelected(item)
    }
}
