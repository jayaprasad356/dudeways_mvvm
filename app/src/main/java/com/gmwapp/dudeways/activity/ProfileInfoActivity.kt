package com.gmwapp.dudeways.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityProfileInfoBinding
import com.gmwapp.dudeways.extentions.isNetworkAvailable
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.AddFriendViewModel
import com.gmwapp.dudeways.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileInfoActivity : BaseActivity() {

    lateinit var binding: ActivityProfileInfoBinding
    lateinit var mContext: ProfileInfoActivity

    lateinit var session: Session
    var user_id: String? = ""
    var unique_name: String? = ""
    var name: String? = ""
    var profile: String? = ""
    var friend_verified: String? = ""
    var friend: String? = ""
    var friends: String? = ""

    private val viewModel: AddFriendViewModel by viewModels()

    private val chatViewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_info)
        mContext = this
        initUI()
        addListner()
        addObsereves()
    }

    private fun initUI() {
        session = Session(mContext)

        user_id = intent.getStringExtra("chat_user_id")

        if (isNetworkAvailable(mContext)) {
            chatViewModel.otherUserDetail(
                session.getData(Constant.USER_ID).toString(),
                user_id.toString()
            )
        } else {
            Toast.makeText(
                mContext, getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }


        val id = intent.getStringExtra("id")

        binding.rlAddFriend.setOnClickListener {
            // Change the background of rlAddFriend
            if (friend == "0") {
                friends = "1"
                friend = "1"
                viewModel.addFriend(
                    session.getData(Constant.USER_ID).toString(),
                    user_id.toString(),
                    friends.toString()
                )
            } else if (friend == "1") {
                friends = "2"
                friend = "0"
                viewModel.addFriend(
                    session.getData(Constant.USER_ID).toString(),
                    user_id.toString(),
                    friends.toString()
                )
            }


        }

        binding.rlChat.setOnClickListener {
            if (user_id == session.getData(Constant.USER_ID)) {
                Toast.makeText(mContext, "You can't chat with yourself", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(mContext, ChatsActivity::class.java)
                intent.putExtra("id", id)
                intent.putExtra("name", name)
                session.setData("reciver_profile", profile)
                intent.putExtra("chat_user_id", user_id)
                intent.putExtra("unique_name", unique_name)
                intent.putExtra("friend_verified", friend_verified)
                startActivity(intent)
            }
        }

        binding.tvmore.visibility = View.GONE

//        binding.tvmore.setOnClickListener {
//            if (binding.tvDescription.visibility == View.VISIBLE) {
//                binding.tvDescription.visibility = View.GONE
//                binding.tvmore.text = activity.getString(R.string.more)
//            } else {
//                binding.tvDescription.visibility = View.VISIBLE
//                binding.tvmore.text = activity.getString(R.string.less)
//            }
//        }

    }

    private fun addObsereves() {
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        chatViewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        chatViewModel.otherUserDetailLiveData.observe(this, Observer {
            if (it.success) {
                Glide.with(mContext).load(it.data.cover_img).placeholder(R.drawable.placeholder_bg)
                    .into(binding.ivCover)
                Glide.with(mContext).load(it.data.profile)
                    .placeholder(R.drawable.profile_placeholder).into(binding.civProfile)


                val gender = it.data.gender
                val age = it.data.age
                val verify = it.data.verified

                        if (verify == "1") {
                            binding.ivVerify.visibility = View.VISIBLE
                        } else {
                            binding.ivVerify.visibility = View.GONE
                        }

                binding.ivAge.text = age

                if (gender == "male") {
                    binding.ivGender.setBackgroundDrawable(resources.getDrawable(R.drawable.male_ic))
                } else {
                    binding.ivGender.setBackgroundDrawable(resources.getDrawable(R.drawable.female_ic))
                }

                if (gender == "male" || gender == "Male") {
                    binding.ivGenderColor.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.blue_200)
                    )
                } else {
                    binding.ivGenderColor.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.primary)
                    )
                }


                binding.tvName.text = it.data.name
                binding.tvProfessional.text = it.data.profession
//              binding.tvCity.text = jsonobj.getString(Constant.CITY)
//              binding.tvState.text = jsonobj.getString(Constant.STATE)
                binding.tvGender.text = it.data.gender
                binding.tvUsername.text = "@" + it.data.unique_name
                binding.tvPlace.text = it.data.city + ", " + it.data.state
                binding.tvAbout.text = it.data.introduction
                binding.tvDescription.text = it.data.introduction
                binding.tvAge.text = it.data.age


                unique_name = it.data.unique_name
                name = it.data.name
                profile = it.data.profile
                friend_verified = it.data.verified
                friend = it.data.friend_status


                var friend_data = "" + friend

                //   Toast.makeText(activity, friend_data, Toast.LENGTH_SHORT).show()

                if (friend_data == "0") {
                    binding.ivaddFriend.setBackgroundResource(R.drawable.add_frinds)
                    binding.tvAddFriend.text = "Add to Friend"
                } else if (friend_data == "1") {
                    binding.ivaddFriend.setBackgroundResource(R.drawable.frinds_added)
                    binding.tvAddFriend.text = "Friend Added"
                }


            } else {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.addFriendLiveData.observe(this, Observer {
            if (it.success) {
                if (friend == "1") {
                    binding.ivaddFriend.setBackgroundResource(R.drawable.frinds_added)
                    binding.tvAddFriend.text = "Friend Added"

                } else if (friend == "2") {
                    binding.ivaddFriend.setBackgroundResource(R.drawable.add_frinds)
                    binding.tvAddFriend.text = "Add to Friend"
                }

                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT)
                    .show()

            } else {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addListner() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.civProfile.setOnClickListener {
            // Set the image resource to the fullscreen image view
            binding.fullscreenImage.setImageDrawable(binding.civProfile.drawable)

            // Show the fullscreen container
            binding.fullscreenContainer.visibility = View.VISIBLE

            // Load and apply the zoom animation
            val zoomInAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
            binding.fullscreenImage.startAnimation(zoomInAnimation)
        }

        binding.fullscreenContainer.setOnClickListener {
            // Load and apply the zoom out animation
            val zoomOutAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_out)
            binding.fullscreenImage.startAnimation(zoomOutAnimation)

            // Set a listener to hide the container after the animation ends
            zoomOutAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    binding.fullscreenContainer.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        }


    }
}