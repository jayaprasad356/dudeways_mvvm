package com.gmwapp.dudeways.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.adapter.TransactionAdapter
import com.gmwapp.dudeways.databinding.ActivityTransactionBinding
import com.gmwapp.dudeways.extentions.isNetworkAvailable
import com.gmwapp.dudeways.model.TransactionModel
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionActivity : BaseActivity() {

    private lateinit var binding: ActivityTransactionBinding
    private lateinit var session: Session
    private val viewModel: TransactionViewModel by viewModels()
    private val transactionList = ArrayList<TransactionModel>()
    private lateinit var transactionAdapter: TransactionAdapter
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transaction)
        session = Session(this)
        initUI()
        observeViewModel()
    }

    private fun initUI() {
        binding.ivBack.setOnClickListener { onBackPressed() }
        setupRecyclerView()
        setupSwipeRefreshLayout()
        loadTransactionList()
    }

    private fun loadTransactionList() {
        if (isLoading) return
        isLoading = true

        if (isNetworkAvailable(this)) {
            viewModel.getTransaction(session.getData(Constant.USER_ID).toString())
        } else {
            Toast.makeText(this, getString(R.string.str_error_internet_connections), Toast.LENGTH_SHORT).show()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) {
            binding.pbLoadData.isVisible = it
        }

        viewModel.transactionLiveData.observe(this) { result ->
            isLoading = false
            binding.swipeRefreshLayout.isRefreshing = false
            if (result.success) {
                transactionList.clear()
                transactionList.addAll(result.data)
                transactionAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvTransactionList.layoutManager = LinearLayoutManager(this)
        transactionAdapter = TransactionAdapter(this, transactionList)
        binding.rvTransactionList.adapter = transactionAdapter

        binding.rvTransactionList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                if (!isLoading && lastVisibleItemPosition == layoutManager.itemCount - 1) {
                    loadTransactionList()
                }
            }
        })
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadTransactionList()
        }
    }
}
