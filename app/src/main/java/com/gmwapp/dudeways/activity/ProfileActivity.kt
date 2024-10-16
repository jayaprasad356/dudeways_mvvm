package com.gmwapp.dudeways.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityProfileBinding
import com.gmwapp.dudeways.model.RegisterModel
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding
    lateinit var mContext: ProfileActivity
    lateinit var session: Session


    var filePath1: String? = null
    var imageUri: Uri? = null

    private val REQUEST_IMAGE_GALLERY = 2
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        mContext = this
        initUI()
        addListner()
        addObsereves()
    }

    private fun initUI() {
        session = Session(mContext)

    }

    private fun addListner() {
        binding.btnUpdateProfile.setOnClickListener {

            if (session?.getBoolean("is_profile_in") == true) {
                viewModel.doUpdateImage(
                    session.getData(Constant.USER_ID).toString(),
                    filePath1.toString()
                )
            } else {
                Toast.makeText(this, "Please select profile image", Toast.LENGTH_SHORT).show()
            }

        }

        binding.ivProfile.setOnClickListener {
            pickImageFromGallery()
        }

        binding.ibBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.tvSkip.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
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

        viewModel.updateImageLiveData.observe(this, Observer {
            if (it.success) {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
                setPrefData(it.data)
            } else {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setPrefData(data: RegisterModel) {
        session.setData(Constant.USER_ID, data.id)
        session.setData(Constant.NAME, data.name)
        session.setData(
            Constant.UNIQUE_NAME,
            data.unique_name
        )
        session.setData(Constant.EMAIL, data.email)
        session.setData(Constant.AGE, data.age)
        session.setData(Constant.GENDER, data.gender)
        session.setData(
            Constant.PROFESSION,
            data.profession
        )
        session.setData(Constant.STATE, data.state)
        session.setData(Constant.CITY, data.city)
        session.setData(Constant.MOBILE, data.mobile)
        session.setData(
            Constant.COVER_IMG,
            data.cover_img
        )
        session.setData(Constant.POINTS, data.points.toString())
        session.setData(
            Constant.REFER_CODE,
            data.refer_code
        )
        session.setData(
            Constant.INTRODUCTION,
            data.introduction
        )
        session.setBoolean("is_logged_in", true)
        val intent = Intent(mContext, HomeActivity::class.java)
        startActivity(intent)
        finish()

    }

    // onbackpressed
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
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
                CropImage.activity(imageUri)
                    .setAspectRatio(1, 1) // Set aspect ratio to 1:1 for a square crop
                    .setCropShape(CropImageView.CropShape.OVAL) // Set crop shape to oval
                    .start(this)
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result: CropImage.ActivityResult? = CropImage.getActivityResult(data)
                if (result != null) {
                    filePath1 = result.getUriFilePath(mContext, true)
                    val imgFile = File(filePath1)
                    if (imgFile.exists()) {
                        val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        binding.ivProfile.setImageBitmap(myBitmap)
                        binding.ivEditProfile.visibility = View.GONE
                        session!!.setBoolean("is_profile_in", true)
                    }
                }
            }
        }
    }


}