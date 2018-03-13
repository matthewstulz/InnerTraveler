package com.github.stulzm2.innertraveler

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_single_post.*
import java.text.SimpleDateFormat
import java.util.*

class SinglePostActivity : BaseActivity() {

    private var postKey: String? = null
    private var mDatabase: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null
    private var mUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_post)
        initOperations()
    }

    private fun initOperations() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mDatabase = FirebaseDatabase.getInstance().reference.child("InnerTraveler")
        postKey = intent.extras!!.getString("PostID")
        mAuth = FirebaseAuth.getInstance()

        mDatabase!!.child(postKey!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val postTitle = dataSnapshot.child("title")?.value as String?
                supportActionBar?.title = postTitle
                singleDesc!!.text = dataSnapshot.child("desc")?.value as String?
                val postImage = dataSnapshot.child("imageUrl")?.value as String?
                mUid = dataSnapshot.child("uid")?.value as String?
                val postDate = dataSnapshot.child("date")?.value as Long?

                singleTitle!!.text = postTitle
                if (postDate != null)
                    singleDate!!.text = convertTime(postDate)
                Glide.with(this@SinglePostActivity).load(postImage).apply(RequestOptions().fitCenter()).into(singleImageview!!)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun convertTime(time: Long): String {
        val dateObject = Date(time)
        val dateFormatter = SimpleDateFormat("MMMM dd, yyyy hh:mm a", Locale.US)
        return dateFormatter.format(dateObject)
    }

    private fun delete() {
        mDatabase!!.child(postKey!!).removeValue()
        startActivity(Intent(this@SinglePostActivity, MainActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return if (mAuth!!.currentUser!!.uid == mUid) {
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
