package com.github.talosdev.task.data

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

interface UserService {

    @GET("users")
    fun getUsers(): Single<UsersResponse>

    @DELETE("users/{userId}")
    fun deleteUser(@Path("userId") userId: String): Single<GeneralResponse>

    @POST("users")
    fun createUser(@Body userPostRequest: UserPostRequest): Single<GeneralResponse>

}