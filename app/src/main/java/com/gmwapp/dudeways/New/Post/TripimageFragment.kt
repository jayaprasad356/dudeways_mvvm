package com.gmwapp.dudeways.New.Post

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.Observer
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.TripCompletedActivity
import com.gmwapp.dudeways.databinding.FragmentTripimageBinding
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.TripViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@AndroidEntryPoint
class TripimageFragment : Fragment() {

    private lateinit var binding: FragmentTripimageBinding
    private val viewModel: TripViewModel by viewModels()
    private lateinit var session: Session
    private var filePath1: String? = null
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            CropImage.activity(it)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .start(requireContext(), this)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tripimage, container, false)
        session = Session(requireContext())

        initUI()
        addListeners()
        addObservers()

        return binding.root
    }

    private fun addObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.pbLoadData.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.tripImageLiveData.observe(viewLifecycleOwner) { response ->
            Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
            if (response.success) {
                val intent = Intent(requireContext(), TripCompletedActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }

        viewModel.addTripLiveData.observe(requireActivity(), Observer {
            if (it.success) {
                var id = it.data.id.toString()
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

             else {
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initUI() {
        val isUploaded = !filePath1.isNullOrEmpty()
        binding.cvUploadSelfie.apply {
            setCardBackgroundColor(ContextCompat.getColor(context, if (isUploaded) R.color.green_light else R.color.primary_extra_light))
            binding.ivUploadSelfie.setImageResource(if (isUploaded) R.drawable.check_ic else R.drawable.ic_camera)
            binding.tvUploadSelfie.apply {
                setTextColor(ContextCompat.getColor(context, if (isUploaded) R.color.green else R.color.primary))
                text = getString(if (isUploaded) R.string.uploaded else R.string.upload)
            }
            isEnabled = !isUploaded
        }
    }

    private fun addListeners() {
        binding.btnContinue.setOnClickListener {
            addTrip()
        }

        binding.cvUploadSelfie.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            val result: CropImage.ActivityResult? = CropImage.getActivityResult(data)
            filePath1 = result?.getUriFilePath(requireContext(), true)
            filePath1?.let { path ->
                val imgFile = File(path)
                if (imgFile.exists()) {
                    val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                    binding.ivUpload.setImageBitmap(myBitmap)
                    binding.btnContinue.isEnabled = true
                    initUI()
                }
            }
        }
    }

    private fun addTrip() {
        val user_id = session.getData(Constant.USER_ID)
        val tripTitle = arguments?.getString("nameTrip") ?: ""
        val tripLocation = arguments?.getString("location") ?: ""
        val tripDescription = arguments?.getString("discription") ?: ""
        val fromDate = arguments?.getString("fromDate") ?: ""
        val toDate = arguments?.getString("toDate") ?: ""
        val profileImage = "0"


        Log.d("addTrip", "User ID: $user_id Trip Title: $tripTitle Trip Location: $tripLocation Trip Description: $tripDescription From Date: $fromDate To Date: $toDate Profile Image: $profileImage")


        lifecycleScope.launch {
            if (profileImage != null) {
                viewModel.addTrip(
                    user_id.toString(),
                    "1",
                    fromDate.toString(),
                    toDate.toString(),
                    tripTitle.toString(),
                    tripDescription.toString(),
                    tripLocation.toString(),
                    profileImage.toString()
                )
            }
        }
    }
}
