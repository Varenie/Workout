package com.example.workout.main.Adapters

import android.content.DialogInterface
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.R
import com.example.workout.main.Actitivities.ExercisesActivity
import com.example.workout.main.DataClasses.Exercise
import com.example.workout.main.Singletons.ExercisesSingleton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.rengwuxian.materialedittext.MaterialEditText

class ExercisesAdapter(size: Int, trainingKey: String, activity: ExercisesActivity): RecyclerView.Adapter<ExercisesAdapter.VHolder>(), ItemTouchHelperAdapter {
    private val size = size
    val activity = activity

    val trainingKey = trainingKey
    val exercisesSingleton = ExercisesSingleton.getInstance()!!

    private lateinit var auth: FirebaseAuth

    private val db = Firebase.database
    val dbExercise = db.getReference("Trainings//trainings-exercises/$trainingKey/")
    val dbTrainings = db.getReference("Trainings")

    class VHolder(itemView: View, auth: FirebaseAuth, trainingKey: String, activity: ExercisesActivity, exercises: ExercisesSingleton, dbTrainings: DatabaseReference): RecyclerView.ViewHolder(itemView) {
        val dbTrainings = dbTrainings
        val trainingKey = trainingKey
        val exercises = exercises

        private val nameOfExercise = itemView.findViewById<TextView>(R.id.tv_nameOfExercise)
        private val number = itemView.findViewById<TextView>(R.id.tv_numberOfReplays)
        private val weight = itemView.findViewById<TextView>(R.id.tv_weight_of_exercise)
        private val currentNumber = itemView.findViewById<TextView>(R.id.et_count_of_replays)
        private val currentWeight = itemView.findViewById<TextView>(R.id.et_weight_of_exercise)
        private val btnFinish = itemView.findViewById<Button>(R.id.btn_finish_exercise)

        init {

            itemView.setOnLongClickListener( View.OnLongClickListener{
                openDialog(activity, exercises)
            })
        }

        fun bind(position: Int, dbExercise: DatabaseReference) {
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

        private fun openDialog(activity: ExercisesActivity, exercises: ExercisesSingleton): Boolean {
            val dialog = AlertDialog.Builder(activity)
            dialog.setTitle("Изменить упражнение")

            val inflater = LayoutInflater.from(activity)
            val addWindow = inflater.inflate(R.layout.exercise_change_layout, null)
            dialog.setView(addWindow)

            val nameOfExercise = addWindow.findViewById<MaterialEditText>(R.id.met_name_of_exercise)
            nameOfExercise.setText(exercises.names[adapterPosition])

            dialog.setNegativeButton("Отменить", DialogInterface.OnClickListener { dialogInterfaсe, which ->
                dialogInterfaсe.dismiss()
            })

            dialog.setPositiveButton("Подтвердить") { dialogInterface, which ->
                when {
                    nameOfExercise.text.isNullOrBlank() -> {
                        val toast = Toast.makeText(
                            activity,
                            "Поле с названием пусто!",
                            Toast.LENGTH_SHORT
                        )
                        toast.setGravity(Gravity.TOP, 0, 0)
                        toast.show()
                    }

                    else -> {
                        val count = exercises.counts[adapterPosition]
                        val weight = exercises.weight[adapterPosition]

                        val trainingKey = trainingKey
                        val key = exercises.keys[adapterPosition]

                        val exercise = Exercise(nameOfExercise.text.toString(), count, weight, key.toString())

                        dbTrainings.child("/trainings-exercises/$trainingKey/$key")
                            .setValue(exercise)
                    }
                }
            }

            dialog.show()

            return true
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.exercise_recycler_item, parent, false)

        auth = Firebase.auth
        return VHolder(view, auth, trainingKey, activity, exercisesSingleton, dbTrainings)
    }

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.bind(position,  dbExercise)
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