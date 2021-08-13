package com.example.testnotification

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class NotificationViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    private val REQUEST_CODE = 0

    private val second: Long = 1_000L

    private val notifyIntent = Intent(app, AlarmReceiver::class.java)
    private val notifyPendingIntent = PendingIntent.getBroadcast(
        getApplication(),
        REQUEST_CODE,
        notifyIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    private val timerLengthOptions: IntArray = app.resources.getIntArray(R.array.int_array)
    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private val _timeSelection = MutableLiveData<Int>()x
    val timeSelection: LiveData<Int>
        get() = _timeSelection

    fun setTimeSelected(timerLengthSelection: Int) {
        _timeSelection.value = timerLengthSelection
    }

    fun sendNotification() {
        timeSelection.value?.let { startTimer(it) }
    }

    private fun startTimer(timerLengthSelection: Int) {
        val selectedInterval = timerLengthOptions[timerLengthSelection] * second
        val triggerTime = SystemClock.elapsedRealtime() + selectedInterval

        val notificationManager =
            ContextCompat.getSystemService(
                app,
                NotificationManager::class.java
            ) as NotificationManager
        notificationManager.cancelAll()

        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerTime,
            notifyPendingIntent
        )
    }
}
