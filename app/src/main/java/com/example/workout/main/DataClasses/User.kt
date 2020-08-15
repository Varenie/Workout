package com.example.workout.main.DataClasses

import com.google.firebase.database.Exclude

data class User(
    val uId: String? = "",
    val email: String? = "",
    val name: String? = "",
    val height: Double = 0.0,
    val weight: Double = 0.0,
    val gender: String? = "",
    val icon: String? = ""
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uId" to uId,
            "email" to email,
            "name" to name,
            "height" to height,
            "weigth" to weight,
            "gender" to gender,
            "icon" to icon
        )
    }
}