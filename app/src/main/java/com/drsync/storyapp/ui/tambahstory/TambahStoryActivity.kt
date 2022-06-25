package com.drsync.storyapp.ui.tambahstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.drsync.storyapp.R
import com.drsync.storyapp.databinding.ActivityTambahStoryBinding
import com.drsync.storyapp.models.User
import com.drsync.storyapp.ui.main.MainActivity.Companion.INSERT_RESULT
import com.drsync.storyapp.util.Constant
import com.drsync.storyapp.util.Constant.reduceFileImage
import com.drsync.storyapp.util.Constant.tokenBearer
import com.drsync.storyapp.util.Constant.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@AndroidEntryPoint
class TambahStoryActivity : AppCompatActivity() {

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private lateinit var binding: ActivityTambahStoryBinding
    private val viewModel: TambahStoryViewModel by viewModels()
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        viewModel.getUser {
            user = it
        }

        binding.apply {
            btnCamera.setOnClickListener { startTakePhoto() }
            btnGallery.setOnClickListener { startGallery() }
            btnUpload.setOnClickListener { uploadImage() }
        }

        showLoading()
    }

    private fun showLoading() {
        viewModel.isLoading.observe(this){
            binding.apply {
                progressBar.isVisible = it
                btnUpload.isEnabled = !it
            }
        }
    }

    private fun uploadImage() {
        val description = binding.etDescription.text.toString()
        if(validateInput(description)){
            val file = reduceFileImage(getFile as File)

            val token = user?.tokenBearer.toString()
            val desc = description.toRequestBody("text/plain".toMediaType())
            val imageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                imageFile
            )

            viewModel.tambahStory(token, imageMultipart, desc){
                if(!it.error){
                    Toast.makeText(this@TambahStoryActivity, it.message, Toast.LENGTH_SHORT).show()
                    Intent().apply {
                        setResult(INSERT_RESULT, this)
                        finish()
                    }
                }else{
                    Toast.makeText(this@TambahStoryActivity, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun validateInput(description: String): Boolean {
        if(description.isEmpty()){
            binding.ilDescription.isErrorEnabled = true
            binding.ilDescription.error = getString(R.string.must_not_empty)
            return false
        }
        if(getFile == null){
            Toast.makeText(this@TambahStoryActivity, getString(R.string.choose_photo_first), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun startGallery() {
        Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
            val chooser = Intent.createChooser(this, "Choose a Picture")
            launcherIntentGallery.launch(chooser)
        }
    }

    private fun startTakePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(packageManager)

            Constant.createTempFile(application).also {
                val photoUri : Uri = FileProvider.getUriForFile(
                    this@TambahStoryActivity,
                    "com.drsync.storyapp",
                    it
                )
                currentPhotoPath = it.absolutePath
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                launcherIntentCamera.launch(intent)
            }
        }
    }

    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile

            val result =  BitmapFactory.decodeFile(myFile.path)
            //gunakan bila gambar mengalami perubahan rotasi
//            val result = rotateBitmap(
//                BitmapFactory.decodeFile(myFile.path),
//                true
//            )

            binding.imgStory.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode == RESULT_OK){
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@TambahStoryActivity)
            getFile = myFile

            binding.imgStory.setImageURI(selectedImg)
        }
    }
}