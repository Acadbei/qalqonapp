package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.example.data.ShieldDatabase
import com.example.data.ShieldRepository
import com.example.data.SpamLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) return

        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) ?: return

        if (state == TelephonyManager.EXTRA_STATE_RINGING) {
            val db = ShieldDatabase.getDatabase(context)
            val repository = ShieldRepository(db)

            CoroutineScope(Dispatchers.IO).launch {
                val registeredSpam = repository.getSpamNumber(incomingNumber)
                if (registeredSpam != null) {
                    // Log to DB
                    repository.insertSpamLog(
                        SpamLog(
                            sender = incomingNumber,
                            message = "KIRUVCHI QO'NG'IROQ BLOKLANDI",
                            type = "QO'NG'IROQ",
                            actionTaken = "BLOKLANDI"
                        )
                    )

                    // Launch overlay reminder SpamWarningActivity
                    val popupIntent = Intent(context, SpamWarningActivity::class.java).apply {
                        putExtra("INCOMING_NUMBER", incomingNumber)
                        putExtra("SPAM_LABEL", registeredSpam.label)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    context.startActivity(popupIntent)
                    Log.d("CallReceiver", "Spam call warning started for: $incomingNumber")
                }
            }
        }
    }
}
