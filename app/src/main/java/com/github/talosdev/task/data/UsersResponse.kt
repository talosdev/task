package com.github.talosdev.task.data

import com.github.talosdev.task.domain.User
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UsersResponse(
    val data: List<User>
)
