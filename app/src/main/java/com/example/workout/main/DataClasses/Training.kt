package com.example.workout.main.DataClasses

import com.google.firebase.database.Exclude

data class Training(
    val name: String? = "",
    val key: String? = "",
    val tag: String? = "",
    val countExercises: Int? = 0
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "key" to key,
            "tag" to tag,
            "countExercises" to countExercises
        )
    }
}