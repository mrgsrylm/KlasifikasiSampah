package com.example.klasifikasisampah.ui.classification

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.klasifikasisampah.R
import com.example.klasifikasisampah.databinding.ActivityCameraBinding
import com.example.klasifikasisampah.ml.ScrapComposeClassifier
import com.example.klasifikasisampah.ml.ScrapTypeClassifier
import com.example.klasifikasisampah.ui.main.MainActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        const val CEK_URI = "extra"
    }

    private lateinit var binding: ActivityCameraBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var bitmap: Bitmap
    private var photo: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTakeGallery.setOnClickListener { startGallery() }
        setupCamera()
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_PICK /* or ACTION_GET_CONTENT */
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            photo = selectedImg.toString()
            val u = Uri.parse(photo)

            bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(this@CameraActivity.contentResolver, u)
            } else {
                val src = ImageDecoder.createSource(this@CameraActivity.contentResolver, u)
                ImageDecoder.decodeBitmap(src).copy(Bitmap.Config.RGBA_F16, true)
            }

            upload()
        }
    }

    private fun setupCamera() {
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        binding.btnTakePhoto.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            Toast.makeText(applicationContext, "Success take a picture", Toast.LENGTH_SHORT).show()
            takePhoto()
        }

        binding.btnCancel.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "$savedUri")
                    val intent = Intent()
                    intent.putExtra(CEK_URI, savedUri.toString())
                    setResult(Activity.RESULT_OK, intent)

                    photo = savedUri.toString()
                    val u = Uri.parse(photo)

                    bitmap = if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images.Media.getBitmap(this@CameraActivity.contentResolver, u)
                    } else {
                        val src = ImageDecoder.createSource(this@CameraActivity.contentResolver, u)
                        ImageDecoder.decodeBitmap(src).copy(Bitmap.Config.RGBA_F16, true)
                    }
                    upload()
                }
            })
    }

    private lateinit var typeClassifier: ScrapTypeClassifier
    private lateinit var categoryClassifier: ScrapComposeClassifier

    private fun upload() {
        typeClassifier = ScrapTypeClassifier(
            assets,
            UploadActivity.STCModel,
            UploadActivity.STCLabel,
            UploadActivity.mInputSize
        )
        val typeResult = typeClassifier.classifyImage(bitmap)
        Log.d("TYPE", typeResult.toString())

        categoryClassifier = ScrapComposeClassifier(
            assets,
            UploadActivity.SCDModel,
            UploadActivity.SCDLabel,
            UploadActivity.mInputSize
        )
        val categoryResult = categoryClassifier.classifyImage(bitmap)
        Log.d("COMPOSE", categoryResult.toString())

        val type = typeResult[0].type
        val confident = typeResult[0].confident
        val identify = ArrayList(categoryResult)

        // move to InfoActivity
        val moveIntent = Intent(this@CameraActivity, InfoActivity::class.java)
        moveIntent.putExtra(InfoActivity.EXTRA_TYPE, type)
        moveIntent.putExtra(InfoActivity.EXTRA_CONFIDENT, confident)
        moveIntent.putParcelableArrayListExtra(InfoActivity.EXTRA_IDENTIFY, identify)
        moveIntent.putExtra(InfoActivity.EXTRA_IMAGE, photo)
        startActivity(moveIntent)
        finish()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(150, 150))
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

}