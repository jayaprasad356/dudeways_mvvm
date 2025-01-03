package com.gmwapp.dudeways.fragment

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.HomeActivity
import com.gmwapp.dudeways.adapter.ChatListAdapter
import com.gmwapp.dudeways.databinding.FragmentMessagesBinding
import com.gmwapp.dudeways.extentions.isNetworkAvailable
import com.gmwapp.dudeways.model.ChatModel
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessagesFragment : Fragment() {

    lateinit var binding: FragmentMessagesBinding
    private lateinit var activity: Activity
    private lateinit var session: Session
    private var offset = 0
    private val limit = 10
    private var isLoading = false
    private var total = 0
    private val chatList = ArrayList<ChatModel>()
    private lateinit var chatlistAdapter: ChatListAdapter
    private val viewModel: ChatViewModel by viewModels()
    private var currentDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_messages, container, false)
        initUI()
        addListener()
        addObservers()
        return binding.root
    }

    private fun initUI() {
        activity = requireActivity()
        session = Session(activity)

        (activity as HomeActivity).binding.rltoolbar.visibility = View.VISIBLE
        (activity as HomeActivity).binding.ivNotification.visibility = View.VISIBLE

        setupRecyclerView()
        setupSwipeRefreshLayout()

        if (isNetworkAvailable(activity)) {
            chatList()
        } else {
            Toast.makeText(
                activity,
                getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addListener() {
        // Add any required listeners here (e.g., button click listeners)
    }

    private fun addObservers() {
        viewModel.unreadMessageLiveData.observe(viewLifecycleOwner, Observer {
            if (it.success) {
                currentDialog?.dismiss()
                offset = 0
                chatList()
            }
        })

        viewModel.chatLiveData.observe(viewLifecycleOwner, Observer {
            isLoading = false
            binding.swipeRefreshLayout.isRefreshing = false

            if (it.success) {
                total = it.total.toInt()
                if (offset == 0) chatList.clear()

                // Prevent duplicate entries
                it.data.forEach { newItem ->
                    if (!chatList.contains(newItem)) chatList.add(newItem)
                }

                chatlistAdapter.notifyDataSetChanged()
                offset += limit // Increment offset after successful data load
            } else {
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvChat.layoutManager = linearLayoutManager

        chatlistAdapter = ChatListAdapter(activity, chatList)
        binding.rvChat.adapter = chatlistAdapter

        binding.rvChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (!isLoading && lastVisibleItemPosition == totalItemCount - 1 && offset < total) {
                    chatList()
                }
            }
        })
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            offset = 0
            chatList()
            showCustomDialog()
        }
    }

    private fun showCustomDialog() {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.chat_list_dialog, null)
        val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(activity)
            .setView(dialogView)
            .setCancelable(true)

        val dialog = dialogBuilder.create()
        dialog.show()

        val btnNo: TextView = dialogView.findViewById(R.id.btnNo)
        val btnYes: TextView = dialogView.findViewById(R.id.btnYes)

        btnNo.setOnClickListener {
            offset = 0
            chatList()
            dialog.dismiss()
        }

        btnYes.setOnClickListener {
            readChats(dialog)
        }
    }

    private fun readChats(dialog: Dialog) {
        currentDialog = dialog
        viewModel.getUnreadMessage(session.getData(Constant.USER_ID).toString())
    }

    private fun chatList() {
        if (isLoading) return
        isLoading = true

        Log.d("MessagesFragment", "Fetching chat list with offset: $offset")
        viewModel.getChat(
            session.getData(Constant.USER_ID).toString(),
            offset.toString(), limit.toString()
        )
    }

    override fun onResume() {
        super.onResume()
        offset = 0
        chatList()
    }

    override fun onStart() {
        super.onStart()
        if (isVisible) {
            offset = 0
            chatList()
        }
    }
}
