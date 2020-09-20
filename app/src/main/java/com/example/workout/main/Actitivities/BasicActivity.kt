package com.example.workout.main.Actitivities

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.workout.R
import com.example.workout.main.DataClasses.Training
import com.example.workout.main.DataClasses.User
import com.example.workout.main.DataClasses.WeightInfo
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.rengwuxian.materialedittext.MaterialEditText
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class BasicActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var auth: FirebaseAuth

    private val format = SimpleDateFormat("dd|MM|yy")

    private val db = Firebase.database
    private val dbUsers = db.getReference("Users")
    private val dbTrainings = db.getReference("Trainings")
    private val dbWeight = db.getReference("Weight")

    val quotes = arrayOf(
        "Чтобы сделать в мире что-нибудь достойное, нельзя стоять на берегу, дрожа и думая о холодной воде и опасностях, подстерегающих пловцов. Надо прыгать в воду и выплывать, как получится / Сидней Смит",
        "Кто может, тот делает, кто не может, тот критикует / Чак Паланик",
        "Всегда делай то, что ты боишься сделать / Ральф Уолдо Эмерсон",
        "Успех чаще выпадает на долю того, кто смело действует, но его редко добиваются те, кто проявляет робость и постоянно опасается последствий / Джавахарлал Неру",
        "Если Вы хотите иметь то, что никогда не имели, — начните делать то, что никогда не делали / Ричард Бах"
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
        val nav_sub = header.findViewById<TextView>(R.id.nav_header_subtitle)
        val userImage = header.findViewById<ImageView>(R.id.nav_user_image)

        val rand = Random.nextInt(0, quotes.size)
        nav_sub.text = quotes[rand]

        dbUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.child(userId).getValue<User>()
                val image = value!!.icon


                val inputStream = image?.toUri()?.let {
                    this@BasicActivity.contentResolver.openInputStream(
                        it
                    )
                }
                val bmp = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                nav_head.text = value!!.name.toString()
                userImage.setImageBitmap(bmp)
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

    fun logOut(view: View) {
        Firebase.auth.signOut()
        startActivity(Intent(this, MainActivity::class.java))
    }

    fun changeHeight(view: View){
        val heightText = this.findViewById<EditText>(R.id.edit_height)
        var height = 0.0

        if (!TextUtils.isEmpty(heightText.text))  height = heightText.text.toString().toDouble()

        val userId = auth.currentUser!!.uid

        if (height < 100) {
            val toast = Toast.makeText(
                this@BasicActivity,
                "Укажите верные параметры роста",
                Toast.LENGTH_SHORT
            )
            toast.setGravity(Gravity.TOP, 0, 0)
            toast.show()
        } else
            dbUsers.child(userId).child("height").setValue(height)

        heightText.setText("")
    }

    fun changeWeight(view: View){
        val weightText = this.findViewById<EditText>(R.id.edit_weight)
        var weight = 0.0

        if (!TextUtils.isEmpty(weightText.text))  weight = weightText.text.toString().toDouble()

        val userId = auth.currentUser!!.uid

        if (weight < 30) {
            val toast = Toast.makeText(
                this@BasicActivity,
                "Укажите верные параметры веса",
                Toast.LENGTH_SHORT
            )
            toast.setGravity(Gravity.TOP, 0, 0)
            toast.show()
        } else {
            dbUsers.child(userId).child("weight").setValue(weight)

            val date = format.format(Date())
            val info = WeightInfo(weight, date.toString())

            dbWeight.child(userId).child(date).setValue(info)
        }


        weightText.setText("")
    }

    fun changeIcon(view: View) {
        startActivity(Intent(this@BasicActivity, IconActivity::class.java))
    }

    fun addTraining(view: View) {
        val dialog = AlertDialog.Builder(this@BasicActivity)
        dialog.setTitle("Добавить тренировку")

        val inflater = LayoutInflater.from(this@BasicActivity)
        val addWindow = inflater.inflate(R.layout.training_add_layout, null)
        dialog.setView(addWindow)

        val nameOfTraining = addWindow.findViewById<MaterialEditText>(R.id.name_of_training)
        val userId = auth.currentUser!!.uid

        dialog.setNegativeButton("Отменить", DialogInterface.OnClickListener { dialogInterfaсe, which ->
            dialogInterfaсe.dismiss()
        })

        dialog.setPositiveButton("Подтвердить") { dialogInterface, which ->
            if (TextUtils.isEmpty(nameOfTraining.text)) {
                val toast = Toast.makeText(
                    this@BasicActivity,
                    "Поле с названием пусто!",
                    Toast.LENGTH_SHORT
                )
                toast.setGravity(Gravity.TOP, 0, 0)
                toast.show()
            } else {
                val key = dbTrainings.push().key
                val training = Training(nameOfTraining.text.toString(), key)

                dbTrainings.child("/user-trainings/$userId/$key").setValue(training)
            }
        }

        dialog.show()
    }

    fun toWeightChart(view: View) {
        startActivity(Intent(this@BasicActivity, WeightChartActivity::class.java))
    }
}