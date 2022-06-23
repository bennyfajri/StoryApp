package com.drsync.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.drsync.storyapp.R
import com.drsync.storyapp.databinding.ActivityLoginBinding
import com.drsync.storyapp.models.LoginRequest
import com.drsync.storyapp.ui.main.MainActivity
import com.drsync.storyapp.ui.register.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private var isError = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            setInputEmail()
            setInputPassword()

            btnLogin.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                val loginRequest = LoginRequest(email, password)

                if(!isError){
                    loginUser(loginRequest)
                }
            }

            tvRegister.setOnClickListener {
                Intent(this@LoginActivity, RegisterActivity::class.java).also {
                    startActivity(it)
                }
            }
            imageView.contentDescription =
                getString(R.string.image_description, getString(R.string.login))

            viewModel.getUser { user ->
                if(user.token.isNotEmpty()){
                    intentToMain()
                }
            }

        }
        showLoading()
        playAnimation()

    }

    private fun intentToMain() {
        Intent(this@LoginActivity, MainActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
        }
    }

    private fun loginUser(loginRequest: LoginRequest) {
        viewModel.loginUser(this@LoginActivity, loginRequest){ user ->
            intentToMain()
        }
    }

    private fun showLoading() {
        viewModel.isLoading.observe(this@LoginActivity) {
            binding.apply {
                btnLogin.isEnabled = !it
                progressBar.isVisible = it
            }
        }
    }

    private fun playAnimation() {
        binding.apply {
            ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_Y, -20f, 20f).apply {
                duration = 1500
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()

            val login = ObjectAnimator.ofFloat(textView, View.ALPHA, 1f).setDuration(500)
            val emailLable = ObjectAnimator.ofFloat(textView2, View.ALPHA, 1f).setDuration(200)
            val etEmail = ObjectAnimator.ofFloat(ilEmail, View.ALPHA, 1f).setDuration(200)
            val passwordLable = ObjectAnimator.ofFloat(textView3, View.ALPHA, 1f).setDuration(200)
            val etPassword = ObjectAnimator.ofFloat(ilPassword, View.ALPHA, 1f).setDuration(200)
            val btnLogin = ObjectAnimator.ofFloat(btnLogin, View.ALPHA, 1f).setDuration(200)
            val dontHaveAccount = ObjectAnimator.ofFloat(textView4, View.ALPHA, 1f).setDuration(200)
            val registerLabel = ObjectAnimator.ofFloat(tvRegister, View.ALPHA, 1f).setDuration(200)

            AnimatorSet().apply {
                playSequentially(
                    login,
                    emailLable,
                    etEmail,
                    passwordLable,
                    etPassword,
                    btnLogin,
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
                activity = this@LoginActivity,
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
                activity = this@LoginActivity,
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