package com.gmwapp.dudeways.activity

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.gmwapp.dudeways.DataBinderMapperImpl
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityBankDetailsBinding
import com.gmwapp.dudeways.extentions.isNetworkAvailable
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BankDetailsActivity : BaseActivity() {

    lateinit var binding: ActivityBankDetailsBinding
    lateinit var mContext: BankDetailsActivity
    lateinit var session: Session
    private val viewModel: WalletViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bank_details)
        mContext = this
        initUI()
        addObsereves()
    }

    private fun initUI() {
        session = Session(mContext)

        binding.etHolderName.text = Editable.Factory.getInstance().newEditable(
            session.getData(
                Constant.ACCOUNT_HOLDER_NAME
            )
        )
        binding.etAccountNumber.text =
            Editable.Factory.getInstance().newEditable(session.getData(Constant.ACCOUNT_NUMBER))
        binding.etIfsccode.text =
            Editable.Factory.getInstance().newEditable(session.getData(Constant.IFSC_CODE))
        binding.etBankName.text =
            Editable.Factory.getInstance().newEditable(session.getData(Constant.BANK_NAME))
        binding.etBranchName.text =
            Editable.Factory.getInstance().newEditable(session.getData(Constant.BRANCH_NAME))

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }


        binding.btnUpdate.setOnClickListener {
            if (binding.etHolderName.text.toString().isEmpty()) {
                binding.etHolderName.error = "Please enter Holder Name"
                return@setOnClickListener
            } else if (binding.etAccountNumber.text.toString().isEmpty()) {
                binding.etAccountNumber.error = "Please enter Account Number"
                return@setOnClickListener
            } else if (binding.etIfsccode.text.toString().isEmpty()) {
                binding.etIfsccode.error = "Please enter IFCS code"
                return@setOnClickListener
            } else if (binding.etBankName.text.toString().isEmpty()) {
                binding.etBankName.error = "Please enter Bank Name"
                return@setOnClickListener
            } else if (binding.etBranchName.text.toString().isEmpty()) {
                binding.etBranchName.error = "Please enter Branch Name"
                return@setOnClickListener
            } else {
                if (isNetworkAvailable(mContext)) {
                    viewModel.updateBank(
                        session.getData(Constant.USER_ID).toString(),
                        binding.etHolderName.text.toString(),
                        binding.etAccountNumber.text.toString(),
                        binding.etIfsccode.text.toString(),
                        binding.etBankName.text.toString(),
                        binding.etBranchName.text.toString()
                    )
                } else {
                    Toast.makeText(
                        mContext, getString(R.string.str_error_internet_connections),
                        Toast.LENGTH_SHORT
                    ).show()
                }

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

        viewModel.updateBankLiveData.observe(this, Observer {
            if (it.success) {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()

                // Update session data
                session.setData(Constant.ACCOUNT_HOLDER_NAME, binding.etHolderName.text.toString())
                session.setData(Constant.ACCOUNT_NUMBER, binding.etAccountNumber.text.toString())
                session.setData(Constant.IFSC_CODE, binding.etIfsccode.text.toString())
                session.setData(Constant.BANK_NAME, binding.etBankName.text.toString())
                session.setData(Constant.BRANCH_NAME, binding.etBranchName.text.toString())

            } else {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}