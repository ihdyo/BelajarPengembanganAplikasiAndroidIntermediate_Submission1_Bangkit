package com.ihdyo.postit.utils

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale

private const val FILENAME_FORMAT = "dd-MMM-yyyy"
val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())

fun createTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

fun urlToBitmap(src: String): Bitmap? {
    return try {
        URL(src).openStream().use {
            BitmapFactory.decodeStream(it)
        }
    } catch (ex: Exception) {
        Timber.e(ex.message.toString())
        null
    }
}

fun createFile(application: Application): File {
    val outputDirectory = application.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: application.filesDir
    return File(outputDirectory, "${System.currentTimeMillis()}.jpg")
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createTempFile(context)
    contentResolver.openInputStream(selectedImg)?.use { inputStream ->
        FileOutputStream(myFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
    return myFile
}

fun reduceFileImage(file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > 1000000)
    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}
