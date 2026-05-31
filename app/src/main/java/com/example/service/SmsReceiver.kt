package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.example.data.ShieldDatabase
import com.example.data.ShieldRepository
import com.example.data.SpamLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent) ?: return
        val db = ShieldDatabase.getDatabase(context)
        val repository = ShieldRepository(db)

        CoroutineScope(Dispatchers.IO).launch {
            for (sms in messages) {
                val sender = sms.originatingAddress ?: continue
                val body = sms.messageBody ?: ""

                // 1. Check if number is blacklisted in DB
                val registeredSpam = repository.getSpamNumber(sender)
                
                // 2. Or check for parental/spam keywords (intelligent cybersecurity heuristic)
                val hasSpamKeywords = containsSpamKeywords(body, repository)
                
                val isSpam = registeredSpam != null || hasSpamKeywords
                val label = registeredSpam?.label ?: if (hasSpamKeywords) "AI Filtr (Xavfli matn)" else "Xavfsiz"

                if (isSpam) {
                    // Log it to Database
                    repository.insertSpamLog(
                        SpamLog(
                            sender = sender,
                            message = body,
                            type = "SMS",
                            actionTaken = "BLOKLANDI"
                        )
                    )

                    // Launch SpamWarningActivity with fullscreen alert overlay style
                    val popupIntent = Intent(context, SpamWarningActivity::class.java).apply {
                        putExtra("INCOMING_NUMBER", sender)
                        putExtra("SPAM_LABEL", label)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    context.startActivity(popupIntent)
                    Log.d("SmsReceiver", "Spam SMS Blocked: $sender: $body")
                } else {
                    // Normal SMS logging
                    repository.insertSpamLog(
                        SpamLog(
                            sender = sender,
                            message = body,
                            type = "SMS",
                            actionTaken = "RUXSAT BERILDI"
                        )
                    )
                }
            }
        }
    }

    private suspend fun containsSpamKeywords(body: String, repository: ShieldRepository): Boolean {
        val savedKeywords = repository.getSetting(
            "spam_keywords",
            "1xbet,mostbet,melbet,yutuq,yutdingiz,yutib oling,million so'm,kredit,foizsiz,stavka,kazino,vaucher,promokod,sovga,shoshiling"
        )
        val keywords = savedKeywords.split(",").map { it.trim().lowercase() }.filter { it.isNotEmpty() }
        val text = body.lowercase()
        return keywords.any { text.contains(it) }
    }
}
