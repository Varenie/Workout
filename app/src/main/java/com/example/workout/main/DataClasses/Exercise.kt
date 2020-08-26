package com.example.workout.main.DataClasses

import com.google.firebase.database.Exclude

data class Exercise(
    val name: String? = "",
    val countOfReplay: Int? = 0,
    val key: String? = ""
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "countOfReplay" to countOfReplay,
            "key" to key
        )
    }
}