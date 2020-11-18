package com.example.workout.main.Adapters

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.text.TextUtils
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.FragmentActivity
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
import com.rengwuxian.materialedittext.MaterialEditText

class TrainingsAdapter(size: Int, activity: FragmentActivity): RecyclerView.Adapter<TrainingsAdapter.VHolder>(), ItemTouchHelperAdapter {
    private val size = size
    val activity = activity
    val trainingSingleton = TrainingSingleton.getInstance()!!

    private lateinit var auth: FirebaseAuth

    val db = Firebase.database
    lateinit var dbTrainings: DatabaseReference
    lateinit var dbExercise: DatabaseReference

    class VHolder(itemView: View, trainings: TrainingSingleton, activity: FragmentActivity, auth: FirebaseAuth, dbTrainings: DatabaseReference, dbTrainingstags: DatabaseReference): RecyclerView.ViewHolder(itemView) {
        val context = itemView.context
        val trainings = trainings
        val dbTrainings = dbTrainings
        val dbTrainingTags = dbTrainingstags

        val hastags = arrayOf(
            "ноги",
            "руки",
            "пресс",
            "спина"
        )

        init {
            super.itemView
            itemView.setOnClickListener(View.OnClickListener {

                val intent = Intent(context, ExercisesActivity::class.java)
                intent.putExtra("key", trainings.keys[adapterPosition])
                intent.putExtra("name", trainings.names[adapterPosition])
                intent.putExtra("tag", trainings.tags[adapterPosition])
                context.startActivity(intent)
            })

            itemView.setOnLongClickListener( View.OnLongClickListener{
                openDialog(activity, auth, trainings)
            })
        }
        private val nameOfTraining = itemView.findViewById<TextView>(R.id.tv_nameOfTraining)
        private val number = itemView.findViewById<TextView>(R.id.tv_numberOfExercises)

        fun bind(position: Int) {
            nameOfTraining.text = trainings.names[position]
            number.text = "Упражнений: ${trainings.counts[position]}"
        }

        fun openDialog(activity: FragmentActivity, auth: FirebaseAuth, trainings: TrainingSingleton): Boolean {
            val dialog = AlertDialog.Builder(activity)
            dialog.setTitle("Изменить тренировку")

            val inflater = LayoutInflater.from(activity)
            val addWindow = inflater.inflate(R.layout.training_add_layout, null)
            dialog.setView(addWindow)

            val nameOfTraining = addWindow.findViewById<MaterialEditText>(R.id.name_of_training)
            nameOfTraining.setText(trainings.names[adapterPosition])

            val switch = addWindow.findViewById<SwitchCompat>(R.id.sw_private_training)

            //ниспадающий список для хэштэгов тренировок
            val spinner = addWindow.findViewById<Spinner>(R.id.sp_hashtags)
            val adapter: ArrayAdapter<String> =
                ArrayAdapter(activity, android.R.layout.simple_spinner_item, hastags)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            dialog.setNegativeButton(
                "Отменить",
                DialogInterface.OnClickListener { dialogInterfaсe, which ->
                    dialogInterfaсe.dismiss()
                })

            dialog.setPositiveButton("Подтвердить") { dialogInterface, which ->
                if (TextUtils.isEmpty(nameOfTraining.text)) {
                    val toast = Toast.makeText(
                        activity,
                        "Поле с названием пусто!",
                        Toast.LENGTH_SHORT
                    )
                    toast.setGravity(Gravity.TOP, 0, 0)
                    toast.show()
                } else {
                    val key = trainings.keys[adapterPosition]
                    val item = spinner.selectedItemPosition
                    val tag = hastags[item]
                    val training = Training(nameOfTraining.text.toString(), key, tag, trainings.counts[adapterPosition])



                        if(switch.isChecked) {
                        //Удаление тренировки из другого тага, если тэг изменился
                        for(item in hastags) {
                            if (item.compareTo(tag) != 0) {
                                val check = dbTrainingTags.child(item)

                                check.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.child(key.toString()).exists())
                                            check.child(key.toString()).removeValue()
                                    }

                                    override fun onCancelled(error: DatabaseError) {}
                                })
                            }
                        }

                        dbTrainings.child("$key").setValue(training)
                        dbTrainingTags.child("$tag/$key").setValue(training)
                    } else {
                        dbTrainings.child("$key").setValue(training)
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
        val view = inflater.inflate(R.layout.training_recycler_item, parent, false)

        auth = Firebase.auth
        val userId = auth.currentUser!!.uid

        dbTrainings = db.getReference("Trainings/user-trainings/$userId")
        dbExercise = db.getReference("Trainings//trainings-exercises")
        val dbTrainingstag = db.getReference("Trainings")

        return VHolder(view, trainingSingleton, activity, auth, dbTrainings, dbTrainingstag)
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