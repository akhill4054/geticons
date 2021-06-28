package com.greedygames.geticons.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.greedygames.geticons.R
import com.greedygames.geticons.data.models.Icon

class IconDownloadHelper(val context: Context) {

    private val downloadManager =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    fun downloadIcon(downloadFormat: Icon.DownloadFormat) {
        try {
            val downloadUrl = downloadFormat.format.downloadUrl
            val downloadRequest = DownloadManager.Request(Uri.parse(downloadUrl)).apply {
                addRequestHeader(
                    "Authorization",
                    "Bearer ${context.getString(R.string.api_key)}"
                )
                setTitle("Icon Download")
                setDescription(
                    "Downloading icon ${downloadFormat.format.format} " +
                            "${downloadFormat.width}x${downloadFormat.height}"
                )
                setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                )
                setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "icon_${downloadFormat.iconId}_${downloadFormat.width}" +
                            "x${downloadFormat.height}.${downloadFormat.format.format}"
                )
            }
            downloadManager.enqueue(downloadRequest)
        } catch (e: Exception) {
            e.printStackTrace()
            // Show error
            context.showShortToast(context.getString(R.string.err_download))
        }
    }
}