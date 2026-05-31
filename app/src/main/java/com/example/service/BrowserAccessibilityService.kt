package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.data.ShieldDatabase
import com.example.data.ShieldRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BrowserAccessibilityService : AccessibilityService() {

    private var db: ShieldDatabase? = null
    private var repository: ShieldRepository? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private var lastCheckedUrl = ""
    private var lastBlockedTimestamp = 0L

    override fun onServiceConnected() {
        super.onServiceConnected()
        db = ShieldDatabase.getDatabase(this)
        repository = ShieldRepository(db!!)
        Log.d("WebAccessibilityService", "Asadbei Shield Website Web-Filter Service Connected.")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (repository == null) return

        val packageName = event.packageName?.toString() ?: return
        
        // Supported browsers
        val browserPackages = listOf(
            "com.android.chrome",
            "org.mozilla.firefox",
            "com.opera.browser",
            "com.microsoft.emmx",
            "com.sec.android.app.sbrowser"
        )

        if (!browserPackages.contains(packageName)) return

        val sourceNode = event.source ?: return
        val url = findBrowserUrl(sourceNode, packageName) ?: return

        if (url.isNotEmpty() && url != lastCheckedUrl) {
            lastCheckedUrl = url
            serviceScope.launch {
                val blockedDomain = repository?.isDomainBlocked(url)
                if (blockedDomain != null) {
                    val currentTime = System.currentTimeMillis()
                    // Avoid quick double-starts
                    if (currentTime - lastBlockedTimestamp > 1500) {
                        lastBlockedTimestamp = currentTime
                        
                        // Log this block to DB
                        repository?.insertSpamLog(
                            com.example.data.SpamLog(
                                sender = blockedDomain.domain,
                                message = "Kirish bloklandi: $url",
                                type = "VEB_FILTR",
                                actionTaken = "BLOKLANDI"
                            )
                        )

                        // Fire up WebsiteBlockActivity
                        val blockIntent = Intent(applicationContext, WebsiteBlockActivity::class.java).apply {
                            putExtra("BLOCKED_DOMAIN", blockedDomain.domain)
                            putExtra("BLOCKED_REASON", blockedDomain.reason)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        }
                        startActivity(blockIntent)
                        Log.d("WebAccessibilityService", "Web-Filter blocked dangerous site: $url")
                    }
                }
            }
        }
    }

    override fun onInterrupt() {
        Log.d("WebAccessibilityService", "Asadbei Shield Web-Filter Interrupted.")
    }

    private fun findBrowserUrl(nodeInfo: AccessibilityNodeInfo, packageName: String): String? {
        // Broadly scan children for url fields
        val queue = ArrayDeque<AccessibilityNodeInfo>()
        queue.add(nodeInfo)

        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            
            // Chrome URL bar check
            if (packageName == "com.android.chrome") {
                val viewId = node.viewIdResourceName
                if (viewId != null && viewId.endsWith("url_bar")) {
                    val urlText = node.text?.toString()
                    if (!urlText.isNullOrEmpty()) {
                        return urlText
                    }
                }
            }

            // Fallback: If node text contains a URL structure
            val text = node.text?.toString()
            if (!text.isNullOrEmpty() && (text.startsWith("http://") || text.startsWith("https://") || text.contains(".co") || text.contains(".net") || text.contains(".org") || text.contains(".uz"))) {
                if (text.contains(".") && !text.contains(" ") && text.length > 3) {
                    return text
                }
            }

            // Add children to queue
            for (i in 0 until node.childCount) {
                val child = node.getChild(i)
                if (child != null) {
                    queue.add(child)
                }
            }
        }
        return null
    }
}
