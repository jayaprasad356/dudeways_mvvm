package com.gmwapp.dudeways.activity

import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityPurchaseverifybuttonBinding
import com.gmwapp.dudeways.utils.Session
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PurchaseverifybuttonActivity : AppCompatActivity() {

    lateinit var binding: ActivityPurchaseverifybuttonBinding
    lateinit var mContext: PurchaseverifybuttonActivity
    lateinit var session: Session
    lateinit var amount:String
    lateinit var id:String

    private val REQUEST_IMAGE_GALLERY = 2

    var imageUri: Uri? = null
    var filePath1: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_purchaseverifybutton)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI(){
        session = Session(mContext)
    }
    private fun addListner(){

    }
}