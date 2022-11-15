package com.skillbox.aslanbolurov.photogallery.presentation


import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.skillbox.aslanbolurov.photogallery.R
import com.skillbox.aslanbolurov.photogallery.data.App
import com.skillbox.aslanbolurov.photogallery.data.NewPhoto
import com.skillbox.aslanbolurov.photogallery.databinding.FragmentTakingPhotoBinding
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor

private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss"

class TakingPhotoFragment : Fragment() {

    private val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
        .format(System.currentTimeMillis())

    private var _binding: FragmentTakingPhotoBinding? = null
    val binding get() = _binding!!

    private lateinit var executor: Executor

    private var imageCapture: ImageCapture? = null

    private val viewModel:TakingPhotoViewModel by viewModels {
        object:ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(TakingPhotoViewModel::class.java)){
                    var photoDao=(requireActivity().application as App).db.photoDao()
                    return TakingPhotoViewModel(photoDao) as T
                }else  {
                    throw IllegalArgumentException("Unknown class name")
                }

            }
        }
    }


    private val launcher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            Toast.makeText(requireContext(), "Permission is not Granted", Toast.LENGTH_LONG).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        executor = ContextCompat.getMainExecutor(requireContext())
        checkPermissions()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTakingPhotoBinding.inflate(inflater, container, false)

        binding.buttonTakePhoto.setOnClickListener {
            lifecycleScope.launchWhenCreated {
                takePhoto()
                viewModel.insertNewPhoto(NewPhoto(path=name, date = Date().toString()))
                delay(5_000)
                findNavController().navigate(
                    R.id.action_BackTo_PhotosList
                )
            }
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun startCamera() {
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            imageCapture = ImageCapture.Builder().build()

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_FRONT_CAMERA,
                preview,
                imageCapture
            )
        }, executor)
    }

    fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            requireActivity().contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()


        imageCapture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(
                        requireContext(),
                        "Photo saved on: ${outputFileResults.savedUri}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        requireContext(),
                        "Photo failed: ${exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    exception.printStackTrace()
                }

            }
        )





    }

    private fun checkPermissions() {
        if (
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
            Toast.makeText(requireContext(), "Permission is Granted", Toast.LENGTH_LONG).show()
        } else {
            launcher.launch(Manifest.permission.CAMERA)
        }

    }


    companion object {


//        private val REQUEST_PERMISSIONS:Array<String> = buildList {
//            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
//                add(Manifest.permission.)
//            }
//        }.toTypedArray()


        fun newInstance() = TakingPhotoFragment()
    }
}