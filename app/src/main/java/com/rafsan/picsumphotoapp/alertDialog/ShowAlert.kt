package com.rafsan.picsumphotoapp.alertDialog

import android.content.Context
import androidx.appcompat.app.AlertDialog

class ShowAlert {
    fun alertDialog(
        context: Context,
        title: String,
        message: String,
        positiveBtnText: String,
        negativeBtnText: String? = "",
        listener: DialogListener
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveBtnText) { dialog, id ->
                dialog.dismiss()
                listener.onYesClicked("yes")
            }
            .setNegativeButton(negativeBtnText) { dialog, id ->
                dialog.dismiss()
                listener.onNoClicked(error = null)
            }

        builder.create().show()
    }

    fun okAlertDialog(
        context: Context,
        title: String,
        message: String,
        neutralBtnText: String
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(message)
            .setNeutralButton(neutralBtnText) { dialog, id ->
                dialog.dismiss()
            }
        builder.create().show()
    }
}