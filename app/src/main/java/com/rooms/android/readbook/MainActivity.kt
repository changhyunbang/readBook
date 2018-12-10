package com.rooms.android.readbook

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.Manifest.permission
import android.Manifest.permission.WRITE_CALENDAR
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.rooms.android.readbook.ocr.OcrCaptureActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.simpleName
    val MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BTN_TTS.setOnClickListener{
            val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

//            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
//                Log.d(TAG, "WRITE_EXTERNAL_STORAGE PERMISSION_DENIED")
//                ActivityCompat.requestPermissions(this,
//                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                    MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE)
//            } else {
//                saveTTS()
//            }
            startActivity(Intent(this@MainActivity, TtsActivity::class.java))
        }

        BTN_OCR.setOnClickListener {
            startActivity(Intent(this@MainActivity, OcrActivity::class.java))
        }

        BTN_OCR_CAMERA.setOnClickListener {
            startActivity(Intent(this@MainActivity, OcrCaptureActivity::class.java))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        Log.d(TAG, "onRequestPermissionsResult requestCode : " + requestCode +
            " permissions : " + permissions.toString() + " grantResults : " + grantResults.toString())

        when(requestCode) {
            MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE ->
            {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    saveTTS()
            }
        }
    }

    fun saveTTS() {
        ApiTest.TTS("사랑을 했다  우리가 만나  지우지 못할  추억이 됐다")
    }
}
