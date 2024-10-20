package com.gmwapp.dudeways.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityStage1Binding
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Stage1Activity : BaseActivity() {

    lateinit var binding: ActivityStage1Binding
    lateinit var mContext: Stage1Activity
    lateinit var session: Session

    // boolean llStep1 is clicked
    var isStep1Clicked = false

    // boolean llStep2 is clicked
    var isStep2Clicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_stage1)
        mContext = this
        initUI()
    }

    private fun initUI() {
        session = Session(mContext)


        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        Glide.with(mContext).load(session.getData(Constant.PROFILE))
            .placeholder(R.drawable.profile_placeholder).into(binding.civProfile)

        val proof1 = session.getData(Constant.SELFIE_IMAGE)
        val proof2 = session.getData(Constant.FRONT_IMAGE)
        val proof3 = session.getData(Constant.BACK_IMAGE)

        // if proof1 is not null then show the image

        if (proof1?.isEmpty() == true) {
            binding.ivProofCheck1.visibility = View.GONE

        } else {
            binding.ivProofCheck1.visibility = View.VISIBLE
        }

        if (proof2?.isEmpty() == true || proof3?.isEmpty() == true) {
            binding.ivProofCheck2.visibility = View.GONE
        } else {
            binding.ivProofCheck2.visibility = View.VISIBLE
        }



        binding.llStep1.setOnClickListener {

            if (proof1?.isEmpty() == true) {
                val intent = Intent(mContext, Stage2Activity::class.java)
                startActivity(intent)
                finish()
                return@setOnClickListener
            } else {

            }

        }

        binding.llStep2.setOnClickListener {


            if (proof2?.isEmpty() == true || proof3?.isEmpty() == true) {
                val intent = Intent(mContext, Stage3Activity::class.java)
                startActivity(intent)
                finish()
                return@setOnClickListener
            } else {

            }

        }





        binding.btnRequestVerification.setOnClickListener {
            if (isStep1Clicked && isStep2Clicked) {
                // Proceed to the next stage
                startActivity(Intent(this, Stage4Activity::class.java))
                finish()
            } else {
                Toast.makeText(mContext, "Please complete all the steps", Toast.LENGTH_SHORT).show()
            }
        }

    }

    // onbackpressed
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(mContext, IdverficationActivity::class.java)
        startActivity(intent)
        finish()

        // close the app
    }

}