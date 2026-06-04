package com.genyassistant

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.gson.annotations.SerializedName
import okhttp3.*
import java.io.IOException
import com.google.gson.Gson

data class GitHubRelease(
    @SerializedName("tag_name") val tagName: String,
    @SerializedName("html_url") val htmlUrl: String,
    @SerializedName("body") val body: String
)

class UpdateChecker(private val context: Context) {
    private val client = OkHttpClient()
    private val gson = Gson()
    private val repoUrl = "https://api.github.com/repos/carsaimz/geny-ia-agent/releases/latest"

    fun checkForUpdates(currentVersion: String, onUpdateAvailable: (GitHubRelease) -> Unit) {
        val request = Request.Builder()
            .url(repoUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { json ->
                    try {
                        val release = gson.fromJson(json, GitHubRelease::class.java)
                        val latestVersion = release.tagName.removePrefix("v")
                        if (isNewerVersion(currentVersion, latestVersion)) {
                            onUpdateAvailable(release)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    private fun isNewerVersion(current: String, latest: String): Boolean {
        val currentParts = current.split(".").map { it.toIntOrNull() ?: 0 }
        val latestParts = latest.split(".").map { it.toIntOrNull() ?: 0 }
        
        for (i in 0 until maxOf(currentParts.size, latestParts.size)) {
            val c = currentParts.getOrElse(i) { 0 }
            val l = latestParts.getOrElse(i) { 0 }
            if (l > c) return true
            if (c > l) return false
        }
        return false
    }

    fun promptUpdate(release: GitHubRelease) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(release.htmlUrl))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
