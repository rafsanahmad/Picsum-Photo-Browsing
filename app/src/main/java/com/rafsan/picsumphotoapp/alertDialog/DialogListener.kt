package com.rafsan.picsumphotoapp.alertDialog

interface DialogListener {
    fun onYesClicked(obj: Any?)
    fun onNoClicked(error: String?)
}