package com.github.stulzm2.innertraveler

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_post.*
import java.net.URI

class PostActivity : BaseActivity() {
    // imports
    private var uri: Uri? = null
    private var textTitle: EditText? = null
    private var textDesc: EditText? = null
    private var storage: StorageReference? = null
    private val database: FirebaseDatabase? = null
    //    private var databaseRef: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null
    private var mDatabaseUsers: DatabaseReference? = null
    private var mCurrentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "New Post"
        // initializing objects
        textDesc = findViewById(R.id.textDesc)
        textTitle = findViewById(R.id.textTitle)
        storage = FirebaseStorage.getInstance().reference
//        databaseRef = database!!.getInstance().getReference().child("InnerTraveler")
//        val databaseRef = FirebaseDatabase.getInstance().reference.child("InnerTraveler")
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
        // posting to Firebase
        Toast.makeText(this@PostActivity, "POSTING...", Toast.LENGTH_LONG).show()
        val PostTitle = textTitle!!.text.toString().trim { it <= ' ' }
        val PostDesc = textDesc!!.text.toString().trim { it <= ' ' }
        // do a check for empty fields
        if (!TextUtils.isEmpty(PostDesc) && !TextUtils.isEmpty(PostTitle)) {
            val filepath = storage!!.child("post_images").child(uri!!.lastPathSegment)
            filepath.putFile(uri!!).addOnSuccessListener { taskSnapshot ->
                val downloadUrl = taskSnapshot.downloadUrl//getting the post image download url
                Toast.makeText(applicationContext, "Successfully Uploaded", Toast.LENGTH_SHORT).show()
                val newPost = databaseRef.push()
                //adding post contents to database reference
                mDatabaseUsers!!.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        newPost.child("title").setValue(PostTitle)
                        newPost.child("desc").setValue(PostDesc)
                        newPost.child("imageUrl").setValue(downloadUrl!!.toString())
                        newPost.child("uid").setValue(mCurrentUser!!.uid)
                        newPost.child("username").setValue(dataSnapshot.child("firstName").value)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val intent = Intent(this@PostActivity, MainActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })
            }
        }
    }

    override// image from gallery result
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            uri = data.data
            imageBtn!!.setImageURI(uri)

//            Picasso.with(this).load("file:" + uri).into(imageBtn)

//            Glide.with(this).load(uri).into(imageBtn)

        }
    }

    companion object {
        private val GALLERY_REQUEST_CODE = 2
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
