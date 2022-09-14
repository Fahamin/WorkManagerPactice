package com.fahamin.workmanagerpactice.NotifyWorkmanager


import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.*
import com.fahamin.workmanagerpactice.R
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    lateinit var worker: WorkManager
    lateinit var txt_input: TextView
    lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        txt_input = findViewById(R.id.txt_input)
        button = findViewById(R.id.btn_send)

        worker = WorkManager.getInstance(this)

        val powerConstraint = Constraints.Builder().setRequiresCharging(true).build()

        val taskData = Data.Builder().putString(MESSAGE_STATUS, "Notify Done.").build()

        val request = OneTimeWorkRequest.Builder(NotifyWorker::class.java)
            .setConstraints(powerConstraint).setInputData(taskData).build()

        button.setOnClickListener {
            worker.enqueue(request)
            //period work
            // scheduleWork("jj")
        }


        worker.getWorkInfoByIdLiveData(request.id).observe(this, Observer { workInfo ->
            workInfo?.let {
                if (it.state.isFinished) {
                    val outputData = it.outputData
                    val taskResult = outputData.getString(NotifyWorker.WORK_RESULT)
                    txt_input.text = taskResult
                } else {
                    val workStatus = workInfo.state
                    txt_input.text = workStatus.toString()
                }
            }
        })
    }

    companion object {
        const val MESSAGE_STATUS = "message_status"
    }

    fun scheduleWork(tag: String) {
        val taskData = Data.Builder().putString(MESSAGE_STATUS, tag).build()

        val constraints = Constraints.Builder()
            .build()
        val work = PeriodicWorkRequestBuilder<NotifyWorker>(
            15, TimeUnit.MINUTES
        ).setConstraints(constraints).setInputData(taskData).build()

        worker.enqueueUniquePeriodicWork(
            tag,
            ExistingPeriodicWorkPolicy.KEEP, work
        )
    }
}