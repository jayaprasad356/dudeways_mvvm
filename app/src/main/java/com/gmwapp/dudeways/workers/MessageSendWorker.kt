package com.gmwapp.dudeways.workers

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.gmwapp.dudeways.model.AddChatResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.gmwapp.dudeways.repositories.ChatRepositories
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MessageSendWorker @Inject constructor(
    val chatRepositories: ChatRepositories,
    appContext: Context,
    val workerParams: WorkerParameters
) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            chatRepositories.addChat(
                workerParams.getInputData().getString("userId").toString(),
                workerParams.getInputData().getString("chatUserId").toString(),
                workerParams.getInputData().getString("unRead").toString(),
                workerParams.getInputData().getString("msgSeen").toString(),
                workerParams.getInputData().getString("message").toString(),
                object :
                    Callback<AddChatResponse> {
                    override fun onResponse(
                        call: Call<AddChatResponse>,
                        response: Response<AddChatResponse>
                    ) {
                        if (response.body() != null) {
                            Result.success()
                        } else {
                            if (runAttemptCount > 5) {
                                Result.failure();
                            } else {
                                Result.retry();
                            }
                        }
                    }

                    override fun onFailure(call: Call<AddChatResponse>, t: Throwable) {
                        if (runAttemptCount > 5) {
                            Result.failure();
                        } else {
                            Result.retry();
                        }
                    }
                }
            ).let {
                Result.success()
            }


        }

    }
}
