package com.gmwapp.dudeways.activity

import android.content.Intent
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
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityDeactivateBinding
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.ProfileViewModel
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeactivateActivity : BaseActivity() {

    lateinit var binding: ActivityDeactivateBinding
    lateinit var mContext: DeactivateActivity
    private val viewModel: ProfileViewModel by viewModels()
    lateinit var session: Session


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_deactivate)
        mContext = this
        initUI()
        addObsereves()
    }

    private fun initUI() {
        session = Session(mContext)


        val cards = listOf(
            binding.cardEmoji1,
            binding.cardEmoji2,
            binding.cardEmoji3,
            binding.cardEmoji4,
            binding.cardEmoji5
        )

        cards.forEach { card ->
            card.setOnClickListener {
                selectCard(card, cards)
            }
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnfeedback.setOnClickListener {

            if (binding.etFeedback.text.toString().isEmpty()) {
                binding.etFeedback.error = "Please enter feedback"
                return@setOnClickListener
            } else {
                viewModel.addFeedBack(session.getData(Constant.USER_ID).toString(),
                    binding.etFeedback.text.toString().trim())
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

        viewModel.addFeedBackLiveData.observe(this, Observer {
            if (it.success){
                Toast.makeText(mContext,it.message,Toast.LENGTH_SHORT).show()
                val intent = Intent(mContext, HomeActivity::class.java)
                startActivity(intent)
                finish()

            }else{
                Toast.makeText(mContext,it.message,Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun selectCard(selectedCard: MaterialCardView, cards: List<MaterialCardView>) {
        val defaultColor = ContextCompat.getColor(this, R.color.white)
        val selectedColor = ContextCompat.getColor(this, R.color.feedback_select)

        cards.forEach { it.setCardBackgroundColor(defaultColor) }

        selectedCard.setCardBackgroundColor(selectedColor)

        when (selectedCard.id) {
            R.id.cardEmoji1 -> {

            }

            R.id.cardEmoji2 -> {

            }

            R.id.cardEmoji3 -> {

            }

            R.id.cardEmoji4 -> {

            }

            R.id.cardEmoji5 -> {

            }
        }
    }

}