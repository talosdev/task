package com.github.talosdev.task.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeneralResponse(
    val code: Int,
    val data: List<Data>?
) {
    companion object {
        const val CREATE_SUCCESS_CODE = 201
        const val DELETE_SUCCESS_CODE = 204
    }

    @JsonClass(generateAdapter = true)
    data class Data(
        val field: String,
        val message: String
    )
}

