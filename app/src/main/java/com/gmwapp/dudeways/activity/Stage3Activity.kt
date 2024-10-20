package com.gmwapp.dudeways.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.canhub.cropper.CropImage
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityStage3Binding
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@AndroidEntryPoint
class Stage3Activity : BaseActivity() {

    lateinit var binding: ActivityStage3Binding
    lateinit var mContext: Stage3Activity
    private lateinit var session: Session

    private var filePath1: String? = null
    private var imageUri: Uri? = null // Define imageUri here
    private lateinit var imageBitmap: Bitmap

    private val REQUEST_IMAGE_GALLERY = 2

    private val viewModel: ChatViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_stage3)
        mContext = this
        initUI()
        addListner()
        addObsereves()
    }

    private fun initUI() {
        session = Session(mContext)
    }

    private fun addListner() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        session.setData(Constant.FrontPROOF, "0")
        session.setData(Constant.BackPROOF, "0")

        binding.cvFrontproof.setOnClickListener {
            session.setData(Constant.FrontPROOF, "0")
            pickImageFromGallery()
        }

        binding.cvBackproof.setOnClickListener {
            session.setData(Constant.BackPROOF, "0")
            pickImageFromGallery()
        }

        binding.btnUpload.setOnClickListener {

            if (session.getData(Constant.FrontPROOF) == "0") {
                Toast.makeText(mContext, "Please upload front proof", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (session.getData(Constant.BackPROOF) == "0") {
                Toast.makeText(mContext, "Please upload back proof", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                val uid: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    session.getData(Constant.USER_ID).toString()
                )

                var profileBody: MultipartBody.Part? = null
                if (filePath1?.isNotEmpty() == true) {
                    val file = File(filePath1)
                    // create RequestBody instance from file
                    val requestFile = RequestBody.create(
                        "multipart/form-data".toMediaTypeOrNull(), file
                    )
                    profileBody = MultipartBody.Part.createFormData(
                        Constant.FRONT_IMAGE, file.name, requestFile
                    )
                }

                viewModel.fontImage(uid, profileBody)
            }


        }
    }

    private fun addObsereves() {
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.fontImageLiveData.observe(this, Observer {
            if (it.success) {
                session.setData(
                    Constant.FRONT_IMAGE, it.data.front_image
                )
                val uid: RequestBody = RequestBody.create(
                    "multipart/form-data".toMediaTypeOrNull(),
                    session.getData(Constant.USER_ID).toString()
                )

                var profileBody: MultipartBody.Part? = null
                if (filePath1?.isNotEmpty() == true) {
                    val file = File(filePath1)
                    // create RequestBody instance from file
                    val requestFile = RequestBody.create(
                        "multipart/form-data".toMediaTypeOrNull(), file
                    )
                    profileBody = MultipartBody.Part.createFormData(
                        Constant.BACK_IMAGE, file.name, requestFile
                    )
                }

                viewModel.backImage(uid, profileBody)


            } else {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.backImageLiveData.observe(this, Observer {
            if (it.success) {
                session.setData(Constant.BACK_IMAGE, it.data.back_image)
                startActivity(Intent(this, PurchaseverifybuttonActivity::class.java))
                finish()

            } else {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            }
        })
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
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY) {
                imageUri = data?.data
                CropImage.activity(imageUri).start(mContext)
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result: CropImage.ActivityResult = CropImage.getActivityResult(data)!!
                filePath1 = result.getUriFilePath(mContext, true)
                val imgFile: File = File(filePath1)
                if (imgFile.exists()) {
                    val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)

                    if (session.getData(Constant.FrontPROOF) == "0") {
                        binding.ivFrontproof.setImageBitmap(myBitmap)
                        binding.ibFrontproof.visibility = View.GONE
                        session.setData(Constant.FrontPROOF, "1")
                        imageBitmap = myBitmap
                    } else if (session.getData(Constant.BackPROOF) == "0") {
                        binding.ivBackproof.setImageBitmap(myBitmap)
                        binding.ibBackproof.visibility = View.GONE
                        session.setData(Constant.BackPROOF, "1")
                        // You might want to assign the back proof bitmap here if needed
                    }
                }
            }
        }
    }

    private fun bitmapToFile(bitmap: Bitmap): File {
        // Create a file in the cache directory
        val file = File(mContext.cacheDir, "temp_image.jpg")

        // Write the bitmap to the file
        file.outputStream().use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }

        return file
    }
}