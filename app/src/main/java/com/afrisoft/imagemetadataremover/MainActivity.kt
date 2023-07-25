package com.afrisoft.imagemetadataremover

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

            // Get the metadata information from the ExifInterface
            val imageMetadata = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION)

            // Show the metadata in a dialog
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle("Image Metadata")
            dialogBuilder.setMessage(imageMetadata ?: "No metadata available.")
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
