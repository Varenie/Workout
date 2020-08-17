package com.example.workout.main.Actitivities

import android.content.ContentResolver
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.workout.R
import com.example.workout.main.Adapters.IconChangeAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_icon.*


class IconActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var userId = ""
    private val db = Firebase.database
    private val dbUsers = db.getReference("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_icon)

        auth = Firebase.auth
        userId = auth.currentUser!!.uid

        val userIcon = findViewById<ImageView>(R.id.iv_userIcon)
        val gv_iconChange = findViewById<GridView>(R.id.gv_changeIcon)

        val gridViewOnItemClickListener =
            OnItemClickListener { parent, v, position, id ->
                val imageView = v.findViewById<ImageView>(R.id.imageView)
                val imageUri: Uri = Uri.Builder()
                    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                    .authority(resources.getResourcePackageName(imageView.tag as Int))
                    .appendPath(resources.getResourceTypeName(imageView.tag as Int))
                    .appendPath(resources.getResourceEntryName(imageView.tag as Int))
                    .build()
                userIcon.setImageURI(imageUri)
                userIcon.tag = imageView.tag
            }

        gv_iconChange.adapter = IconChangeAdapter(this@IconActivity)
        gv_iconChange.onItemClickListener = gridViewOnItemClickListener
    }

    fun confirmIcon(view: View) {

        val imageView = findViewById<ImageView>(R.id.iv_userIcon)
        val imageUri: Uri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(imageView.tag as Int))
            .appendPath(resources.getResourceTypeName(imageView.tag as Int))
            .appendPath(resources.getResourceEntryName(imageView.tag as Int))
            .build()
        dbUsers.child("/$userId/icon").setValue(imageUri.toString())
        startActivity(Intent(this@IconActivity, BasicActivity::class.java))
    }
}


