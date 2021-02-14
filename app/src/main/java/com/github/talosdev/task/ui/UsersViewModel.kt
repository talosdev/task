package com.github.talosdev.task.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.talosdev.task.domain.ValidationException
import com.github.talosdev.task.domain.User
import com.github.talosdev.task.domain.UserRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject


class UsersViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _usersLiveData: MutableLiveData<UsersState> = MutableLiveData()
    val usersLiveData: LiveData<UsersState> = _usersLiveData

    private val _deleteUserLiveData: MutableLiveData<DeleteActionState> = MutableLiveData()
    val deleteUserLiveData: LiveData<DeleteActionState> = _deleteUserLiveData

    private val _createUserStream: PublishSubject<CreateUserState> = PublishSubject.create()
    val createUserStream: Observable<CreateUserState> = _createUserStream

    private val disposables: CompositeDisposable = CompositeDisposable()

    init {
        loadUsers()
    }

    fun loadUsers() {
        _usersLiveData.value = UsersState.Loading

        disposables.add(
            userRepository.getUsers()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ usersList ->
                    _usersLiveData.value = UsersState.Data(usersList)
                }, {
                    _usersLiveData.value = UsersState.Error
                })
        )
    }

    fun deleteUser(user: User) {
        (_usersLiveData.value as? UsersState.Data)?.users?.indexOf(user)?.let { index ->
            _deleteUserLiveData.value =
                DeleteActionState(index, DeleteActionState.State.IN_PROGRESS)
            disposables.add(
                userRepository.deleteUser(user)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        _usersLiveData.value =
                            (_usersLiveData.value as? UsersState.Data)?.users?.filter { it != user }
                                ?.let {
                                    UsersState.Data(it)
                                } ?: UsersState.Error
                        DeleteActionState(index, DeleteActionState.State.SUCCESS)
                    }, {
                        _deleteUserLiveData.value =
                            DeleteActionState(index, DeleteActionState.State.ERROR)
                    })
            )
        }
    }

    fun submitUser(name: String, email: String) {
        disposables.add(
            userRepository.createUser(name, email)
                .subscribe({
                    _createUserStream.onNext(CreateUserState.Success)
                }, {
                    _createUserStream.onNext(CreateUserState.Error(it is ValidationException))
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

}

class UsersViewModelFactory(private val userRepository: UserRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return UsersViewModel(userRepository) as T
    }
}

sealed class UsersState {
    data class Data(val users: List<User>) : UsersState()
    object Error : UsersState()
    object Loading : UsersState()
}

data class DeleteActionState(
    val position: Int,
    val state: State
) {
    enum class State {
        IN_PROGRESS,
        SUCCESS,
        ERROR
    }
}

sealed class CreateUserState {
    object Success : CreateUserState()
    data class Error(val isValidationError: Boolean = false) : CreateUserState()
}
