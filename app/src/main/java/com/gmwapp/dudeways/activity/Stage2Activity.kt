package com.gmwapp.dudeways.activity

import android.app.Activity
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
import androidx.lifecycle.Observer
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityStage1Binding
import com.gmwapp.dudeways.databinding.ActivityStage2Binding
import com.gmwapp.dudeways.extentions.isNetworkAvailable
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@AndroidEntryPoint
class Stage2Activity : BaseActivity() {

    lateinit var binding: ActivityStage2Binding
    lateinit var mContext: Stage2Activity
    lateinit var session: Session
    var imageUri: Uri? = null
    var isImageUploaded = false
    var filePath1: String? = null
    private val viewModel: ChatViewModel by viewModels()

    companion object {
        const val SELFIE_REQUEST_CODE = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_stage2)
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


        binding.cvProof1.setOnClickListener {
            startActivityForResult(Intent(this, SelfiActivity::class.java), SELFIE_REQUEST_CODE)
        }

        binding.btnUpload.setOnClickListener {
            if (isImageUploaded) {
                if (isNetworkAvailable(mContext)) {
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
                            Constant.SELFIE_IMAGE, file.name, requestFile
                        )
                    }

                    viewModel.selfiImage(uid, profileBody)


                } else {
                    Toast.makeText(
                        mContext, getString(R.string.str_error_internet_connections),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELFIE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imagePath = data?.getStringExtra(SelfiActivity.KEY_IMAGE_PATH)
            //  Toast.makeText(this, imagePath, Toast.LENGTH_SHORT).show()
            val bmImg = BitmapFactory.decodeFile(imagePath)
            filePath1 = imagePath
            binding.ivProof1.setImageBitmap(bmImg)
            isImageUploaded = true
            binding.ivAddProof1.visibility = View.GONE
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

        viewModel.selfiImageLiveData.observe(this, Observer {
            if (it.success) {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
                session.setData(Constant.SELFIE_IMAGE, it.data[0].selfie_image)
                startActivity(Intent(this, Stage1Activity::class.java))
                finish()

            } else {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

}