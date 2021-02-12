package com.github.talosdev.task.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.talosdev.task.databinding.ItemUserBinding
import com.github.talosdev.task.domain.User

class UsersAdapter(
    private val userActions: UserActions
) : RecyclerView.Adapter<UserViewHolder>() {

    var users: List<User> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position], userActions)
    }

    override fun getItemCount(): Int {
        return users.size
    }
}

class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User, userActions: UserActions) {
        binding.name.text = user.name
        binding.email.text = user.email
        binding.delete.setOnClickListener {
            userActions.onUserAction(UserAction.Delete(user))
        }
    }
}

sealed class UserAction {
    data class Delete(val user: User): UserAction()
}

interface UserActions {
    fun onUserAction(userAction: UserAction)
}
