package com.rooms.android.readbook

import android.Manifest
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.vision.text.TextBlock
import android.util.SparseArray
import com.google.android.gms.vision.Detector
import android.view.SurfaceHolder
import android.Manifest.permission
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.SurfaceView
import android.view.View
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.text.TextRecognizer
import kotlinx.android.synthetic.main.activity_ocr.*
import java.io.IOException


class OcrActivity : AppCompatActivity() {

    val TAG = OcrActivity::class.simpleName

    val requestPermissionID : Int = 1000
    var mCameraSource : CameraSource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocr)

        startCameraSource()
    }

    private fun startCameraSource() {

        //Create the TextRecognizer
        val textRecognizer = TextRecognizer.Builder(applicationContext).build()

        if (!textRecognizer.isOperational) {
            Log.w(TAG, "Detector dependencies not loaded yet")
        } else {

            //Initialize camerasource to use high resolution and set Autofocus on.
            mCameraSource = CameraSource.Builder(applicationContext, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setAutoFocusEnabled(true)
                .setRequestedFps(2.0f)
                .build()

            /**
             * Add call back to SurfaceView and check if camera permission is granted.
             * If permission is granted we can start our cameraSource and pass it to surfaceView
             */
            surfaceView.getHolder().addCallback(object : SurfaceHolder.Callback {
                override fun surfaceCreated(holder: SurfaceHolder) {
                    try {

                        if (ActivityCompat.checkSelfPermission(
                                applicationContext,
                                Manifest.permission.CAMERA
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {

                            ActivityCompat.requestPermissions(
                                this@OcrActivity,
                                arrayOf(Manifest.permission.CAMERA),
                                requestPermissionID
                            )
                            return
                        }
                        mCameraSource?.start(surfaceView.getHolder())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }

                override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

                /**
                 * Release resources for cameraSource
                 */
                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    mCameraSource?.stop()
                }
            })

            //Set the TextRecognizer's Processor.
            textRecognizer.setProcessor(object : Detector.Processor<TextBlock> {
                override fun release() {}

                /**
                 * Detect all the text from camera using TextBlock and the values into a stringBuilder
                 * which will then be set to the textView.
                 */
                override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
                    val items = detections.detectedItems
                    if (items.size() != 0) {

                        text_view.post({
                            val stringBuilder = StringBuilder()
                            for (i in 0 until items.size()) {
                                val item = items.valueAt(i)
                                stringBuilder.append(item.value)
                                stringBuilder.append("\n")
                            }
                            text_view.setText(stringBuilder.toString())
                        })
                    }
                }
            })
        }
    }
}