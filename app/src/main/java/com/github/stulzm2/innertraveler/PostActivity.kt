package com.github.stulzm2.innertraveler

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_post.*

class PostActivity : BaseActivity() {

    private var mUri: Uri? = null
    private var mStorage: StorageReference? = null
    private var mAuth: FirebaseAuth? = null
    private var mDatabaseUsers: DatabaseReference? = null
    private var mCurrentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        initOperations()
    }

    private fun initOperations() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "New Post"
        mStorage = FirebaseStorage.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        mCurrentUser = mAuth!!.currentUser
        mDatabaseUsers = FirebaseDatabase.getInstance().reference.child("Users").child(mCurrentUser!!.uid)
        //picking image from gallery
        imageBtn!!.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
        }
    }

    private fun post(databaseRef: DatabaseReference) {
        Toast.makeText(this@PostActivity, "POSTING...", Toast.LENGTH_LONG).show()
        val postTitle = et_textTitle!!.text.toString().trim { it <= ' ' }
        val postDesc = et_textDesc!!.text.toString().trim { it <= ' ' }

        if (!TextUtils.isEmpty(postDesc) && !TextUtils.isEmpty(postTitle)) {
            val filepath = mStorage!!.child("post_images").child(mUri!!.lastPathSegment)
            filepath.putFile(mUri!!).addOnSuccessListener { taskSnapshot ->
                val downloadUrl = taskSnapshot.downloadUrl//getting the post image download url
                Toast.makeText(applicationContext, "Successfully Uploaded", Toast.LENGTH_SHORT).show()
                val newPost = databaseRef.push()

                mDatabaseUsers!!.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        newPost.child("title").setValue(postTitle)
                        newPost.child("desc").setValue(postDesc)
                        newPost.child("imageUrl").setValue(downloadUrl!!.toString())
                        newPost.child("uid").setValue(mCurrentUser!!.uid)
                        newPost.child("username").setValue(dataSnapshot.child("firstName").value)
                                newPost.child("date").setValue(ServerValue.TIMESTAMP)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        startActivity(Intent(this@PostActivity, MainActivity::class.java))
                                    }
                                }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })
            }
        }
    }

    // image from gallery result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mUri = data.data
            Glide.with(this).load(mUri).into(imageBtn)
        }
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 2
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_post, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> finish()
            R.id.action_post -> {
                val databaseRef = FirebaseDatabase.getInstance().reference.child("InnerTraveler")
                post(databaseRef)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
