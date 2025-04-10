package com.gmwapp.dudeways.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.androidbrowserhelper.trusted.LauncherActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.adapter.HomeProfilesAdapter.ItemHolder
import com.gmwapp.dudeways.databinding.ActivityPurchasepointBinding
import com.gmwapp.dudeways.databinding.ItemPurchaseBinding
import com.gmwapp.dudeways.databinding.LayoutHomeProfilesBinding
import com.gmwapp.dudeways.extentions.isNetworkAvailable
import com.gmwapp.dudeways.model.PurchaseModel
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.ChatViewModel
import com.gmwapp.dudeways.viewmodel.EarningViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PurchasepointActivity : BaseActivity() {

    lateinit var binding: ActivityPurchasepointBinding
    lateinit var mContext: PurchasepointActivity
    lateinit var session: Session
    private var purchaseList: ArrayList<PurchaseModel> = arrayListOf()
    private val viewModel: ChatViewModel by viewModels()
    private val viewModel1: EarningViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_purchasepoint)
        mContext = this
        initUI()
        addListner()
        addObsereves()
    }

    private fun initUI() {
        session = Session(mContext)


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

    private fun addListner() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
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

        viewModel.purchaseLiveData.observe(this, Observer {
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

        viewModel.addPointsLiveData.observe(this, Observer {
            println("Long URL: ${it.longurl}") // Print to the terminal
            //Toast.makeText(mContext, it.longurl, Toast.LENGTH_SHORT).show()
            val intent = Intent(mContext, LauncherActivity::class.java)
            intent.setData(Uri.parse(it.longurl))
            startActivity(intent)
            finish()// Directly starting the intent without launcher

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

        override fun onBindViewHolder(holder: ItemHolder, position: Int)
        {
            val item = list[position]
            holder.binding.tvTitle.text = "" + item.points + " Points"

            if (item.offer_percentage == "0") {
                holder.binding.tvPercentage.visibility = View.GONE
            } else {
                holder.binding.tvPercentage.visibility = View.VISIBLE
                holder.binding.tvPercentage.text = item.offer_percentage + "% OFF"
            }


            holder.binding.tvPrice.text = "₹" + item.price

            holder.itemView.setOnClickListener {
//                addpurchase(item.id)

//                val intent = Intent(activity, MainActivity::class.java)
//                intent.putExtra("id", item.id)
//                session.setData(Constant.AMOUNT,item.price)
//                startActivity(intent)

            }

            holder.itemView.setOnClickListener {

                val  amount = item.price
                val pointsId = item.id

                if (session.getData(Constant.MOBILE)?.isNotEmpty() == true)
                {
                    viewModel.addPoints(
                        session.getData(Constant.NAME).toString(),
                        amount,
                        session.getData(Constant.EMAIL).toString(),
                        session.getData(Constant.MOBILE).toString(),
                        session.getData(Constant.USER_ID).toString() + "-" + pointsId
                    )

                }
                else{
                    showMobileInputDialog(item.id, item.price)
                }

            }

        }

        private fun showMobileInputDialog(pointsId: String, amount: String) {
            // Inflate custom dialog layout
            val dialogView =
                LayoutInflater.from(activity).inflate(R.layout.custom_mobile_dialog, null)

            // Find the views
            val etMobileNumber: EditText = dialogView.findViewById(R.id.etMobileNumber)
            val btnSubmit: Button = dialogView.findViewById(R.id.btnSubmit)
            val btnCancel: Button = dialogView.findViewById(R.id.btnCancel)

            // Create AlertDialog with the custom layout
            val dialog = AlertDialog.Builder(activity)
                .setView(dialogView)
                .create()

            // Set click listener for submit button
            btnSubmit.setOnClickListener {
                val mobileNumber = etMobileNumber.text.toString().trim()

                // Validate the mobile number
                if (mobileNumber.isEmpty()) {
                    Toast.makeText(activity, "Please enter mobile number", Toast.LENGTH_SHORT)
                        .show()
                } else if (mobileNumber.length != 10) {
                    Toast.makeText(
                        activity,
                        "Please enter a valid mobile number",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    session.setData(Constant.MOBILE, mobileNumber)

                    viewModel1.updateMobileData(
                        session.getData(Constant.USER_ID).toString(),
                        etMobileNumber.text.toString()
                    )

                    viewModel.addPoints(
                        session.getData(Constant.NAME).toString(),
                        amount,
                        session.getData(Constant.EMAIL).toString(),
                        session.getData(Constant.MOBILE).toString(),
                        session.getData(Constant.USER_ID).toString() + "-" + pointsId
                    )
                    // initiatePaymentLink(pointsId, amount)
                    dialog.dismiss() // Dismiss the dialog after submitting
                }
            }

            // Set click listener for cancel button
            btnCancel.setOnClickListener {
                dialog.dismiss() // Dismiss the dialog when cancelled
            }

            // Show the dialog
            dialog.show()
        }


        override fun getItemCount(): Int {
            return list.size
        }

        inner class ItemHolder(val binding: ItemPurchaseBinding) :
            RecyclerView.ViewHolder(binding.root) {
        }
    }

}