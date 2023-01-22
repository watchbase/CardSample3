package com.example.cardsample3.ui.camera

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.icu.text.SimpleDateFormat
import android.media.MediaActionSound
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider.*
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import com.example.cardsample3.MainActivity
import com.example.cardsample3.R
import com.example.cardsample3.databinding.FragmentCameraBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class  CameraFragment : Fragment() {

    private lateinit var viewBinding: FragmentCameraBinding
    private lateinit var packageManager:PackageManager

    private val binding get() = viewBinding
    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService

    override fun onStart() {
        super.onStart()

      //  (requireActivity() as MainActivity).setToolbarTitle(getString(R.string.title_camera))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        viewBinding = FragmentCameraBinding.inflate(inflater, container, false)
        val root: View = binding.root

        packageManager = requireActivity().packageManager

        // Request camera permissions
        if (checkCameraPermission()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listeners for take photo and video capture buttons
        viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }
        viewBinding.imageCaptureAddButton
            .let {
                it.setOnClickListener { startCamera() }
                it.hide()
            }

        cameraExecutor = Executors.newSingleThreadExecutor()

        return root
    }

    private fun takePhoto()
    {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        //シャッター音
        val sound = MediaActionSound()
        sound.load(MediaActionSound.SHUTTER_CLICK)

        val contentResolver = requireContext().contentResolver

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"

                    showCapturedPhoto(output.savedUri)
                    switchButton(false)
                    //  Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    // Log.d(TAG, msg)
                }
            }
        )
    }

    lateinit var  cameraProvider: ProcessCameraProvider

    private fun stopCamera() {
        cameraProvider.unbindAll()
        switchButton(false)
    }

    private fun switchButton(cameraOn: Boolean) {
        viewBinding.imageCaptureButton.let {
            if (cameraOn) { it.show() } else { it.hide() }
        }
        viewBinding.imageCaptureAddButton.let {
            if (cameraOn) { it.hide() } else { it.show() }
        }
        binding.viewFinder.let {
            it.isInvisible = !cameraOn
        }
        binding.imageView.let {
            it.isInvisible = cameraOn
        }
    }

    private fun startCamera() {

        switchButton(true)

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            cameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            //image
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }


    fun showCapturedPhoto(photoUri:Uri?) {

        var bitmap: Bitmap?
        try {
            photoUri?.let {
                val inputStream: InputStream? =
                    requireContext().getContentResolver().openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream!!)
                inputStream?.let {
                    it.close()
                }
                binding.imageView.setImageBitmap(bitmap)

            }
        } catch (e: FileNotFoundException) {

            e.printStackTrace()
        } catch (e: IOException) {
            //
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }


    private fun checkCameraPermission(): Boolean {
        //  val applicationContext = MyApplication();
        return PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults:
        IntArray,
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(requireContext(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                    //finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }


}