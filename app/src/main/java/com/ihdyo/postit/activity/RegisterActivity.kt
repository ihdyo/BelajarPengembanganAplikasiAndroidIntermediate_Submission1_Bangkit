package com.ihdyo.postit.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ihdyo.postit.R.string
import com.ihdyo.postit.data.remote.ApiResponse
import com.ihdyo.postit.data.remote.auth.AuthBody
import com.ihdyo.postit.databinding.ActivityRegisterBinding
import com.ihdyo.postit.utils.UiConstValue
import com.ihdyo.postit.utils.ext.isEmailValid
import com.ihdyo.postit.utils.ext.showOKDialog
import com.ihdyo.postit.utils.ext.showToast
import com.ihdyo.postit.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private val registerViewModel: RegisterViewModel by viewModels()

    private var _activityRegisterBinding: ActivityRegisterBinding? = null
    private val binding get() = _activityRegisterBinding!!

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, RegisterActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _activityRegisterBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(_activityRegisterBinding?.root)

        initAction()
    }

    private fun initAction() {
        binding.btnRegister.setOnClickListener {
            val userName = binding.edtName.text.toString()
            val userEmail = binding.edtEmail.text.toString()
            val userPassword = binding.edtPassword.text.toString()

            Handler(Looper.getMainLooper()).postDelayed({
                when {
                    userName.isBlank() -> binding.edtName.error = getString(string.empty_name)
                    userEmail.isBlank() -> binding.edtEmail.error = getString(string.empty_email)
                    !userEmail.isEmailValid() -> binding.edtEmail.error = getString(string.invalid_email)
                    userPassword.isBlank() -> binding.edtPassword.error = getString(string.empty_password)
                    else -> {
                        val request = AuthBody(
                            userName, userEmail, userPassword
                        )
                        registerUser(request)
                    }
                }
            }, UiConstValue.ACTION_DELAYED_TIME)
        }
        binding.tvToLogin.setOnClickListener {
            LoginActivity.start(this)
        }
    }

    private fun registerUser(newUser: AuthBody) {
        registerViewModel.registerUser(newUser).observe(this) { response ->
            when(response) {
                is ApiResponse.Loading -> {
                    showLoading(true)
                }
                is ApiResponse.Success -> {
                    try {
                        showLoading(false)
                    } finally {
                        LoginActivity.start(this)
                        finish()
                        showToast(getString(string.success))
                    }
                }
                is ApiResponse.Error -> {
                    showLoading(false)
                    showOKDialog(getString(string.error), response.errorMessage)
                }
                else -> {
                    showToast(getString(string.unknown_state))
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.bgDim.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.edtName.isClickable = !isLoading
        binding.edtName.isEnabled = !isLoading
        binding.edtEmail.isClickable = !isLoading
        binding.edtEmail.isEnabled = !isLoading
        binding.edtPassword.isClickable = !isLoading
        binding.edtPassword.isEnabled = !isLoading
        binding.btnRegister.isClickable = !isLoading
    }

}