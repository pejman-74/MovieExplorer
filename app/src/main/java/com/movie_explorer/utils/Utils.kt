package com.movie_explorer.utils

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.movie_explorer.R
import java.text.SimpleDateFormat
import java.util.*
object Utils {
    fun List<String>.toGenresStyleText(): String {
        var genres = ""
        forEachIndexed { index, s ->
            genres += s
            if (index < size - 1)
                genres += ", "
        }
        return genres
    }

    fun Context.showLongToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    

    fun Context.doubleButtonAlertDialog(
        titleMessage: String,
        message: String,
        positiveButtonText: String,
        negativeButtonText: String,
        positiveButtonAction: (() -> Unit)? = null,
        negativeButtonAction: (() -> Unit)? = null
    ): AlertDialog {
        return MaterialAlertDialogBuilder(this).setTitle(titleMessage)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                positiveButtonAction?.let {
                    it()
                }
                dialog.dismiss()
            }.setNegativeButton(negativeButtonText) { dialog, _ ->
                negativeButtonAction?.let {
                    it()
                }
                dialog.dismiss()
            }
            .setCancelable(true).setBackground(
                ColorDrawable(
                    ContextCompat.getColor(
                        this,
                        R.color.movieItemBackgroundColor
                    )
                )
            )
            .show().apply {
                getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                    ContextCompat.getColor(
                        this@doubleButtonAlertDialog,
                        R.color.movieItemTitlesTextColor
                    )
                )
                getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                    ContextCompat.getColor(
                        this@doubleButtonAlertDialog,
                        R.color.movieItemTitlesTextColor
                    )
                )
            }
    }

    fun getCurrentUTCDateTime() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)
        .apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date()) + "Z"
}