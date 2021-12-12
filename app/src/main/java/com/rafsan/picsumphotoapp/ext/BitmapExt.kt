package com.rafsan.picsumphotoapp.ext

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.rafsan.picsumphotoapp.PicsumPhotoApp
import com.rafsan.picsumphotoapp.utils.Constants
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.URL

// extension function to get / download bitmap from url
fun URL.toBitmap(): Bitmap? {
    return try {
        BitmapFactory.decodeStream(openStream())
    } catch (e: IOException) {
        Log.d(Constants.TAG, e.toString())
        return null
    }
}

// extension function to save an image to internal storage
fun Bitmap.saveToInternalStorage(name: String): Uri? {
    val context = PicsumPhotoApp.applicationContext()
    // initializing a new file
    // bellow line return a directory in internal storage
    val fileName = "${name}.jpg"
    val file = File(
        context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            .toString() + File.separator + fileName
    )
    if (file.exists()) {
        return Uri.parse(file.absolutePath)
    } else {
        return try {
            // get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // compress bitmap
            compress(Bitmap.CompressFormat.JPEG, Constants.IMAGE_QUALITY, stream)

            // flush the stream
            stream.flush()

            // close stream
            stream.close()

            // return the saved image uri
            Uri.parse(file.absolutePath)
        } catch (e: IOException) { // catch the exception
            e.printStackTrace()
            Log.d(Constants.TAG, e.toString())
            null
        }
    }
}