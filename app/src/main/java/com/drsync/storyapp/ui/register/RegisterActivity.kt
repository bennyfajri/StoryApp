package com.drsync.storyapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.drsync.storyapp.R
import com.drsync.storyapp.databinding.ActivityRegisterBinding
import com.drsync.storyapp.models.RegisterRequest
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()
    private var isError = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            setInputEmail()
            setInputPassword()
            btnRegister.setOnClickListener {
                val nama = etName.text.toString()
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                if (validateInput(nama)) {
                    registerUser(
                        RegisterRequest(nama, email, password)
                    )
                }

            }
            tvLogin.setOnClickListener {
                finish()
            }
            imageView.contentDescription =
                getString(R.string.image_description, getString(R.string.register))
        }
        showLoading()
        playAnimation()
    }

    private fun showLoading() {
        viewModel.isLoading.observe(this){
            binding.apply {
                progressBar.isVisible = it
                btnRegister.isEnabled = !it
            }
        }
    }

    private fun registerUser(registerRequest: RegisterRequest) {
        viewModel.registerUser(this@RegisterActivity, registerRequest){ success ->
            if(success){
                finish()
            }
        }
    }

    private fun validateInput(nama: String): Boolean {
        binding.apply {
            if (nama.isEmpty()) {
                ilName.isErrorEnabled = true
                ilName.error = getString(R.string.must_not_empty)
                return false
            }
            if (isError) {
                return false
            }
            return true
        }
    }

    private fun playAnimation() {
        binding.apply {
            ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_Y, -20f, 20f).apply {
                duration = 1500
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()

            val login = ObjectAnimator.ofFloat(textView, View.ALPHA, 1f).setDuration(200)
            val nameLable = ObjectAnimator.ofFloat(textView5, View.ALPHA, 1f).setDuration(150)
            val etName = ObjectAnimator.ofFloat(ilName, View.ALPHA, 1f).setDuration(150)
            val emailLable = ObjectAnimator.ofFloat(textView2, View.ALPHA, 1f).setDuration(150)
            val etEmail = ObjectAnimator.ofFloat(ilEmail, View.ALPHA, 1f).setDuration(150)
            val passwordLable = ObjectAnimator.ofFloat(textView3, View.ALPHA, 1f).setDuration(150)
            val etPassword = ObjectAnimator.ofFloat(ilPassword, View.ALPHA, 1f).setDuration(150)
            val btnRegister = ObjectAnimator.ofFloat(btnRegister, View.ALPHA, 1f).setDuration(150)
            val dontHaveAccount = ObjectAnimator.ofFloat(textView4, View.ALPHA, 1f).setDuration(150)
            val registerLabel = ObjectAnimator.ofFloat(tvLogin, View.ALPHA, 1f).setDuration(150)

            AnimatorSet().apply {
                playSequentially(
                    login,
                    nameLable,
                    etName,
                    emailLable,
                    etEmail,
                    passwordLable,
                    etPassword,
                    btnRegister,
                    dontHaveAccount,
                    registerLabel
                )
                start()
            }
        }
    }

    private fun setInputPassword() {
        binding.apply {
            etPassword.onValidateInput(
                activity = this@RegisterActivity,
                hideErrorMessage = {
                    ilPassword.isErrorEnabled = false
                    isError = false
                },
                setErrorMessage = {
                    ilPassword.error = it
                    ilPassword.isErrorEnabled = true
                    isError = true
                }
            )
        }
    }

    private fun setInputEmail() {
        binding.apply {
            etEmail.onValidateInput(
                activity = this@RegisterActivity,
                hideErrorMessage = {
                    ilEmail.isErrorEnabled = false
                    isError = false
                },
                setErrorMessage = {
                    ilEmail.error = it
                    ilEmail.isErrorEnabled = true
                    isError = true
                }
            )
        }
    }
}