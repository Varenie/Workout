package com.example.workout.main.Fragments

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.R
import com.example.workout.main.Adapters.ExercisesAdapter
import com.example.workout.main.Adapters.SimpleTouchHelperCallback
import com.example.workout.main.Adapters.TrainingsAdapter
import com.example.workout.main.DataClasses.Training
import com.example.workout.main.Singletons.TrainingSingleton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TrainingFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    private val db = Firebase.database

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_training, container, false)
        val myRecycler = root.findViewById<RecyclerView>(R.id.rv_trainings)


        auth = Firebase.auth
        val userId = auth.currentUser!!.uid
        val dbTrainings = db.getReference("Trainings/user-trainings/$userId")

        dbTrainings.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val trainingSingleton = TrainingSingleton.getInstance()!!
                for ((i, item) in snapshot.children.withIndex()) {
                    trainingSingleton.names[i] = item.child("name").value as String?
                    trainingSingleton.counts[i] = item.child("countExercises").getValue(Int::class.java)
                    trainingSingleton.keys[i] = item.child("key").value as String?
                }

                val size = snapshot.childrenCount.toInt()
                updateUi(size, myRecycler)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                val toast = Toast.makeText(
                    activity!!.applicationContext,
                    "Ошибка загрузки данных",
                    Toast.LENGTH_SHORT
                )
                toast.setGravity(Gravity.TOP, 0, 0)
                toast.show()
            }
        })
        return root
    }

    private fun updateUi(size: Int, myRecycler: RecyclerView) {
        myRecycler.layoutManager = LinearLayoutManager(activity)
        myRecycler.setHasFixedSize(true)

        val adapter = TrainingsAdapter(size)
        myRecycler.adapter = adapter

        val callback = SimpleTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(myRecycler)
    }
}