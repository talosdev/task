package com.github.talosdev.task

import com.github.talosdev.task.data.TokenInterceptor
import com.github.talosdev.task.data.UserService
import com.github.talosdev.task.domain.UserRepository
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

const val BASE_URL = "https://gorest.co.in/public-api/"
const val TOKEN = "2d91c35be7ebcf3a47a710455c9fa6d3c3dc0ee08f153bf4d16dd5da67f33875"

@Suppress("MemberVisibilityCanBePrivate")
object ServiceLocator {

    val moshi = Moshi.Builder().build()
    val rxAdapter: RxJava3CallAdapterFactory = RxJava3CallAdapterFactory.create()

    val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .addInterceptor(TokenInterceptor(TOKEN))
        .build()


    val retrofit: Retrofit = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(rxAdapter)
        .client(client)
        .build()

    val userService: UserService = retrofit.create(UserService::class.java)

    val userRepo: UserRepository = UserRepository(userService)
}