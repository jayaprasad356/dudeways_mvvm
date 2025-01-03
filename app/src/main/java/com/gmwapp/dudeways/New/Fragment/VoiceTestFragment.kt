package com.gmwapp.dudeways.New.Fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.gmwapp.dudeways.New.Fragment.VoiceVerficationFragment
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.SplashActivity
import com.gmwapp.dudeways.databinding.FragmentVoiceTestBinding
import com.gmwapp.dudeways.extentions.isNetworkAvailable
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.io.File
import java.io.IOException


@AndroidEntryPoint
class VoiceTestFragment : Fragment() {
    private lateinit var binding: FragmentVoiceTestBinding
    private lateinit var activity: Activity
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private lateinit var audioFilePath: String

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var session: Session

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_voice_test, container, false)
        activity = requireActivity()
        session = Session(activity)
        setupListeners()
        addObservers()

        return binding.root
    }

    private fun addObservers() {
        viewModel.isLoading.observe(requireActivity(), Observer { isLoading ->
            binding.pbLoadData.visibility = if (isLoading) View.VISIBLE else View.GONE
        })


        viewModel.voiceUpdateLiveData.observe(requireActivity(), Observer {
            if (it.success) {
               // Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
                val intent = Intent(requireActivity(), SplashActivity::class.java)
                startActivity(intent)
                requireActivity().finish()

            } else {
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun setupListeners() {
        binding.btnSubmit.setOnClickListener {
            mNetworkCallLoginAPI()
        }

        binding.ivRecord.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    checkPermissionsAndRecord()
                    binding.avRecharge.playAnimation()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (isRecording) {
                        stopRecording()
                        binding.avRecharge.pauseAnimation()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun checkPermissionsAndRecord() {
        if (ContextCompat.checkSelfPermission(
                activity, Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(activity, "Recording permission is required to use this feature.", Toast.LENGTH_LONG).show()
            }
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        } else {
            startRecording()
        }
    }

    private fun startRecording() {
        try {
            binding.btnSubmit.isEnabled = false
            val audioFile = File(
                activity.getExternalFilesDir(Environment.DIRECTORY_MUSIC),
                "voice_test_${System.currentTimeMillis()}.mp3"
            )
            audioFilePath = audioFile.absolutePath

            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setOutputFile(audioFilePath)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                prepare()
                start()
            }
            isRecording = true
            Toast.makeText(activity, "Recording started...", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Log.e("VoiceTestFragment", "Recording failed: ${e.message}")
            Toast.makeText(activity, "Recording failed", Toast.LENGTH_SHORT).show()
            binding.btnSubmit.isEnabled = true
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            binding.btnSubmit.isEnabled = true
          //  Toast.makeText(activity, "Recording saved: $audioFilePath", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("VoiceTestFragment", "Stop recording failed: ${e.message}")
            Toast.makeText(activity, "Failed to stop recording", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startRecording()
        } else {
            Toast.makeText(activity, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaRecorder?.release()
        mediaRecorder = null
    }

    private fun mNetworkCallLoginAPI() {

        val userId = createRequestBody(session.getData(Constant.USER_ID))


        if (isNetworkAvailable(requireActivity())) {
            if (audioFilePath.isNotEmpty()) {
                viewModel.addVoiceTest(userId, audioFilePath)
            } else {
                Toast.makeText(activity, "No voice file found to upload.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(
                requireActivity(),
                getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun createRequestBody(value: String?): RequestBody {
        return RequestBody.create("multipart/form-data".toMediaTypeOrNull(), value ?: "")
    }

}
