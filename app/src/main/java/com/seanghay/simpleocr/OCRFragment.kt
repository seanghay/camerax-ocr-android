package com.seanghay.simpleocr

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.seanghay.simpleocr.databinding.FragmentOcrBinding
import java.util.concurrent.Executors

class OCRFragment : Fragment() {

  private var _binding: FragmentOcrBinding? = null
  private val binding: FragmentOcrBinding get() = _binding!!

  private val cameraPermissionLauncher =
    registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
      if (granted) {
        startCamera()
      } else {
        Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
      }
    }

  private fun startCamera() {
    val mainLooper = ContextCompat.getMainExecutor(requireContext())

    val executor = Executors.newSingleThreadExecutor()
    val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

    cameraProviderFuture.addListener({
      val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

      val preview = Preview.Builder()
        .build()

      preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)

      val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

      val analyzer = OCRAnalyzer { text ->
        binding.textView.post {
          binding.textView.text = text.text
        }
      }

      val analysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
        .setTargetResolution(Size(1280, 720))
        .build()

      analysis.setAnalyzer(executor, analyzer)

      try {

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
          this, cameraSelector, analysis, preview
        )
      } catch (exc: Exception) {

      }
    }, ContextCompat.getMainExecutor(requireContext()))


  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _binding = FragmentOcrBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    cameraPermissionLauncher.launch(
      android.Manifest.permission.CAMERA
    )

  }

  override fun onDestroyView() {
    _binding = null
    super.onDestroyView()
  }

  companion object {
    fun newInstance(): OCRFragment {
      return OCRFragment()
    }
  }

}