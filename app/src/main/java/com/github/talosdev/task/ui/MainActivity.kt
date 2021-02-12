package com.github.talosdev.task.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.github.talosdev.task.R
import com.github.talosdev.task.ServiceLocator
import com.github.talosdev.task.databinding.ActivityMainBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable


class MainActivity : AppCompatActivity(), UserActions {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: UsersViewModel

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(
            this,
            UsersViewModelFactory(ServiceLocator.userRepo)
        ).get(UsersViewModel::class.java)

        val adapter = UsersAdapter(this)
        binding.recycler.adapter = adapter

        binding.retryButton.setOnClickListener {
            viewModel.loadUsers()
        }

        viewModel.usersLiveData.observe(this) { usersState ->
            binding.recycler.isVisible = usersState is UsersState.Data
            binding.errorLabel.isVisible = usersState is UsersState.Error
            binding.retryButton.isVisible = usersState is UsersState.Error
            binding.progress.isVisible = usersState is UsersState.Loading

            if (usersState is UsersState.Data) {
                adapter.users = usersState.users
            }
        }

        viewModel.deleteUserLiveData.observe(this)
        { deleteState ->
            when (deleteState.state) {
                DeleteActionState.State.IN_PROGRESS -> {
                } // show progress
                DeleteActionState.State.SUCCESS -> showToast(R.string.delete_success)
                DeleteActionState.State.ERROR -> showToast(R.string.delete_error)
            }
        }

        disposables.add(
            viewModel.createUserStream
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe
                {
                    if (it == CreateUserState.Success) {
                        viewModel.loadUsers()
                    }
                }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add -> CreateUserFragment().apply {
                show(supportFragmentManager, TAG)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showToast(@StringRes messageRes: Int) {
        Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    companion object {
        const val TAG = "create_user"
    }

    override fun onUserAction(userAction: UserAction) {
        when (userAction) {
            is UserAction.Delete -> {
                AlertDialog.Builder(this)
                    .setMessage(R.string.confirm_delete)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(
                        android.R.string.ok
                    ) { _, _ ->
                        viewModel.deleteUser(userAction.user)
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            }
        }
    }
}