package com.seanghay.simpleocr

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class OCRAnalyzer(
  private val textCallback: (Text) -> Unit
): ImageAnalysis.Analyzer {


  private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

  @SuppressLint("UnsafeOptInUsageError")
  override fun analyze(image: ImageProxy) {
    val mediaImage = image.image ?: return
    val inputImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
    recognizer.process(inputImage)
      .addOnCompleteListener { image.close() }
      .addOnSuccessListener { text ->
        textCallback(text)
        Log.d("OCRAnalyzer", "text=${text.text}")
      }
  }

}