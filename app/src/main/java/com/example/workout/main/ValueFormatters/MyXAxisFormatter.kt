package com.example.workout.main.ValueFormatters

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class MyXAxisFormatter(dates: ArrayList<String>) : ValueFormatter() { //класс для верхнего лэйбла на графике
    val dates = dates
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return dates.getOrNull(value.toInt()) ?: value.toString()
    }
}