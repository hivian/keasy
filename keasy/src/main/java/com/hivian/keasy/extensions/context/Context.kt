@file:Suppress("UNUSED", "NOTHING_TO_INLINE")

package com.hivian.keasy.extensions.context

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.FileProvider
import com.hivian.keasy.R
import com.hivian.keasy.extensions.conversions.toFileOrNull
import com.hivian.keasy.extensions.standard.letOrElse


const val TEXT_MIME = "text/plain"
const val IMAGE_MIME = "image/*"

/**
 * Start activity passing optional [extras], [action] and [data] as arguments
 */
inline fun <reified T: Activity> Context.startCustomActivity(extras : Bundle?= null, action : String ?= null, data: Uri?= null) {
    startActivity(Intent(this, T::class.java).apply {
        extras?.let { putExtras(extras) }
        action?.let { setAction(action) }
        data?.let { setData(data) }
    })
}

/**
 * Hide activity's current focus and keyboard
 */
inline fun Activity.hideKeyboard() {
    // check if no view has focus:
    val view = currentFocus
    view?.clearFocus()
    val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(view?.windowToken, 0)
}



/**
 * Start Application Details settings activity
 */
inline fun Context.startAppSettings() {
    startActivity(Intent().apply {
        action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.parse("package:$packageName")
    })
}

inline fun <reified T: Activity> Activity.myStartActivityForResult(requestCode: Int) {
    val intent = Intent(this, T::class.java)
    startActivityForResult(intent, requestCode)
}

/**
 * Get screen dimensions
 * Return [Point]
 */
inline fun Activity.getScreenDimension() : Point {
    val display = windowManager.defaultDisplay
    val size = Point()
    display.getSize(size)
    return size
}

/**
 * Change status bar color of `this` activity
 */
inline fun Activity.setWhiteStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.WHITE
    }
}

/**
 * Start gallery intent activity with [requestCode] to identify the result
 */
inline fun Activity.showGalleryChooser(requestCode: Int) {
    val intent = Intent().apply {
        type = "image/*"
        action = Intent.ACTION_PICK
    }
    startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode)
}

/**
 * Start share intent activity with custom [title], [message] and optional [bitmap] for sharing image
 */
inline fun Context.createChooser(title : String, message : String, bitmap : Bitmap ?= null) {
    val context = this
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, message)
        type = bitmap.letOrElse({ TEXT_MIME }) { b ->
            val file = b.toFileOrNull(context)
            file.letOrElse({ TEXT_MIME }) {
                val uri = FileProvider.getUriForFile(context, getString(R.string.file_provider_authority), it)
                putExtra(Intent.EXTRA_STREAM, uri)
                IMAGE_MIME
            }
        }
    }
    startActivity(Intent.createChooser(sendIntent, title))
}