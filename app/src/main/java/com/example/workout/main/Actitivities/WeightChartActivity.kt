package com.example.workout.main.Actitivities

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.workout.R
import com.example.workout.main.ValueFormatters.MyXAxisFormatter
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class WeightChartActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private val db = Firebase.database


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight_chart)

        auth = Firebase.auth
        val userId = auth.currentUser!!.uid
        val dbWeight = db.getReference("Weight/$userId")

        val weightChart = findViewById<LineChart>(R.id.chart_weight)

        dbWeight.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val entries = ArrayList<Entry>()
                val labels = ArrayList<String>()
                for ((i, item) in snapshot.children.withIndex()) {
                    val weight = item.child("weight").getValue(Float::class.java)
                    val date = item.child("date").getValue(String::class.java)

                    entries.add(Entry(i.toFloat(), weight!!))
                    labels.add(date!!)
                }

                updateUI(entries, labels, weightChart)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                val toast = Toast.makeText(
                    this@WeightChartActivity,
                    "Ошибка загрузки данных",
                    Toast.LENGTH_SHORT
                )
                toast.setGravity(Gravity.TOP, 0, 0)
                toast.show()
            }
            })
    }

    private fun updateUI(entries: ArrayList<Entry>, labels: ArrayList<String>, weightChart: LineChart) {
        val dataset = LineDataSet(entries, "Вес")
        dataset.color = Color.RED
        dataset.lineWidth = 2f
        dataset.setCircleColor(Color.RED)
        dataset.valueTextSize = 10f
        dataset.valueTextColor = Color.BLUE

        val data = LineData(dataset)
        weightChart.data = data
        weightChart.axisRight.isEnabled = false
        weightChart.xAxis.valueFormatter = MyXAxisFormatter(labels)
        weightChart.description.isEnabled = false

    }
}
