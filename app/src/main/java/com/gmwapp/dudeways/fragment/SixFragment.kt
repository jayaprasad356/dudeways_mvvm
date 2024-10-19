package com.gmwapp.dudeways.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.StarttripActivity
import com.gmwapp.dudeways.activity.TripCompletedActivity
import com.gmwapp.dudeways.databinding.FragmentSixBinding
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.TripViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@AndroidEntryPoint
class SixFragment : Fragment() {

    lateinit var binding: FragmentSixBinding
    lateinit var activity: Activity
    lateinit var session: Session

    var filePath1: String? = ""
    var imageUri: Uri? = null

    private val REQUEST_IMAGE_GALLERY = 2
    var trip_type: String? = ""
    private val viewModel: TripViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_six, container, false)
        initUI()
        addListner()
        addObsereves()
        return binding.root
    }

    private fun initUI() {

        activity = requireActivity()
        session = Session(activity)


        if (session.getData(Constant.PROFILE) != "") {
            binding.cbUseProfileImage.visibility = View.VISIBLE
        } else {
            binding.cbUseProfileImage.visibility = View.GONE
        }

        (activity as StarttripActivity).binding.btnNext.text = "Start Trip"

        binding.ivAddProof1.setOnClickListener {
            pickImageFromGallery()
        }

        binding.ivProof1.setOnClickListener {
            pickImageFromGallery()
        }

        binding.ivPost.setOnClickListener {
            pickImageFromGallery()
        }

        when (session.getData(Constant.TRIP_TYPE)) {
            "0" -> trip_type = "Road Trip"
            "1" -> trip_type = "Adventure Trip"
            "2" -> trip_type = "Explore Cities"
            "3" -> trip_type = "Airport Flyover"
        }


    }

    private fun addListner() {

    }

    private fun addObsereves() {
        viewModel.isLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })
        viewModel.tripImageLiveData.observe(requireActivity(), Observer {
            if (it.success) {
                val intent = Intent(activity, TripCompletedActivity::class.java)
                startActivity(intent)
                activity.finish()
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.addTripLiveData.observe(requireActivity(), Observer {
            if (it.success) {
                var id = it.data.id.toString()
                if (binding.cbUseProfileImage.isChecked) {
                    val intent = Intent(activity, TripCompletedActivity::class.java)
                    startActivity(intent)
                    activity.finish()
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                } else {
                    val uid: RequestBody = RequestBody.create(
                        "multipart/form-data".toMediaTypeOrNull(), id.toString()
                    )

                    var profileBody: MultipartBody.Part? = null
                    if (filePath1?.isNotEmpty() == true) {
                        val file = File(filePath1)
                        // create RequestBody instance from file
                        val requestFile = RequestBody.create(
                            "multipart/form-data".toMediaTypeOrNull(), file
                        )
                        profileBody = MultipartBody.Part.createFormData(
                            Constant.TRIP_IMAGE, file.name, requestFile
                        )
                    }

                    viewModel.updateTripImage(uid, profileBody)

                }

            } else {
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
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
                CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.RECTANGLE)
                    .start(requireContext(), this)

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result: CropImage.ActivityResult? = CropImage.getActivityResult(data)
                if (result != null) {
                    filePath1 = result.getUriFilePath(activity, true)
                    val imgFile = File(filePath1)
                    if (imgFile.exists()) {
                        val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        binding.ivPost.setImageBitmap(myBitmap)
                        binding.ivAddProof1.visibility = View.GONE
                        binding.ivProof1.visibility = View.INVISIBLE
                        //    binding.rlProfile.visibility = View.GONE
                    }
                }
            }
        }
    }

    fun addTrip() {
        val profileImage: String

        // Check if either the checkbox is checked or an image is selected
        if (binding.cbUseProfileImage.isChecked) {
            profileImage = "1"
            filePath1 = session.getData(Constant.PROFILE_IMAGE)
        } else {
            profileImage = "0"
        }

        if (!binding.cbUseProfileImage.isChecked && filePath1.isNullOrEmpty()) {
            Toast.makeText(
                activity,
                "Please select an image to upload or use your profile image.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        viewModel.addTrip(
            session.getData(Constant.USER_ID).toString(),
            trip_type.toString(), session.getData(Constant.TRIP_FROM_DATE).toString(),
            session.getData(Constant.TRIP_TO_DATE).toString(),
            session.getData(Constant.TRIP_TITLE).toString(),
            session.getData(Constant.TRIP_DESCRIPTION).toString(),
            session.getData(Constant.TRIP_LOCATION).toString(),
            profileImage.toString()
        )

    }

}