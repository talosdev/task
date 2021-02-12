package com.github.talosdev.task.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.github.talosdev.task.R
import com.github.talosdev.task.databinding.BottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable


class CreateUserFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: UsersViewModel
    private var binding: BottomSheetBinding? = null
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = BottomSheetBinding.bind(view)
        viewModel = ViewModelProvider(activity!!).get(UsersViewModel::class.java)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding?.run {
                    saveButton.isEnabled =
                        !(nameEdittext.text.isNullOrEmpty() || emailEdittext.text.isNullOrEmpty())
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        }

        binding!!.run {
            nameEdittext.addTextChangedListener(textWatcher)
            emailEdittext.addTextChangedListener(textWatcher)
            saveButton.isEnabled = false

            disposables.add(
                viewModel.createUserStream
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { createUserState ->
                        saveButton.isEnabled = true
                        when (createUserState) {
                            is CreateUserState.Success -> {
                                showToast(R.string.create_success)
                                dismiss()
                            }
                            is CreateUserState.Error -> {
                                createUserState.message?.let { showToast(it) } ?: run {
                                    showToast(R.string.create_error)
                                }
                            }
                        }
                    }
            )

            saveButton.setOnClickListener {
                saveButton.isEnabled = false
                viewModel.submitUser(nameEdittext.text.toString(), emailEdittext.text.toString())
            }

        }
    }

    private fun showToast(@StringRes messageRes: Int) {
        context?.let {
            Toast.makeText(it, messageRes, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showToast(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }
}