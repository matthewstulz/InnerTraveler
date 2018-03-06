package com.github.stulzm2.innertraveler

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : BaseActivity() {

    private var recyclerView: RecyclerView? = null
    private var mDatabase: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //initialize recyclerview and Firebase objects
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.setHasFixedSize(true)
        mDatabase = FirebaseDatabase.getInstance().reference.child("InnerTraveler")
        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener {
            if (mAuth!!.currentUser == null) {
                val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(loginIntent)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthListener!!)
        val FBRA = object : FirebaseRecyclerAdapter<InnerTraveler, ItViewHolder>(
                InnerTraveler::class.java,
                R.layout.card_items,
                ItViewHolder::class.java,
                mDatabase
        ) {
            override fun populateViewHolder(viewHolder: ItViewHolder, model: InnerTraveler, position: Int) {
                val post_key = getRef(position).key.toString()
                viewHolder.setTitle(model.title)
                viewHolder.setDesc(model.desc)
                viewHolder.setImageUrl(applicationContext, model.imageUrl)
                viewHolder.setUserName(model.username)
                viewHolder.mView.setOnClickListener {
                    val singleActivity = Intent(this@MainActivity, SinglePostActivity::class.java)
                    singleActivity.putExtra("PostID", post_key)
                    startActivity(singleActivity)
                }
            }
        }
        recyclerView!!.adapter = FBRA
    }

    class ItViewHolder(var mView: View) : RecyclerView.ViewHolder(mView) {
        fun setTitle(title: String?) {
            val postTitle = mView.findViewById<TextView>(R.id.post_title_txtview)
            postTitle.text = title
        }

        fun setDesc(desc: String?) {
            val postDesc = mView.findViewById<TextView>(R.id.post_desc_txtview)
            postDesc.text = desc
        }

        fun setImageUrl(ctx: Context, imageUrl: String?) {
            val postImage = mView.findViewById<ImageView>(R.id.post_image)
            Glide.with(ctx).load(imageUrl).apply(RequestOptions().centerCrop()).into(postImage)
        }

        fun setUserName(userName: String?) {
            val postUserName = mView.findViewById<TextView>(R.id.post_user)
            postUserName.text = "Posted by $userName"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_settings -> startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            R.id.action_add -> startActivity(Intent(this@MainActivity, PostActivity::class.java))
            R.id.action_log_out -> {
                mAuth!!.signOut()
                val logoutIntent = Intent(this@MainActivity, LoginActivity::class.java)
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(logoutIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}