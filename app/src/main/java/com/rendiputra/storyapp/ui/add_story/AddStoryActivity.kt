package com.rendiputra.storyapp.ui.add_story

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.rendiputra.storyapp.R
import com.rendiputra.storyapp.databinding.ActivityAddStoryBinding
import com.rendiputra.storyapp.domain.Response
import com.rendiputra.storyapp.ui.auth.AuthViewModel
import com.rendiputra.storyapp.ui.camera.CameraActivity
import com.rendiputra.storyapp.ui.camera.CameraActivity.Companion.CAMERA_X_RESULT
import com.rendiputra.storyapp.ui.home.MainActivity
import com.rendiputra.storyapp.util.reduceFileImage
import com.rendiputra.storyapp.util.rotateBitmap
import com.rendiputra.storyapp.util.uriToFile
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.concurrent.Executors

@AndroidEntryPoint
class AddStoryActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        enum class PhotoSource {
            CAMERA, GALLERY
        }
    }

    private lateinit var binding: ActivityAddStoryBinding

    private val authViewModel: AuthViewModel by viewModels()
    private val addStoryViewModel: AddStoryViewModel by viewModels()

    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    private var selectedPhotoSource: PhotoSource = PhotoSource.CAMERA

    private var storyImage: File? = null
    private var token: String? = null
    private var isBackCamera = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = authViewModel.authToken.value

        setupToolbar()
        setupButtonsPhoto()
        observeUploadNewStoryState()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_camera -> selectedPhotoSource = PhotoSource.CAMERA
            R.id.btn_gallery -> selectedPhotoSource = PhotoSource.GALLERY
            R.id.btn_photo -> startPhoto()
            R.id.btn_add_story -> uploadNewStory()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupButtonsPhoto() {
        arrayOf(binding.btnCamera, binding.btnGallery, binding.btnPhoto, binding.btnAddStory)
            .forEach { button -> button.setOnClickListener(this) }
    }

    private fun startPhoto() {
        when (selectedPhotoSource) {
            PhotoSource.CAMERA -> startCamera()
            PhotoSource.GALLERY -> startGallery()
        }
    }

    private fun observeUploadNewStoryState() {
        addStoryViewModel.uploadNewStoryState.observe(this) { response ->
            when (response) {
                is Response.Loading -> toggleLoading(true)
                is Response.Empty -> toggleLoading(false)
                is Response.Success -> {
                    toggleLoading(false)
                    Snackbar.make(binding.root, response.data, Snackbar.LENGTH_LONG).show()
                    navigateToHomeScreen()
                }
                is Response.Error -> {
                    toggleLoading(false)
                    response.message?.let {
                        Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun navigateToHomeScreen() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun toggleLoading(state: Boolean) {
        val progressVisibility = if (state) View.VISIBLE else View.GONE
        binding.progressBar.visibility = progressVisibility
        binding.btnAddStory.isEnabled = !state
    }

    private fun startGallery() {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImage: Uri = result.data?.data as Uri
            val imageFile = uriToFile(selectedImage, this)
            storyImage = imageFile
            binding.ivStory.setImageURI(selectedImage)
        }
    }

    @Suppress("DEPRECATION")
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode != CAMERA_X_RESULT) return@registerForActivityResult
        executor.execute {
            val selectedImage = it.data?.getSerializableExtra("picture") as File
            storyImage = selectedImage
            isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            val result = rotateBitmap(
                BitmapFactory.decodeFile(selectedImage.path),
                isBackCamera
            )
            handler.post { binding.ivStory.setImageBitmap(result) }
        }
    }

    private fun uploadNewStory() {
        if (validateFormLogin()) return

        toggleLoading(true)

        executor.execute {
            val storyImage = reduceFileImage(storyImage as File, isBackCamera)
            val description = binding.edtDescription.text.toString()
            addStoryViewModel.uploadNewStory(
                "Bearer $token",
                storyImage,
                description
            )
        }
    }

    private fun validateFormLogin(): Boolean {
        if (storyImage == null) {
            Snackbar.make(binding.root, getString(R.string.image_empty), Snackbar.LENGTH_LONG).show()
            return true
        }

        if (binding.edtDescription.text?.isEmpty() == true) {
            Snackbar.make(binding.root, getString(R.string.validation_error_description), Snackbar.LENGTH_LONG).show()
            return true
        }

        return false
    }

}