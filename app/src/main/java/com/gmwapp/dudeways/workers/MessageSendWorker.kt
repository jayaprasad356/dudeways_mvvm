package com.gmwapp.dudeways.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.gmwapp.dudeways.repositories.ChatRepositories


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
                workerParams.getInputData().getString("message").toString()
            ).let {
                if (it.body() != null) {
                    Result.success()
                } else {
                    if (runAttemptCount > 5) {
                        Result.failure();
                    } else {
                        Result.retry();
                    }
                }
            }


        }

    }
}
