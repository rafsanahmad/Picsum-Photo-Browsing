package com.rafsan.picsumphotoapp.alertDialog

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.rafsan.picsumphotoapp.R

class ShowAlert {
    fun alertDialog(
        context: Context,
        title: String,
        message: String,
        positiveBtnText: String,
        negativeBtnText: String? = "",
        listener: DialogListener
    ) {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveBtnText) { dialog, _ ->
                dialog.dismiss()
                listener.onYesClicked("yes")
            }
            .setNegativeButton(negativeBtnText) { dialog, _ ->
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
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        builder.setTitle(title)
            .setMessage(message)
            .setNeutralButton(neutralBtnText) { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }
}