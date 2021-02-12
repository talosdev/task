package com.github.talosdev.task.domain

import com.github.talosdev.task.data.GeneralResponse
import com.github.talosdev.task.data.UserPostRequest
import com.github.talosdev.task.data.UserService
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class UserRepository(
    private val userService: UserService
) {

    fun getUsers(): Single<List<User>> {
        return userService.getUsers().subscribeOn(Schedulers.io()).map { it.data }
    }

    fun deleteUser(user: User): Completable {
        return userService.deleteUser(user.id)
            .subscribeOn(Schedulers.io())
            .flatMapCompletable { response ->
                if (response.code == GeneralResponse.DELETE_SUCCESS_CODE) {
                    Completable.complete()
                } else {
                    Completable.error(BusinessException(
                        response.data?.firstOrNull()?.let {
                            "${it.field} ${it.message}"
                        }
                    ))
                }
            }
    }

    fun createUser(name: String, email: String): Completable {
        return userService.createUser(
            UserPostRequest(
                name,
                email
            )
        ).subscribeOn(Schedulers.io())
            .flatMapCompletable { response ->
                if (response.code == GeneralResponse.CREATE_SUCCESS_CODE) {
                    Completable.complete()
                } else {
                    Completable.error(BusinessException(
                        response.data?.firstOrNull()?.let {
                            "${it.field} ${it.message}"
                        }
                    ))
                }
            }
    }
}

class BusinessException(message: String?) : Exception(message)