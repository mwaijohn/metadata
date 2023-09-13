package com.afrisoft.imagemetadataremover

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.Nullable
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class MainActivity : ComponentActivity() {
    private val REQUEST_IMAGE_PICK = 1

    private var imageView: ImageView? = null
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById<ImageView>(R.id.imageView)

        val btnLoadImage = findViewById<Button>(R.id.btnLoadImage)
        val btnViewMetadata = findViewById<Button>(R.id.btnViewMetadata)
        val btnRemoveMetadata = findViewById<Button>(R.id.btnRemoveMetadata)
        val btnPreview = findViewById<Button>(R.id.btnPreview)

        btnLoadImage.setOnClickListener { openImagePicker() }

        btnViewMetadata.setOnClickListener { viewImageMetadata() }

        btnRemoveMetadata.setOnClickListener { removeImageMetadata() }

        btnPreview.setOnClickListener { previewImage() }

    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                imageView!!.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getSimplifiedNameUsingAI(key: String): String {
        // In a real AI system, this function would use AI algorithms to determine the simplified name.
        // Since this is a simulation, let's generate a simplified name based on the key.
        return key.toLowerCase().replace("_", " ")
    }

    @SuppressLint("Range")
    private fun viewImageMetadata() {
        try {
            if (imageUri == null) {
                Toast.makeText(this, "Please load an image first.", Toast.LENGTH_SHORT).show()
                return
            }
            val inputStream = contentResolver.openInputStream(imageUri!!)
            if (inputStream == null) {
                Toast.makeText(this, "Failed to open the image.", Toast.LENGTH_SHORT).show()
                return
            }
            val exifInterface = ExifInterface(inputStream)
            inputStream.close()

//            // Get the metadata information 1`  qfrom the ExifInterface
//            val imageMetadata = exifInterface.getAttribute(ExifInterface.TAG_APERTURE_VALUE)
//
//            val hashMap: HashMap<String, String> = HashMap()
//            for (key in DataClass.attributes) {
//                // Use AI to determine the simplified name for each key
//                val simplifiedName = getSimplifiedNameUsingAI(key)
//                hashMap[key] = simplifiedName
//            }
            val newString = StringBuilder();
//            for ((key, value) in hashMap) {
//                println("$key: $value")
//                newString.append("$value: ${exifInterface.getAttribute(ExifInterface.TAG_APERTURE_VALUE)} \n")
//            }

            val dateTime: String? = exifInterface.getAttribute(ExifInterface.TAG_DATETIME)
            val make: String? = exifInterface.getAttribute(ExifInterface.TAG_MAKE)
            val model: String? = exifInterface.getAttribute(ExifInterface.TAG_MODEL)
            val aperture: String? = exifInterface.getAttribute(ExifInterface.TAG_APERTURE)
            val exposureTime: String? = exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME)
            val focalLength: String? = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH)
            val iso: String? = exifInterface.getAttribute(ExifInterface.TAG_ISO)
            val orientation: String? = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION)

            newString.append("Date: $dateTime\n")
            newString.append("Make: $make\n")
            newString.append("Model: $model\n")
            newString.append("Aperture: $aperture\n")
            newString.append("Exposure: $exposureTime\n")


            val projection = arrayOf(
                MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.SIZE,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE,
                MediaStore.Images.ImageColumns.WIDTH,
                MediaStore.Images.ImageColumns.HEIGHT,
                MediaStore.Images.ImageColumns.LATITUDE,
                MediaStore.Images.ImageColumns.LONGITUDE
            )
            val cursor = contentResolver.query(imageUri!!, projection, null, null, null)

            if (cursor != null && cursor.moveToFirst()) {
                val displayName: String = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME))
                val size: Long = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE))
                val mimeType: String = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.MIME_TYPE))
                val width: Int = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.WIDTH))
                val height: Int = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.HEIGHT))
                val latitude: Double = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LATITUDE))
                val longitude: Double = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LONGITUDE))

                newString.append("Latitude: $latitude\n")
                newString.append("Longitude: $longitude\n")
                newString.append("Name: $displayName\n")
                newString.append("Size: $size\n")
                newString.append("Mime Type: $mimeType\n")
                newString.append("Width: $width\n")
                newString.append("Height: $height\n")
            }

            cursor?.close()

            // Show the metadata in a dialog
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle("Image Metadata")
            dialogBuilder.setMessage(newString)
            dialogBuilder.setPositiveButton("OK", null)
            val alertDialog = dialogBuilder.create()
            alertDialog.show()

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to read metadata.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeImageMetadata() {
        try {
            if (imageUri == null) {
                Toast.makeText(this, "Please load an image first.", Toast.LENGTH_SHORT).show()
                return
            }
            val inputStream = contentResolver.openInputStream(imageUri!!)
            if (inputStream == null) {
                Toast.makeText(this, "Failed to open the image.", Toast.LENGTH_SHORT).show()
                return
            }
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            // Create a new Bitmap without metadata
            val bitmapWithoutMetadata = originalBitmap.copy(originalBitmap.config, true)

            // Display the bitmap without metadata
            imageView!!.setImageBitmap(bitmapWithoutMetadata)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to remove metadata.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun previewImage() {
        // TODO: Implement previewing the image with removed metadata
    }
}
