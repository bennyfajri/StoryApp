package com.drsync.storyapp.customview

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.drsync.storyapp.R

class PasswordEditText : AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private val errorMessage = MutableLiveData<String>()
    private val hideError = MutableLiveData<Boolean>()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        hint = context.getString(R.string.enter_password)
    }

    private fun init() {
        inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        transformationMethod = PasswordTransformationMethod()
        doAfterTextChanged { text ->
            if (text?.isEmpty() == true) {
                setErrorMessage(context.getString(R.string.must_not_empty))
            } else {
                if ((text?.length ?: 0) < 6) {
                    setErrorMessage(context.getString(R.string.more_than_character))
                } else {
                    hideErrorMessage()
                }
            }
        }
    }

    private fun hideErrorMessage() {
        hideError.value = true
    }

    private fun setErrorMessage(message: String) {
        errorMessage.value = message
    }

    fun onValidateInput(
        activity: Activity,
        hideErrorMessage: () -> Unit,
        setErrorMessage: (message: String) -> Unit
    ) {
        hideError.observe(activity as LifecycleOwner) { hideErrorMessage() }
        errorMessage.observe(activity as LifecycleOwner) { setErrorMessage(it) }
    }

}