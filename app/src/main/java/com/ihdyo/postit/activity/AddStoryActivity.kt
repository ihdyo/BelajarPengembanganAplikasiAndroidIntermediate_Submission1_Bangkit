package com.ihdyo.postit.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ihdyo.postit.R.string
import com.ihdyo.postit.data.remote.ApiResponse
import com.ihdyo.postit.databinding.ActivityAddStoryBinding
import com.ihdyo.postit.utils.ConstVal.CAMERA_X_RESULT
import com.ihdyo.postit.utils.ConstVal.KEY_PICTURE
import com.ihdyo.postit.utils.ConstVal.REQUEST_CODE_PERMISSIONS
import com.ihdyo.postit.utils.SessionManager
import com.ihdyo.postit.utils.ext.gone
import com.ihdyo.postit.utils.ext.show
import com.ihdyo.postit.utils.ext.showOKDialog
import com.ihdyo.postit.utils.ext.showToast
import com.ihdyo.postit.utils.reduceFileImage
import com.ihdyo.postit.utils.uriToFile
import com.ihdyo.postit.viewmodel.StoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@Suppress("NAME_SHADOWING")
@AndroidEntryPoint
class AddStoryActivity : AppCompatActivity() {

    private val storyViewModel: StoryViewModel by viewModels()
    private var _activityAddStoryBinding: ActivityAddStoryBinding? = null
    private val binding get() = _activityAddStoryBinding!!
    private var uploadFile: File? = null
    private var token: String? = null
    private lateinit var pref: SessionManager

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AddStoryActivity::class.java)
            context.startActivity(intent)
        }

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityAddStoryBinding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(_activityAddStoryBinding?.root)

        pref = SessionManager(this)
        token = pref.getToken

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        initUI()
        initAction()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!allPermissionsGranted()) {
            showToast(getString(string.not_permitted))
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun initAction() {
        binding.btnOpenCamera.setOnClickListener {
            launchIntentCamera.launch(Intent(this, CameraActivity::class.java))
        }
        binding.btnOpenGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            val chooser = Intent.createChooser(intent, getString(string.choose_a_picture))
            launchIntentGallery.launch(chooser)
        }
        binding.btnUpload.setOnClickListener {
            uploadImage()
        }
    }

    private fun initUI() {
        title = getString(string.new_post)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private val launchIntentCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == CAMERA_X_RESULT) {
            result.data?.getSerializableExtra(KEY_PICTURE)?.let { file ->
                uploadFile = file as File
                val result = BitmapFactory.decodeFile(file.path)
                binding.imgPreview.setImageBitmap(result)
            }
        }
    }

    private val launchIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { selectedImg ->
                val file = uriToFile(selectedImg, this)
                uploadFile = file
                binding.imgPreview.setImageURI(selectedImg)
            }
        }
    }

    private fun uploadImage() {
        uploadFile?.let { file ->
            val reducedFile = reduceFileImage(file)
            val description = binding.edtStoryDesc.text.trim()

            if (description.isBlank()) {
                binding.edtStoryDesc.requestFocus()
                binding.edtStoryDesc.error = getString(string.empty_description)
                return
            }

            val descMediaTyped = description.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = reducedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart = MultipartBody.Part.createFormData(
                "photo",
                reducedFile.name,
                requestImageFile
            )

            storyViewModel.addNewStory("Bearer $token", imageMultipart, descMediaTyped).observe(this) { response ->
                when (response) {
                    is ApiResponse.Loading -> showLoading(true)
                    is ApiResponse.Success -> {
                        showLoading(false)
                        showToast(getString(string.success))
                        finish()
                    }
                    is ApiResponse.Error -> {
                        showLoading(false)
                        showOKDialog(getString(string.info), response.errorMessage)
                    }
                    else -> {
                        showLoading(false)
                        showToast(getString(string.unknown_state))
                    }
                }
            }
        } ?: showOKDialog(getString(string.information), getString(string.select_image))
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.progressBar.show() else binding.progressBar.gone()
        if (isLoading) binding.bgDim.show() else binding.bgDim.gone()
        binding.apply {
            btnUpload.isClickable = !isLoading
            btnUpload.isEnabled = !isLoading
            btnOpenGallery.isClickable = !isLoading
            btnOpenGallery.isEnabled = !isLoading
            btnOpenCamera.isClickable = !isLoading
            btnOpenCamera.isEnabled = !isLoading
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}