package com.gmwapp.dudeways.New.Fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.HomeActivity
import com.gmwapp.dudeways.databinding.FragmentPurchasepointBinding
import com.gmwapp.dudeways.databinding.ItemPurchaseBinding
import com.gmwapp.dudeways.extentions.isNetworkAvailable
import com.gmwapp.dudeways.fragment.HomeFragment
import com.gmwapp.dudeways.model.PurchaseModel
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.ChatViewModel
import com.gmwapp.dudeways.viewmodel.EarningViewModel
import com.google.androidbrowserhelper.trusted.LauncherActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PurchasepointFragment : Fragment() {

    lateinit var binding: FragmentPurchasepointBinding
    private lateinit var mContext: Activity

    lateinit var session: Session
    private var purchaseList: ArrayList<PurchaseModel> = arrayListOf()
    private val viewModel: ChatViewModel by viewModels()
    private val viewModel1: EarningViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_purchasepoint, container, false)
        mContext = requireActivity()

        initUI()
        addListeners()
        addObservers()

        return binding.root
    }

    private fun initUI() {
        session = Session(mContext)
        (activity as HomeActivity).binding.rltoolbar.visibility = View.GONE
        (activity as HomeActivity).binding.bottomNavigationView.visibility = View.GONE

        // Set RecyclerView layout manager to GridLayoutManager with 2 columns
        val gridLayoutManager = GridLayoutManager(mContext, 3)
        binding.recyclerView.layoutManager = gridLayoutManager

        if (isNetworkAvailable(mContext)) {
            viewModel.getPurchase(session.getData(Constant.USER_ID).toString())
        } else {
            Toast.makeText(
                mContext, getString(R.string.str_error_internet_connections),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun addListeners() {
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun addObservers() {
        viewModel.isLoading.observe(requireActivity(), Observer {
            binding.pbLoadData.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.purchaseLiveData.observe(requireActivity(), Observer {
            if (it.success) {
                purchaseList.clear()
                purchaseList.addAll(it.data)
                val purchaseAdapter = PurchaseAdapter(mContext, purchaseList)
                binding.recyclerView.adapter = purchaseAdapter
            } else {
                Toast.makeText(
                    mContext,
                    it.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        viewModel.addPointsLiveData.observe(requireActivity(), Observer {
            println("Long URL: ${it.longurl}") // Debug log
            val intent = Intent(mContext, LauncherActivity::class.java)
            intent.data = Uri.parse(it.longurl)
            startActivity(intent)
        })
    }

    inner class PurchaseAdapter(
        private val activity: Activity,
        private val list: ArrayList<PurchaseModel>
    ) : RecyclerView.Adapter<PurchaseAdapter.ItemHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            return ItemHolder(
                ItemPurchaseBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            val item = list[position]
            holder.binding.tvTitle.text = "${item.points} Points"

            if (item.offer_percentage == "0") {
                holder.binding.tvPercentage.visibility = View.GONE
            } else {
                holder.binding.tvPercentage.visibility = View.VISIBLE
                holder.binding.tvPercentage.text = "${item.offer_percentage}% OFF"
            }

            holder.binding.tvPrice.text = "₹${item.price}"

            holder.itemView.setOnClickListener {
                val amount = item.price
                val pointsId = item.id

                if (session.getData(Constant.MOBILE)?.isNotEmpty() == true) {
                    viewModel.addPoints(
                        session.getData(Constant.NAME).toString(),
                        amount,
                        session.getData(Constant.EMAIL).toString(),
                        session.getData(Constant.MOBILE).toString(),
                        "${session.getData(Constant.USER_ID)}-$pointsId"
                    )
                } else {
                    showMobileInputDialog(item.id, item.price)
                }
            }
        }

        private fun showMobileInputDialog(pointsId: String, amount: String) {
            val dialogView = LayoutInflater.from(activity).inflate(R.layout.custom_mobile_dialog, null)

            val etMobileNumber: EditText = dialogView.findViewById(R.id.etMobileNumber)
            val btnSubmit: Button = dialogView.findViewById(R.id.btnSubmit)
            val btnCancel: Button = dialogView.findViewById(R.id.btnCancel)

            val dialog = AlertDialog.Builder(activity)
                .setView(dialogView)
                .create()

            btnSubmit.setOnClickListener {
                val mobileNumber = etMobileNumber.text.toString().trim()

                if (mobileNumber.isEmpty()) {
                    Toast.makeText(activity, "Please enter mobile number", Toast.LENGTH_SHORT).show()
                } else if (mobileNumber.length != 10) {
                    Toast.makeText(activity, "Please enter a valid mobile number", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    session.setData(Constant.MOBILE, mobileNumber)

                    viewModel1.updateMobileData(
                        session.getData(Constant.USER_ID).toString(),
                        mobileNumber
                    )

                    viewModel.addPoints(
                        session.getData(Constant.NAME).toString(),
                        amount,
                        session.getData(Constant.EMAIL).toString(),
                        mobileNumber,
                        "${session.getData(Constant.USER_ID)}-$pointsId"
                    )
                    dialog.dismiss()
                }
            }

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        override fun getItemCount(): Int = list.size

        inner class ItemHolder(val binding: ItemPurchaseBinding) :
            RecyclerView.ViewHolder(binding.root)
    }

    override fun onResume() {
        super.onResume()
        handleOnBackPressed()
    }

    private fun handleOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment())
                        .commit()
                }
            })
    }
}
