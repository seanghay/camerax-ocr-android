package com.seanghay.simpleocr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    if (savedInstanceState == null) {
      val fragment = OCRFragment.newInstance()
      supportFragmentManager.beginTransaction()
        .setPrimaryNavigationFragment(fragment)
        .replace(R.id.fragmentContainerView, fragment)
        .commit()
    }
  }

}