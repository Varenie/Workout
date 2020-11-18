package com.example.workout.main.Adapters

import android.content.Context
import android.content.Intent
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.R
import com.example.workout.main.Actitivities.ExercisesActivity
import com.example.workout.main.DataClasses.Exercise
import com.example.workout.main.DataClasses.Training
import com.example.workout.main.Singletons.ExercisesSingleton
import com.example.workout.main.Singletons.TrainingSingleton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ExercisesAdapter(size: Int, trainingKey: String): RecyclerView.Adapter<ExercisesAdapter.VHolder>(), ItemTouchHelperAdapter {
    private val size = size
    val trainingKey = trainingKey
    val exercisesSingleton = ExercisesSingleton.getInstance()!!

    private lateinit var auth: FirebaseAuth

    private val db = Firebase.database
    val dbExercise = db.getReference("Trainings//trainings-exercises/$trainingKey/")

    class VHolder(itemView: View, auth: FirebaseAuth, trainingKey: String): RecyclerView.ViewHolder(itemView) {
        private val nameOfExercise = itemView.findViewById<TextView>(R.id.tv_nameOfExercise)
        private val number = itemView.findViewById<TextView>(R.id.tv_numberOfReplays)
        private val weight = itemView.findViewById<TextView>(R.id.tv_weight_of_exercise)
        private val currentNumber = itemView.findViewById<TextView>(R.id.et_count_of_replays)
        private val currentWeight = itemView.findViewById<TextView>(R.id.et_weight_of_exercise)
        private val btnFinish = itemView.findViewById<Button>(R.id.btn_finish_exercise)

        fun bind(exercises: ExercisesSingleton, position: Int, dbExercise: DatabaseReference) {
            nameOfExercise.text = exercises.names[position]
            number.text = "Повторов: ${exercises.counts[position]}"
            weight.text = "Вес: ${exercises.weight[position]}"

            btnFinish.setOnClickListener {
                var number = exercises.counts[position]
                var weight = exercises.weight[position]
                val key = exercises.keys[position]

                if (!currentNumber.text.isNullOrBlank()) number = currentNumber.text.toString().toInt()
                if (!currentWeight.text.isNullOrBlank()) weight = currentWeight.text.toString().toDouble()

                val exercise = Exercise(exercises.names[position], number, weight, key)

                if (key != null) {
                    dbExercise.child(key).setValue(exercise)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.exercise_recycler_item, parent, false)

        auth = Firebase.auth
        return VHolder(view, auth, trainingKey)
    }

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.bind(exercisesSingleton, position,  dbExercise)
    }

    override fun getItemCount(): Int {
        return size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {

    }

    override fun onItemDismiss(position: Int) {                 //Удаление упражнение по свайпу
        dbExercise.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dbExercise.child(exercisesSingleton.keys[position].toString()).removeValue()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
    }
}