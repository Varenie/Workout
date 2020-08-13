package com.example.workout.main.Fragments

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.workout.R
import com.example.workout.main.DataClasses.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class AccountFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    private val db = Firebase.database
    private val dbUsers = db.getReference("Users")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_account, container, false)

        auth = Firebase.auth
        val userId = auth.currentUser!!.uid

        dbUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.child(userId).getValue<User>()

                updateUI(value, root)
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

    private fun updateUI(user: User?, root: View) {
        val name = root.findViewById<TextView>(R.id.userName)
        val height = root.findViewById<TextView>(R.id.height_acc)
        val weight = root.findViewById<TextView>(R.id.weight_acc)

        name.text = user!!.name
        height.text = "Рост: ${user.height}"
        weight.text = "Вес: ${user.weight}"
    }


}