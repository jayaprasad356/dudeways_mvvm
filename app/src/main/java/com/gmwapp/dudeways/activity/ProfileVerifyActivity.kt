package com.gmwapp.dudeways.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityProfileVerifyBinding
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.EarningViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class ProfileVerifyActivity : BaseActivity() {

    private lateinit var binding: ActivityProfileVerifyBinding
    private lateinit var session: Session
    private var proofImage: MultipartBody.Part? = null
    private var selfieImage: MultipartBody.Part? = null
    private var imageUri: Uri? = null // Added for storing the picked image URI

    private val viewModel: EarningViewModel by viewModels()

    var imagePath : String? = null
    var filePath : String? = null


    companion object {
        const val SELFIE_REQUEST_CODE = 1
        const val REQUEST_IMAGE_GALLERY = 2 // Constant for image gallery request
        const val CAMERA_PERMISSION_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_verify)
        session = Session(this)

        initUI()
        setupListeners()
        addObservers()
    }

    private fun addObservers() {
        viewModel.earningImageLiveData.observe(this) { response ->
            session.setData(Constant.SELFIE_IMAGE, imagePath)
            session.setData(Constant.FRONT_IMAGE, filePath)
            val intent = Intent(this, EarningActivity::class.java)
            startActivity(intent)
            finish()
            Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initUI() {
        val proof1 = imagePath
        val proof2 = filePath

        updateUI(proof1, proof2)
        binding.btnSubmit.isEnabled = !proof1.isNullOrEmpty() && !proof2.isNullOrEmpty()
        binding.btnSubmit.backgroundTintList = ContextCompat.getColorStateList(
            this, if (binding.btnSubmit.isEnabled) R.color.primary else R.color.grey
        )
    }

    private fun updateUI(proof1: String?, proof2: String?) {
        binding.cvUploadSelfie.apply {
            setCardBackgroundColor(
                ContextCompat.getColor(
                    context, if (proof1.isNullOrEmpty()) R.color.primary_extra_light else R.color.green_light
                )
            )
            binding.ivUploadSelfie.setImageResource(
                if (proof1.isNullOrEmpty()) R.drawable.ic_camera else R.drawable.check_ic
            )
            binding.tvUploadSelfie.setTextColor(
                ContextCompat.getColor(
                    context, if (proof1.isNullOrEmpty()) R.color.primary else R.color.green
                )
            )
            isEnabled = proof1.isNullOrEmpty()
        }

        binding.cvUploadIDProof.apply {
            setCardBackgroundColor(
                ContextCompat.getColor(
                    context, if (proof2.isNullOrEmpty()) R.color.primary_extra_light else R.color.green_light
                )
            )
            binding.ivUploadIDProof.setImageResource(
                if (proof2.isNullOrEmpty()) R.drawable.ic_camera else R.drawable.check_ic
            )
            binding.tvUploadIDProof.setTextColor(
                ContextCompat.getColor(
                    context, if (proof2.isNullOrEmpty()) R.color.primary else R.color.green
                )
            )
            isEnabled = proof2.isNullOrEmpty()
        }
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener { onBackPressed() }

        binding.cvUploadSelfie.setOnClickListener {
            if (session.getData(Constant.SELFIE_IMAGE).isNullOrEmpty()) {
                startActivityForResult(Intent(this, SelfiActivity::class.java), SELFIE_REQUEST_CODE)
            }
        }

        binding.cvUploadIDProof.setOnClickListener {
            if (session.getData(Constant.FRONT_IMAGE).isNullOrEmpty()) {
                pickImageFromGallery() // Change to picking image from gallery
            }
        }

        binding.btnSubmit.setOnClickListener {
            val uid = createRequestBody(session.getData(Constant.USER_ID))
            val type = createRequestBody("with_verification")

            if (selfieImage != null) {
                viewModel.doUpdateImage(uid, type, selfieImage, proofImage)
            } else {
                Toast.makeText(this, "Selfie image is missing", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createRequestBody(value: String?): RequestBody {
        return RequestBody.create("multipart/form-data".toMediaTypeOrNull(), value ?: "")
    }

    private fun pickImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_GALLERY
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SELFIE_REQUEST_CODE -> handleSelfieResult(data)
                REQUEST_IMAGE_GALLERY -> handleGalleryResult(data) // Handle gallery result
            }
        }
    }

    private fun handleSelfieResult(data: Intent?) {
        imagePath = data?.getStringExtra(SelfiActivity.KEY_IMAGE_PATH) ?: return

        val selfieFile = File(imagePath)
        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), selfieFile)
        selfieImage = MultipartBody.Part.createFormData(Constant.SELFIE_IMAGE, selfieFile.name, requestFile)

        initUI()
    }

    private fun handleGalleryResult(data: Intent?) {
        imageUri = data?.data
        if (imageUri != null) {
            // Save the image and create the MultipartBody.Part
             filePath = saveImageToFile(imageUri!!)

            val proofFile = File(filePath)
            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), proofFile)
            proofImage = MultipartBody.Part.createFormData(Constant.PROOF_IMAGE, proofFile.name, requestFile)

            initUI()
        } else {
            Toast.makeText(this, "Failed to get image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageToFile(uri: Uri): String {
        val file = File(cacheDir, "id_proof.jpg")
        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { out ->
                inputStream.copyTo(out)
            }
        }
        return file.absolutePath
    }
}
