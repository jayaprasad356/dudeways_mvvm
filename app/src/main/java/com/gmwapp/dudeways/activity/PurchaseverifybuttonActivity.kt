package com.gmwapp.dudeways.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.adapter.PlanListAdapter
import com.gmwapp.dudeways.databinding.ActivityPurchaseverifybuttonBinding
import com.gmwapp.dudeways.extentions.isNetworkAvailable
import com.gmwapp.dudeways.model.PlanListModel
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.ChatViewModel
import com.google.androidbrowserhelper.trusted.LauncherActivity
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@AndroidEntryPoint
class PurchaseverifybuttonActivity : BaseActivity(), PlanListAdapter.onItemClick {

    lateinit var binding: ActivityPurchaseverifybuttonBinding
    lateinit var mContext: PurchaseverifybuttonActivity
    lateinit var session: Session
    lateinit var amount: String
    lateinit var id: String

    private val REQUEST_IMAGE_GALLERY = 2

    var imageUri: Uri? = null
    var filePath1: String? = ""
    var upiId: String? = ""
    private val viewModel: ChatViewModel by viewModels()
    private var planList: ArrayList<PlanListModel> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_purchaseverifybutton)
        mContext = this
        initUI()
        addListner()
        addObsereves()
    }

    private fun initUI() {
        session = Session(mContext)
        upiId = session.getData(Constant.UPI_ID)
        binding.tvUpiId.text = upiId

        binding.rvplan.layoutManager = LinearLayoutManager(mContext)


    }

    private fun addListner() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
            finish()
        }

        binding.btnCopyUpi.setOnClickListener {
            copyTextToClipboard(upiId.toString())
        }

        binding.btnUploadScreenshots.setOnClickListener {
            pickImageFromGallery()
        }
        binding.btnSendReview.setOnClickListener {
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
                        Constant.PAYMENT_IMAGE, file.name, requestFile
                    )
                }
                viewModel.getPaymentImage(uid, profileBody)
            } else {
                Toast.makeText(
                    mContext, getString(R.string.str_error_internet_connections),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        if (isNetworkAvailable(mContext)) {
            viewModel.getPlan(session.getData(Constant.USER_ID).toString())
        } else {
            Toast.makeText(
                mContext, getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
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
                CropImage.activity(imageUri)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .start(this)

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result: CropImage.ActivityResult? = CropImage.getActivityResult(data)
                if (result != null) {
                    filePath1 = result.getUriFilePath(mContext, true)
                    val imgFile = File(filePath1)
                    if (imgFile.exists()) {
                        val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        binding.btnUploadScreenshots.text = getString(R.string.screenshot_uploaded)
                        binding.btnUploadScreenshots.setTextColor(
                            ContextCompat.getColor(
                                this,
                                R.color.darkGreen
                            )
                        )
                    }
                }
            }
        }
    }

    private fun copyTextToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Refer Text", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
    }

    private fun addObsereves() {
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.addPointsLiveData.observe(this, Observer {
            val intent = Intent(mContext, LauncherActivity::class.java)
            intent.setData(Uri.parse(it.longurl))
            startActivity(intent) // Directly starting the intent without launcher

        })

        viewModel.paymentImageLiveData.observe(this, Observer {
            if (it.success) {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
                val intent = Intent(mContext, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.planLiveData.observe(this, Observer {
            if (it.success) {
                planList.clear()
                planList.addAll(it.data)
                // Set up the RecyclerView with the adapter
                val adapter = PlanListAdapter(mContext, planList, this) { plan ->
                    //    showToast("${plan.plan_name} selected")

                    amount = plan.price.toString()
                    id = plan.id.toString()


                }
                binding.rvplan.adapter = adapter

            } else {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onClick() {
        viewModel.addPoints(
            "tamil", "10.00", "test@gmail.com",
            "6382088746", "1"
        )
    }

}