package com.andy.visioncodeimage

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import com.andy.visioncodeimage.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED){
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA) , REQUEST_CAMERA_PERMISSION)
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CAMERA_PERMISSION){
            if(permissions[0] == android.Manifest.permission.CAMERA
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //取得權限
                startCamera()
                return
            }
        }
    }
    var qrcodeValue:String? = null
    private fun startCamera() {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED){
            return
        }
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetAspectRatio(AspectRatio.RATIO_16_9)
        }.build()
        val preview = Preview(previewConfig)
        preview.setOnPreviewOutputUpdateListener{
           viewBinding.preview.setSurfaceTexture(it.surfaceTexture)
        }
        val imageAnalysisConfig = ImageAnalysisConfig.Builder()
            .build()
        val imageAnalysis = ImageAnalysis(imageAnalysisConfig)

        val qrCodeAnalyzer = ZxingQrCodeAnalyzer{ result ->
            if(qrcodeValue != result.text){
                qrcodeValue = result.text
                Log.d(TAG, "${result.barcodeFormat}")
                Log.d(TAG, "${result.text}")
            }
        }
        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), qrCodeAnalyzer)
        CameraX.bindToLifecycle(this, preview, imageAnalysis)
    }

    companion object{
        const val TAG = "MainActivity"
        const val REQUEST_CAMERA_PERMISSION = 123
    }
}