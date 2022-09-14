package com.fahamin.workmanagerpactice.NotifyWorkmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.fahamin.workmanagerpactice.R

class NotifyWorker(
    context: Context, workerParameters: WorkerParameters
) : Worker(context, workerParameters) {
    override fun doWork(): Result {
        val taskData = inputData
        val taskDataString = taskData.getString(
            MainActivity.MESSAGE_STATUS
        )

        showNotification("WorkManager", taskDataString.toString())
        val outputData = Data.Builder().putString(
            WORK_RESULT, "Task Finished"
        ).build()
        return Result.success(outputData)
    }

    private fun showNotification(task: String, desc: String) {
        val manager = applicationContext.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val channelID = "message_channel"
        val channelName = "message_name"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelID, channelName, NotificationManager
                    .IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(applicationContext, channelID)
            .setContentTitle(task)
            .setContentText(desc)
            .setSmallIcon(R.mipmap.ic_launcher)

        manager.notify(1, builder.build())
    }


    companion object {
        val WORK_RESULT = "WORK_RESULT"
    }
}