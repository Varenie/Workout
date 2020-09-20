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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.R
import com.example.workout.main.Adapters.ExercisesAdapter
import com.example.workout.main.Adapters.TrainingsAdapter
import com.example.workout.main.DataClasses.Exercise
import com.example.workout.main.DataClasses.Training
import com.example.workout.main.Singletons.ExercisesSingleton
import com.example.workout.main.Singletons.TrainingSingleton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.rengwuxian.materialedittext.MaterialEditText

class ExercisesActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private val db = Firebase.database
    private val dbTrainings = db.getReference("Trainings")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercises)

        val intent = intent
        val trainingKey = intent.getStringExtra("key")
        val name = intent.getStringExtra("name")

        val nameTraining = findViewById<TextView>(R.id.tv_name_training_ex)
        nameTraining.text = name

        auth = Firebase.auth
        val dbExercise = db.getReference("Trainings//trainings-exercises/$trainingKey/")

        val myRecycler = findViewById<RecyclerView>(R.id.rv_exercises)

        dbExercise.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val exercisesSingleton = ExercisesSingleton.getInstance()!!
                for ((i, item) in snapshot.children.withIndex()) {
                    exercisesSingleton.names[i] = item.child("name").value as String?
                    exercisesSingleton.counts[i] = item.child("countOfReplay").getValue(Int::class.java)
                    exercisesSingleton.keys[i] = item.child("key").value as String?
                }

                val size = snapshot.childrenCount.toInt()


                updateUi(size, myRecycler, trainingKey)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                val toast = Toast.makeText(
                    this@ExercisesActivity,
                    "Ошибка загрузки данных",
                    Toast.LENGTH_SHORT
                )
                toast.setGravity(Gravity.TOP, 0, 0)
                toast.show()
            }
        })
    }

    private fun updateUi(size: Int, myRecycler: RecyclerView, trainingKey: String) {
        myRecycler.layoutManager = LinearLayoutManager(this@ExercisesActivity)
        myRecycler.setHasFixedSize(true)

        myRecycler.adapter = ExercisesAdapter(size, trainingKey)
    }

    fun addExercises(view: View) {
        val dialog = AlertDialog.Builder(this@ExercisesActivity)
        dialog.setTitle("Добавить упражнение")

        val inflater = LayoutInflater.from(this@ExercisesActivity)
        val addWindow = inflater.inflate(R.layout.exercises_add_layout, null)
        dialog.setView(addWindow)

        val nameOfExercise = addWindow.findViewById<MaterialEditText>(R.id.name_of_exercise)
        val countOfReplays = addWindow.findViewById<MaterialEditText>(R.id.count_of_replays)

        dialog.setNegativeButton("Отменить", DialogInterface.OnClickListener { dialogInterfaсe, which ->
            dialogInterfaсe.dismiss()
        })

        dialog.setPositiveButton("Подтвердить") { dialogInterface, which ->
            var countR = 0
            if (!TextUtils.isEmpty(countOfReplays.text)) countR = countOfReplays.text.toString().toInt()

            when {
                TextUtils.isEmpty(nameOfExercise.text) -> {
                    val toast = Toast.makeText(
                        this@ExercisesActivity,
                        "Поле с названием пусто!",
                        Toast.LENGTH_SHORT
                    )
                    toast.setGravity(Gravity.TOP, 0, 0)
                    toast.show()
                }

                countR == 0 -> {
                    val toast = Toast.makeText(
                        this@ExercisesActivity,
                        "Укажите количетство повторов!",
                        Toast.LENGTH_SHORT
                    )
                    toast.setGravity(Gravity.TOP, 0, 0)
                    toast.show()
                }

                else -> {
                    val intent = intent
                    val trainingKey = intent.getStringExtra("key")
                    val key = dbTrainings.push().key
                    val exercise = Exercise(nameOfExercise.text.toString(), countR, key.toString())

                    dbTrainings.child("/trainings-exercises/$trainingKey/$key")
                        .setValue(exercise)
                    }
                }
            }
        dialog.show()
    }

    fun confirmChange(view: View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val intent = intent
        val trainingKey = intent.getStringExtra("key")
        val userId = auth.currentUser!!.uid

        val dbExercise = db.getReference("Trainings//trainings-exercises/$trainingKey/")
        val countOfExercisesRef = db.getReference("Trainings/user-trainings/$userId/$trainingKey/countExercises")

        dbExercise.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val size = snapshot.childrenCount.toInt()

                countOfExercisesRef.setValue(size)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}