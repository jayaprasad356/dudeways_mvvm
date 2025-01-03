package com.gmwapp.dudeways.New

import android.content.Intent
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.BaseActivity
import com.gmwapp.dudeways.databinding.ActivityLogin2Binding
import com.gmwapp.dudeways.databinding.ActivityUpdateProfileBinding
import com.gmwapp.dudeways.extentions.isNetworkAvailable
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.ProfileViewModel
import com.gmwapp.dudeways.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


@AndroidEntryPoint
class UpdateProfileActivity : BaseActivity() {

    lateinit var binding: ActivityUpdateProfileBinding
    private lateinit var mContext: UpdateProfileActivity
    lateinit var session: Session

    var filePath1: String? = ""
    var imageUri: Uri? = null

    private val REQUEST_IMAGE_GALLERY = 2

    private val REQUEST_IMAGE_CAMERA = 3
    private var isCameraRequest = false

    private val viewModel: ProfileViewModel by viewModels()
    private val registerViewModel: RegisterViewModel by viewModels()
    private val profileLiveData: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_profile)
        mContext = this
        initUI(savedInstanceState)
        addListner()
        addObsereves()
    }

    private fun initUI(savedInstanceState: Bundle?) {
        session = Session(mContext)

        binding.etName.setText(session.getData(Constant.NAME))
        binding.tvGender.setText(session.getData(Constant.GENDER))
        binding.tvLanguage.setText(session.getData(Constant.LANGUAGE))
        binding.tvDob.setText(session.getData(Constant.DOB))

        val profile = session.getData(Constant.PROFILE)
        Glide.with(mContext).load(profile).placeholder(R.drawable.profile_placeholder)
            .into(binding.civProfile)

    }


    private fun addListner() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.ivAddProfile.setOnClickListener {
            isCameraRequest = false
            pickImageFromGallery()
        }

        binding.btnUpdate.setOnClickListener {


            if (isNetworkAvailable(mContext)) {
                registerViewModel.doUpdateProfile(
                    session.getData(Constant.USER_ID).toString(),
                    binding.etName.text.toString().trim()
                    )
            } else {
                Toast.makeText(
                    this, getString(R.string.str_error_internet_connections),
                    Toast.LENGTH_SHORT
                ).show()
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
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        profileLiveData.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        profileLiveData.profileLiveData.observe(this, Observer {
            if (it.success) {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
              }

        })

        registerViewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        registerViewModel.registerLiveData.observe(this, Observer {
            if (it.success) {
                session.setData(Constant.NAME, it.data.name)
                session.setData(Constant.UNIQUE_NAME, it.data.unique_name)
                session.setData(Constant.EMAIL, it.data.email)
                session.setData(Constant.AGE, it.data.age)
                session.setData(Constant.GENDER, it.data.gender)
                session.setData(Constant.PROFESSION, it.data.profession)
                session.setData(Constant.STATE, it.data.state)
                session.setData(Constant.CITY, it.data.city)
                session.setData(Constant.PROFILE, it.data.profile)
                session.setData(Constant.MOBILE, it.data.mobile)
                session.setData(Constant.REFER_CODE, it.data.refer_code)
                session.setData(Constant.REFERRED_BY, it.data.referred_by)
                Glide.with(this).load(session.getData(Constant.PROFILE))
                    .placeholder(R.drawable.profile_placeholder).into(binding.civProfile)

                // call resume() the HomeActivity
                onResume()


            } else {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
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
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY) {
                imageUri = data?.data
                if (isCameraRequest) {
                    // Configure CropImage for rectangular crop
                    CropImage.activity(imageUri)
                        .setAspectRatio(4, 2) // Set aspect ratio for a rectangle
                        .setCropShape(CropImageView.CropShape.RECTANGLE) // Set crop shape to rectangle
                        .start( this)
                } else {
                    // Configure CropImage for circular crop
                    CropImage.activity(imageUri)
                        .setAspectRatio(1, 1) // Set aspect ratio to 1:1 for a square crop
                        .setCropShape(CropImageView.CropShape.OVAL) // Set crop shape to oval
                        .start( this)
                }
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result: CropImage.ActivityResult? = CropImage.getActivityResult(data)
                if (result != null) {
                    filePath1 = result.getUriFilePath(this, true)
                    val imgFile = File(filePath1)
                    if (imgFile.exists()) {
                        val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        if (isCameraRequest) {
//                            binding.ivCover.setImageBitmap(myBitmap)
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
                                    Constant.COVER_IMG, file.name, requestFile
                                )
                            }


                            viewModel.getCoverImage(
                                uid,
                                profileBody
                            )
                        } else {
                            binding.civProfile.setImageBitmap(myBitmap)
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
                                    Constant.PROFILE, file.name, requestFile
                                )
                            }


                            //  Toast.makeText(requireActivity(), "$profileBody", Toast.LENGTH_SHORT).show()

                            registerViewModel.doUpdateImage(
                                uid,
                                profileBody
                            )
                        }
                    }
                }
            }
        }

    }
}
