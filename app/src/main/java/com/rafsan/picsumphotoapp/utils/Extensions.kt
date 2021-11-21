package com.rafsan.picsumphotoapp.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.rafsan.picsumphotoapp.PicsumPhotoApp
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.URL
import java.util.*

// extension function to get / download bitmap from url
fun URL.toBitmap(): Bitmap? {
    return try {
        BitmapFactory.decodeStream(openStream())
    } catch (e: IOException) {
        Log.d("PicsumApp", e.toString())
        return null
    }
}

// extension function to save an image to internal storage
fun Bitmap.saveToInternalStorage(): Uri? {
    val context = PicsumPhotoApp.applicationContext()
    // initializing a new file
    // bellow line return a directory in internal storage
    val fileName = "${UUID.randomUUID()}.jpg"
    val file = File(
        context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            .toString() + File.separator + fileName
    )

    return try {
        // get the file output stream
        val stream: OutputStream = FileOutputStream(file)

        // compress bitmap
        compress(Bitmap.CompressFormat.JPEG, 100, stream)

        // flush the stream
        stream.flush()

        // close stream
        stream.close()

        // return the saved image uri
        Uri.parse(file.absolutePath)
    } catch (e: IOException) { // catch the exception
        e.printStackTrace()
        Log.d("PicsumApp", e.toString())
        null
    }
}