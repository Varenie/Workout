package com.example.workout.main.Actitivities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.workout.R
import com.example.workout.main.DataClasses.User
import com.example.workout.main.Fragments.AccountFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class BasicActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var auth: FirebaseAuth

    private val db = Firebase.database
    private val dbUsers = db.getReference("Users")

    val icons = mapOf(
        "user" to R.drawable.user,
        "axe" to R.drawable.battle_axe
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        auth = Firebase.auth
        val userId = auth.currentUser!!.uid

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        val header = navView.getHeaderView(0)
        val nav_head = header.findViewById<TextView>(R.id.nav_header_title)
        val userImage = header.findViewById<ImageView>(R.id.nav_user_image)

        dbUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.child(userId).getValue<User>()
                val image = value!!.icon
                nav_head.text = value!!.name.toString()
                userImage.setImageResource(icons[image]!!)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                val toast = Toast.makeText(
                    this@BasicActivity,
                    "Ошибка загрузки данных",
                    Toast.LENGTH_SHORT
                )
                toast.setGravity(Gravity.TOP, 0, 0)
                toast.show()
            }
        })

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_account, R.id.nav_trainings, R.id.nav_chart
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.basic, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun log_out(view: View) {
        Firebase.auth.signOut()
        startActivity(Intent(this, MainActivity::class.java))
    }

    fun changeHeight(view: View){
        val heightText = this.findViewById<EditText>(R.id.edit_height)

        if (TextUtils.isEmpty(heightText.text)) {
            val toast = Toast.makeText(
                this@BasicActivity,
                "Поле не заполнено",
                Toast.LENGTH_SHORT
            )
            toast.setGravity(Gravity.TOP, 0, 0)
            toast.show()
        } else {
            val height = heightText.text.toString().toDouble()
            val userId = auth.currentUser!!.uid

            if (height < 100) {
                val toast = Toast.makeText(
                    this@BasicActivity,
                    "Прошу прощения, но кажется ваш рост точно больше 100 см",
                    Toast.LENGTH_SHORT
                )
                toast.setGravity(Gravity.TOP, 0, 0)
                toast.show()
            } else
                dbUsers.child(userId).child("height").setValue(height)
        }

        heightText.setText("")
    }

    fun changeWeight(view: View){
        val weightText = this.findViewById<EditText>(R.id.edit_weight)

        if (TextUtils.isEmpty(weightText.text)) {
            val toast = Toast.makeText(
                this@BasicActivity,
                "Поле не заполнено",
                Toast.LENGTH_SHORT
            )
            toast.setGravity(Gravity.TOP, 0, 0)
            toast.show()
        } else {
            val weight = weightText.text.toString().toDouble()
            val userId = auth.currentUser!!.uid

            if (weight < 30) {
                val toast = Toast.makeText(
                    this@BasicActivity,
                    "Прошу прощения, но кажется ваш вес точно больше 30 кг",
                    Toast.LENGTH_SHORT
                )
                toast.setGravity(Gravity.TOP, 0, 0)
                toast.show()
            } else
                dbUsers.child(userId).child("weight").setValue(weight)
        }

        weightText.setText("")
    }
}