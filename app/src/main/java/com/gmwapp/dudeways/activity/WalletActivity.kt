package com.gmwapp.dudeways.activity

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.adapter.WithdrawalAdapter
import com.gmwapp.dudeways.databinding.ActivityWalletBinding
import com.gmwapp.dudeways.extentions.isNetworkAvailable
import com.gmwapp.dudeways.model.WithdrawModel
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WalletActivity : BaseActivity() {

    lateinit var binding: ActivityWalletBinding
    lateinit var mContext: WalletActivity
    lateinit var session: Session
    private val viewModel: WalletViewModel by viewModels()
    private var walletList: ArrayList<WithdrawModel> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet)
        mContext = this
        initUI()
        addObsereves()
    }

    private fun initUI() {
        session = Session(mContext)

        // Load withdrawal list initially
        if (isNetworkAvailable(mContext)) {
            viewModel.getWallet(session.getData(Constant.USER_ID).toString())
        } else {
            Toast.makeText(
                mContext, getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.tvBalanceAmount.text = "₹" + session.getData(Constant.BALANCE)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.tvAddBank.setOnClickListener {
            val intent = android.content.Intent(mContext, BankDetailsActivity::class.java)
            startActivity(intent)
        }

        binding.btnWithdraw.setOnClickListener {
            if (binding.etAmount.text.toString().isEmpty()) {
                binding.etAmount.error = "Please enter Amount"
                return@setOnClickListener
            } else {
                if (isNetworkAvailable(mContext)) {
                    viewModel.getWithdraw(
                        session.getData(Constant.USER_ID)
                            .toString(), binding.etAmount.text.toString().trim()
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

        viewModel.walletLiveData.observe(this, Observer {
            if (it.success) {
                walletList.clear()
                walletList.addAll(it.data)
                // Set up the RecyclerView
                val adapter =
                    WithdrawalAdapter(mContext, walletList) // Pass both the activity and the list
                binding.rvWithdrawStatus.adapter = adapter
                binding.rvWithdrawStatus.layoutManager = LinearLayoutManager(mContext)

            } else {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            }
        })


        viewModel.withdrawLiveData.observe(this, Observer {
            if (it.success){

                viewModel.getWallet(session.getData(Constant.USER_ID).toString())
                binding.tvBalanceAmount.text = "₹" + it.balance
                Toast.makeText(mContext,it.message,Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(mContext,it.message,Toast.LENGTH_SHORT).show()
            }
        })
    }
}