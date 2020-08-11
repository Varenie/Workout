package com.example.workout.main.Actitivities

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.workout.R
import com.example.workout.main.DataClasses.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.rengwuxian.materialedittext.MaterialEditText

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private val db = Firebase.database
    private val dbUsers = db.getReference("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
    }

    fun showRegWindow(view: View) {
        val dialog = AlertDialog.Builder(this@MainActivity)
        dialog.setTitle("Регистрация")
        dialog.setMessage("Введите данные")

        val inflater = LayoutInflater.from(this@MainActivity)
        val registerWindow = inflater.inflate(R.layout.registration_layout, null)
        dialog.setView(registerWindow)

        val email = registerWindow.findViewById<MaterialEditText>(R.id.emailFieldReg)
        val password = registerWindow.findViewById<MaterialEditText>(R.id.passwordFieldReg)
        val confirmPassword = registerWindow.findViewById<MaterialEditText>(R.id.confirmPasswordField)
        val name = registerWindow.findViewById<MaterialEditText>(R.id.nameField)
        val height = registerWindow.findViewById<MaterialEditText>(R.id.heightField)
        val weight = registerWindow.findViewById<MaterialEditText>(R.id.weightField)


        dialog.setNegativeButton("Отменить", DialogInterface.OnClickListener { dialogInterfaсe, which ->
            dialogInterfaсe.dismiss()
        })

        dialog.setPositiveButton("Подтвердить", DialogInterface.OnClickListener { dialogInterface, which ->
            val pass = password.text.toString()
            val confirmPass = confirmPassword.text.toString()
            var heightN = 0
            var weightN = 0

            when {
                TextUtils.isEmpty(email.text) -> {
                    val toast = Toast.makeText(
                        this@MainActivity,
                        "Укажите электронную почту!",
                        Toast.LENGTH_SHORT
                    )
                    toast.setGravity(Gravity.TOP, 0, 0)
                    toast.show()

                }

                password.length() < 6 -> {
                    val toast = Toast.makeText(
                        this@MainActivity,
                        "Пароль не менее 6 символов!",
                        Toast.LENGTH_SHORT
                    )
                    toast.setGravity(Gravity.TOP, 0, 0)
                    toast.show()
                }

                pass.compareTo(confirmPass) != 0 -> {
                    val toast = Toast.makeText(
                        this@MainActivity,
                        "Пароли не совпадают",
                        Toast.LENGTH_SHORT
                    )
                    toast.setGravity(Gravity.TOP, 0, 0)
                    toast.show()
                }

                TextUtils.isEmpty(name.text) -> {
                    val toast = Toast.makeText(
                        this@MainActivity,
                        "Заполните имя пользователя!",
                        Toast.LENGTH_SHORT
                    )
                    toast.setGravity(Gravity.TOP, 0, 0)
                    toast.show()
                }

                !TextUtils.isEmpty(height.text) -> heightN = height.text.toString().toInt()

                !TextUtils.isEmpty(weight.text) -> weightN = weight.text.toString().toInt()

                else ->{

                    auth.createUserWithEmailAndPassword(email.text.toString(), pass)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                val toast = Toast.makeText(
                                    this@MainActivity,
                                    "Пользователь добавлен",
                                    Toast.LENGTH_SHORT
                                )
                                toast.setGravity(Gravity.TOP, 0, 0)
                                toast.show()

                                val userId = auth.currentUser!!.uid
                                val user = User(
                                    userId,
                                    email.text.toString(),
                                    name.text.toString(),
                                    heightN,
                                    weightN
                                )
                                Log.d("PROVERKA", user.toString())

                                dbUsers.child(userId).setValue(user)

                                startActivity(Intent(this, BasicActivity::class.java))
                            } else {
                                // If sign in fails, display a message to the user.
                                val toast = Toast.makeText(
                                    this@MainActivity,
                                    "Ошибка при добавлении!",
                                    Toast.LENGTH_SHORT
                                )
                                toast.setGravity(Gravity.TOP, 0, 0)
                                toast.show()

                            }
                        }
                }
            }
        })


        dialog.show()
    }

    fun showAuthWindow(view: View) {
        val dialog = AlertDialog.Builder(this@MainActivity)
        dialog.setTitle("Авторизация")
        dialog.setMessage("Введите данные")

        val inflater = LayoutInflater.from(this@MainActivity)
        val registerWindow = inflater.inflate(R.layout.sign_in_layout, null)
        dialog.setView(registerWindow)

        val email = registerWindow.findViewById<MaterialEditText>(R.id.emailFieldAuth)
        val password = registerWindow.findViewById<MaterialEditText>(R.id.passwordFieldAuth)

        dialog.setNegativeButton("Отменить", DialogInterface.OnClickListener { dialogInterfae, which ->
            dialogInterfae.dismiss()
        })

        dialog.setPositiveButton("Подтвердить", DialogInterface.OnClickListener { dialogInterface, which ->
            when {
                TextUtils.isEmpty(email.text) -> {
                    val toast = Toast.makeText(
                        this@MainActivity,
                        "Укажите электронную почту!",
                        Toast.LENGTH_SHORT
                    )
                    toast.setGravity(Gravity.TOP, 0, 0)
                    toast.show()

                }

                TextUtils.isEmpty(password.text) -> {
                    val toast = Toast.makeText(
                        this@MainActivity,
                        "Укажите пароль!",
                        Toast.LENGTH_SHORT
                    )
                    toast.setGravity(Gravity.TOP, 0, 0)
                    toast.show()
                }

                else -> {
                    auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                val user = auth.currentUser
                                startActivity(Intent(this, BasicActivity::class.java))
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(
                                    baseContext, "Ошибка авторизации.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        })

        dialog.show()
    }
}
