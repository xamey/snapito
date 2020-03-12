package android.ut3.snapito

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Camera
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.params.StreamConfigurationMap
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.ut3.snapito.R
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class CameraActivity : AppCompatActivity() {
    private var cameraManager: CameraManager? = null
    private var cameraFacing: Int = 0
    private var surfaceTextureListener: TextureView.SurfaceTextureListener? = null
    private var previewSize: Size? = null
    private var cameraId: String? = null
    private var stateCallback: CameraDevice.StateCallback? = null
    private var backgroundHandler: Handler? = null
    private var backgroundThread: HandlerThread? = null
    private var cameraDevice: CameraDevice? = null

    private var captureRequestBuilder: CaptureRequest.Builder? = null
    private var captureRequest: CaptureRequest? = null
    private var cameraCaptureSession: CameraCaptureSession? = null

    private lateinit var takePicBtn: FloatingActionButton;
    private lateinit var flipBtn: ImageButton;
    private var textureView: TextureView? = null

    private lateinit var galleryFolder: File;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_camera)

        textureView = findViewById(R.id.texture_view)
        flipBtn = findViewById(R.id.flip_btn);
        takePicBtn = findViewById(R.id.fab_take_photo);

        flipBtn.setOnClickListener { view -> CameraActivity@ flip() }
        takePicBtn.setOnClickListener{ view -> CameraActivity@takePicture() }


        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            CAMERA_REQUEST_CODE
        )

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraFacing = CameraCharacteristics.LENS_FACING_BACK

        surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surfaceTexture: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                setUpCamera()
                openCamera()
            }

            override fun onSurfaceTextureSizeChanged(
                surfaceTexture: SurfaceTexture,
                width: Int,
                height: Int
            ) {

            }

            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {

            }

        }

        stateCallback = object : CameraDevice.StateCallback() {
            override fun onOpened(cameraDevice: CameraDevice) {
                this@CameraActivity.cameraDevice = cameraDevice
                createPreviewSession()
            }

            override fun onDisconnected(cameraDevice: CameraDevice) {
                cameraDevice.close()
                this@CameraActivity.cameraDevice = null
            }

            override fun onError(cameraDevice: CameraDevice, error: Int) {
                cameraDevice.close()
                this@CameraActivity.cameraDevice = null
            }
        }
    }

    private fun takePicture() {
        var outputPhoto: FileOutputStream? = null;
        try {
            createImageGallery();
            val imageFile = createImageFile(galleryFolder);
            outputPhoto = FileOutputStream(imageFile);
            textureView?.bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, outputPhoto);

            println(galleryFolder.absolutePath)
            startCanvasActivity(imageFile.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace();
        } finally {
            try {
                outputPhoto?.close();
            } catch (e: IOException) {
                e.printStackTrace();
            }
        }
    }

    private fun startCanvasActivity(absolutePath: String) {
        val intent = Intent(this, CanvasActivity::class.java);
        intent.putExtra("imagePath", absolutePath);
        startActivity(intent);
    }

    private fun setUpCamera() {
        try {
            for (cameraId in cameraManager!!.cameraIdList) {
                val cameraCharacteristics = cameraManager!!.getCameraCharacteristics(cameraId)
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == cameraFacing) {
                    val streamConfigurationMap = cameraCharacteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
                    )
                    previewSize =
                        streamConfigurationMap!!.getOutputSizes(SurfaceTexture::class.java)[0]
                    this.cameraId = cameraId
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    fun flip() {
        cameraFacing = if (cameraFacing == CameraCharacteristics.LENS_FACING_BACK)
            CameraCharacteristics.LENS_FACING_FRONT
        else CameraCharacteristics.LENS_FACING_BACK
        closeCamera();
        setUpCamera();
        openCamera();

    }

    private fun openCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                cameraManager!!.openCamera(cameraId!!, stateCallback!!, backgroundHandler)

            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    private fun openBackgroundThread() {
        backgroundThread = HandlerThread("camera_background_thread")
        backgroundThread!!.start()
        backgroundHandler = Handler(backgroundThread!!.looper)
    }

    override fun onResume() {
        super.onResume()
        openBackgroundThread()
        if (textureView!!.isAvailable) {
            setUpCamera()
            openCamera()
        } else {
            textureView!!.surfaceTextureListener = surfaceTextureListener
        }
    }

    override fun onStop() {
        super.onStop()
        closeCamera()
        closeBackgroundThread()
    }

    private fun closeCamera() {
        if (cameraCaptureSession != null) {
            cameraCaptureSession!!.close()
            cameraCaptureSession = null
        }

        if (cameraDevice != null) {
            cameraDevice!!.close()
            cameraDevice = null
        }
    }

    private fun closeBackgroundThread() {
        if (backgroundHandler != null) {
            backgroundThread!!.quitSafely()
            backgroundThread = null
            backgroundHandler = null
        }
    }

    private fun createPreviewSession() {
        try {
            val surfaceTexture = textureView!!.surfaceTexture
            surfaceTexture.setDefaultBufferSize(previewSize!!.width, previewSize!!.height)
            val previewSurface = Surface(surfaceTexture)
            captureRequestBuilder =
                cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder!!.addTarget(previewSurface)

            cameraDevice!!.createCaptureSession(
                listOf(previewSurface),
                object : CameraCaptureSession.StateCallback() {

                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                        if (cameraDevice == null) {
                            return
                        }

                        try {
                            captureRequest = captureRequestBuilder!!.build()
                            this@CameraActivity.cameraCaptureSession = cameraCaptureSession
                            this@CameraActivity.cameraCaptureSession!!.setRepeatingRequest(
                                captureRequest!!,
                                null,
                                backgroundHandler
                            )
                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }

                    }

                    override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {

                    }
                }, backgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    fun createImageGallery() {
        val storageDirectory: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        galleryFolder = File(storageDirectory, resources.getString(R.string.app_name));
        if (!galleryFolder.exists()) {
            val wasCreated: Boolean = galleryFolder.mkdirs();
            if (!wasCreated) {
                Log.e("CapturedImages", "Failed to create directory");
            }
        }
    }

    fun createImageFile(galleryFolder: File): File {
        val timeStamp = Date().toString();
        return File.createTempFile(timeStamp, ".jpg", galleryFolder);
    }


    companion object {
        private val CAMERA_REQUEST_CODE = 32
    }

}
