package com.github.talosdev.task.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserPostRequest(
    val name: String,
    val email: String,
    val gender: String = "Female", // Hardcoded
    val status: String = "Active" // Hardcoded
)
