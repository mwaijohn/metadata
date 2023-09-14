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

            val newString = StringBuilder();

            val dateTime: String? = exifInterface.getAttribute(ExifInterface.TAG_DATETIME)
            val make: String? = exifInterface.getAttribute(ExifInterface.TAG_MAKE)
            val model: String? = exifInterface.getAttribute(ExifInterface.TAG_MODEL)
            val aperture: String? = exifInterface.getAttribute(ExifInterface.TAG_APERTURE)
            val exposureTime: String? = exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME)
            val focalLength: String? = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH)
            val iso: String? = exifInterface.getAttribute(ExifInterface.TAG_ISO)
            val orientation: String? = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION)
            val brightness: String? = exifInterface.getAttribute(ExifInterface.TAG_BRIGHTNESS_VALUE)
            val date: String? = exifInterface.getAttribute(ExifInterface.TAG_DATETIME)
            val exposureMode: String? = exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_MODE)
            val flash: String? = exifInterface.getAttribute(ExifInterface.TAG_FLASH)
            val lightSource: String? = exifInterface.getAttribute(ExifInterface.TAG_LIGHT_SOURCE)
            val lensMaxAperture: String? = exifInterface.getAttribute(ExifInterface.TAG_MAX_APERTURE_VALUE)
            val meteringMode: String? = exifInterface.getAttribute(ExifInterface.TAG_METERING_MODE)
            val photographicSensitivity: String? = exifInterface.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS)
            val sceneCaptureType: String? = exifInterface.getAttribute(ExifInterface.TAG_SCENE_CAPTURE_TYPE)
            val sensorType: String? = exifInterface.getAttribute(ExifInterface.TAG_SENSING_METHOD)
            val sceneType: String? = exifInterface.getAttribute(ExifInterface.TAG_SCENE_TYPE)
            val shutterSpeed: String? = exifInterface.getAttribute(ExifInterface.TAG_SHUTTER_SPEED_VALUE)
            val whiteBalance: String? = exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE)
            val xResolution: String? = exifInterface.getAttribute(ExifInterface.TAG_X_RESOLUTION)
            val yResolution: String? = exifInterface.getAttribute(ExifInterface.TAG_Y_RESOLUTION)
            val latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
            val longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)


            val combinedAttributes = "Date: $dateTime\n"+
                    "Aperture: $aperture\n " +
                    "Brightness: $brightness\n " +
                    "Date: $date\n " +
                    "Make: $make\n"+
                    "Model: $model\n"+
                    "Exposure Mode: $exposureMode\n " +
                    "Exposure Time: $exposureTime\n " +
                    "Flash: $flash\n " +
                    "Focal Length: $focalLength\n " +
                    "Light Source: $lightSource\n " +
                    "Lens Max Aperture: $lensMaxAperture\n " +
                    "Metering Mode: $meteringMode\n " +
                    "Orientation: $orientation\n " +
                    "Photographic Sensitivity: $photographicSensitivity\n " +
                    "Scene Capture Type: $sceneCaptureType\n " +
                    "Sensor Type: $sensorType\n " +
                    "Scene Type: $sceneType\n " +
                    "Shutter Speed: $shutterSpeed\n " +
                    "White Balance: $whiteBalance\n " +
                    "X Resolution: $xResolution\n " +
                    "Y Resolution: $yResolution \n" +
                    "Latitude: $latitude \n" +
                    "Longitude: $longitude \n" +
                    "Iso: $iso\n"

            newString.append(combinedAttributes)

            val projection = arrayOf(
                MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.SIZE,
                MediaStore.Images.ImageColumns.MIME_TYPE,
            )
            val cursor = contentResolver.query(imageUri!!, projection, null, null, null)

            if (cursor != null && cursor.moveToFirst()) {
                val displayName: String = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME))
                val size: Long = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE))
                val mimeType: String = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.MIME_TYPE))

                newString.append("Name: $displayName\n")
                newString.append("Size: $size\n")
                newString.append("Mime Type: $mimeType\n")
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
