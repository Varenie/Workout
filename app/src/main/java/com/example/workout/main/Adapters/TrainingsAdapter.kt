package com.example.workout.main.Adapters

import android.content.Context
import android.content.Intent
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.R
import com.example.workout.main.Actitivities.ExercisesActivity
import com.example.workout.main.DataClasses.Training
import com.example.workout.main.Singletons.TrainingSingleton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TrainingsAdapter(size: Int): RecyclerView.Adapter<TrainingsAdapter.VHolder>(), ItemTouchHelperAdapter {
    private val size = size
    val trainingSingleton = TrainingSingleton.getInstance()!!

    private lateinit var auth: FirebaseAuth

    val db = Firebase.database
    lateinit var dbTrainings: DatabaseReference
    lateinit var dbExercise: DatabaseReference

    class VHolder(itemView: View, trainings: TrainingSingleton): RecyclerView.ViewHolder(itemView) {
        val context = itemView.context
        val trainings = trainings

        init {
            super.itemView
            itemView.setOnClickListener(View.OnClickListener {

                val intent = Intent(context, ExercisesActivity::class.java)
                intent.putExtra("key", trainings.keys[adapterPosition])
                intent.putExtra("name", trainings.names[adapterPosition])
                intent.putExtra("tag", trainings.tags[adapterPosition])
                context.startActivity(intent)
            })
        }
        private val nameOfTraining = itemView.findViewById<TextView>(R.id.tv_nameOfTraining)
        private val number = itemView.findViewById<TextView>(R.id.tv_numberOfExercises)

        fun bind(position: Int) {
            nameOfTraining.text = trainings.names[position]
            number.text = "Упражнений: ${trainings.counts[position]}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.training_recycler_item, parent, false)

        auth = Firebase.auth
        val userId = auth.currentUser!!.uid

        dbTrainings = db.getReference("Trainings/user-trainings/$userId")
        dbExercise = db.getReference("Trainings//trainings-exercises")


        return VHolder(view, trainingSingleton)
    }

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {

    }

    override fun onItemDismiss(position: Int) {
        val trainingKey = trainingSingleton.keys[position]

        dbTrainings.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dbExercise.child(trainingKey.toString()).removeValue()
                dbTrainings.child(trainingKey.toString()).removeValue()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
    }
}