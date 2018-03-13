package com.github.stulzm2.innertraveler

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_profile.*
import android.graphics.BitmapFactory

class ProfileActivity : BaseActivity(), OnMapReadyCallback {

    private var uri: Uri? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var numberOfPosts: Int = 0
    private var currentFirebaseUser: FirebaseUser? = null
    private var storage: StorageReference? = null

    private lateinit var mMap: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Profile"

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        storage = FirebaseStorage.getInstance().reference
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        mAuthListener = FirebaseAuth.AuthStateListener {
            if (mAuth!!.currentUser == null) {
                val loginIntent = Intent(this@ProfileActivity, LoginActivity::class.java)
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(loginIntent)
            }
        }
        currentFirebaseUser = FirebaseAuth.getInstance().currentUser
        FirebaseDatabase.getInstance().reference.child("InnerTraveler").orderByChild("uid").equalTo(currentFirebaseUser?.uid)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    numberOfPosts++
                }
                tvPosts.text = numberOfPosts.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        mDatabaseReference!!.child(currentFirebaseUser!!.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        tvProfileName.text = dataSnapshot.child("firstName")?.value as String?
                        val profileImage = dataSnapshot.child("profilePhoto")?.value as String?
                        Glide.with(this@ProfileActivity).load(profileImage).into(ivProfile)

                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })

        ivProfile!!.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, ProfileActivity.GALLERY_REQUEST_CODE)
        }
    }

    private fun updateProfileImage() {
        val filepath = storage!!.child("users_profile_images").child(uri!!.lastPathSegment)
        filepath.putFile(uri!!).addOnSuccessListener { taskSnapshot ->
            val downloadUrl = taskSnapshot.downloadUrl
            Toast.makeText(applicationContext, "Profile Image update", Toast.LENGTH_SHORT).show()
            val updateCurrentUser = mDatabaseReference!!.child(currentFirebaseUser!!.uid)
            updateCurrentUser.child("profilePhoto").setValue(downloadUrl!!.toString()).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this@ProfileActivity, ProfileActivity::class.java))
                }
            }
        }
    }

    override// image from gallery result
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            uri = data.data
            Glide.with(this).load(uri).into(ivProfile)
            updateProfileImage()
        }
    }

    companion object {
        private val GALLERY_REQUEST_CODE = 2
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val icon = BitmapFactory.decodeResource(this.resources,
                R.drawable.ic_beenhere_black_24px)


        // Add a marker in Sydney and move the camera
        val florence = LatLng(39.0, -84.0)
        mMap.addMarker(MarkerOptions().position(florence).title("Florence, KY"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(florence))

        FirebaseDatabase.getInstance().reference.child("Markers").orderByChild("uid").equalTo(currentFirebaseUser?.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot in dataSnapshot.children) {
                            val latitude = snapshot.child("latitude")?.value as Double
                            val longitude = snapshot.child("longitude")?.value as Double
                            val tempMarker = LatLng(latitude, longitude)
                            mMap.addMarker(MarkerOptions().position(tempMarker).title("Marker"))
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })

        // Setting a click event handler for the map
        mMap.setOnMapClickListener { latLng ->
            // Creating a marker
            val markerOptions = MarkerOptions()

            // Setting the position for the marker
            markerOptions.position(latLng)

            // Setting the title for the marker.
            // This will be displayed on taping the marker
            markerOptions.title(latLng.latitude.toString() + " : " + latLng.longitude)

            // Clears the previously touched position
            googleMap.clear()

            // Animating to the touched position
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))

            // Placing a marker on the touched position
            googleMap.addMarker(markerOptions)

            mDatabaseReference = mDatabase!!.reference!!.child("Markers")
            val newMarker = mDatabaseReference!!.push()
            mDatabaseReference!!.child(currentFirebaseUser!!.uid)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            newMarker.child("latitude").setValue(latLng.latitude)
                            newMarker.child("longitude").setValue(latLng.longitude)
                            newMarker.child("uid").setValue(currentFirebaseUser!!.uid)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this@ProfileActivity, "New marker added", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                        }
                    })
        }
    }
}
