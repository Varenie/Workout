package com.example.workout.main.Actitivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.net.toUri
import com.example.workout.R
import com.example.workout.main.DataClasses.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.rengwuxian.materialedittext.MaterialEditText

class ChangeInfoActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    val db = Firebase.database
    val dbUser = db.getReference("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_info)

        auth = Firebase.auth
        val userId = auth.currentUser?.uid

        dbUser.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mySnap = snapshot.child(userId.toString())
                val icon = mySnap.child("icon").value as String
                val name = mySnap.child("name").value as String
                val height = mySnap.child("height").value as Double
                val weight = mySnap.child("weight").value as Double
                val gender = mySnap.child("gender").value as String

                val user = User(name = name, icon = icon, height = height, weight = weight, gender = gender)

                updateUI(user)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        val btnAccept = findViewById<Button>(R.id.btn_accept)
        btnAccept.setOnClickListener {
            confirmChanges(userId)
        }
    }

    private fun updateUI(user: User) {
        val ivUser = findViewById<ImageView>(R.id.iv_change_icon)
        val metName = findViewById<MaterialEditText>(R.id.met_change_name)
        val metHeight = findViewById<MaterialEditText>(R.id.met_change_height)
        val metWeight = findViewById<MaterialEditText>(R.id.met_change_weight)
        val rbtnMale = findViewById<RadioButton>(R.id.gender_male)
        val rbtnFamale = findViewById<RadioButton>(R.id.gender_famale)

        ivUser.setImageURI(user.icon?.toUri())
        metName.setText(user.name)
        metHeight.setText(user.height.toString())
        metWeight.setText(user.weight.toString())

        //установка значение гендера
        if (user.gender?.compareTo("Мужской") == 0) {
            rbtnMale.isChecked = true
        } else {
            rbtnFamale.isChecked = true
        }

        ivUser.setOnClickListener {
            startActivity(Intent(this@ChangeInfoActivity, IconActivity::class.java))
        }
    }

    fun confirmChanges(userId: String?) {
        val metName = findViewById<MaterialEditText>(R.id.met_change_name)
        val metHeight = findViewById<MaterialEditText>(R.id.met_change_height)
        val metWeight = findViewById<MaterialEditText>(R.id.met_change_weight)
        val rbtnFamale = findViewById<RadioButton>(R.id.gender_famale)

        //проверка на заполнение полей
        when {
            metName.text.isNullOrBlank() -> {
                val toast = Toast.makeText(
                    this@ChangeInfoActivity,
                    "Заполните имя пользователя!",
                    Toast.LENGTH_SHORT
                )
                toast.setGravity(Gravity.TOP, 0, 0)
                toast.show()
            }

            metHeight.text.isNullOrBlank() -> {
                val toast = Toast.makeText(
                    this@ChangeInfoActivity,
                    "Заполните рост!",
                    Toast.LENGTH_SHORT
                )
                toast.setGravity(Gravity.TOP, 0, 0)
                toast.show()
            }

            metWeight.text.isNullOrBlank() -> {
                val toast = Toast.makeText(
                    this@ChangeInfoActivity,
                    "Заполните вес",
                    Toast.LENGTH_SHORT
                )
                toast.setGravity(Gravity.TOP, 0, 0)
                toast.show()
            }

            else -> {
                val name = metName.text.toString()
                var height = metHeight.text.toString().toDouble()
                var weight = metHeight.text.toString().toDouble()
                var gender = "Мужской"

                when {
                    (height < 100 || height > 250) -> {
                        val toast = Toast.makeText(
                            this@ChangeInfoActivity,
                            "Укажите данныек в диапазоне от 100 до 250 см",
                            Toast.LENGTH_SHORT
                        )
                        toast.setGravity(Gravity.TOP, 0, 0)
                        toast.show()
                    }

                    (weight < 30 || weight > 250) -> {
                        val toast = Toast.makeText(
                            this@ChangeInfoActivity,
                            "Укажите данныек в диапазоне от 30 до 250 кг",
                            Toast.LENGTH_SHORT
                        )
                        toast.setGravity(Gravity.TOP, 0, 0)
                        toast.show()
                    }

                    else -> {
                        if (rbtnFamale.isChecked) gender = "Женский"

                        dbUser.child("$userId/name").setValue(name)
                        dbUser.child("$userId/height").setValue(height)
                        dbUser.child("$userId/weight").setValue(weight)
                        dbUser.child("$userId/gender").setValue(gender)
                    }
                }

            }
        }

        startActivity(Intent(this, BasicActivity::class.java))
    }
}