package android.ut3.snapito.view


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import java.io.File
import java.io.InputStream
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.os.Environment
import android.os.Looper
import android.provider.Settings
import android.ut3.snapito.R
import android.ut3.snapito.dataclasses.Sticker
import android.ut3.snapito.model.photos.TakenPhoto
import android.ut3.snapito.viewmodel.FirebaseStorageViewModel
import android.ut3.snapito.viewmodel.FirestoreViewModel
import android.util.Log
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import org.koin.android.ext.android.inject
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class CanvasActivity : Activity(), SensorEventListener {

    private val firestoreViewModel: FirestoreViewModel by inject()
    private val firebaseStorageViewModel: FirebaseStorageViewModel by inject()

    private var last_x: Float = 0f
    private var last_y: Float = 0f
    private var last_z: Float = 0f
    private var lastUpdate: Long = 0
    private lateinit var btnChooseImage: ImageView;
    private lateinit var btnChooseColor: ImageView;
    private lateinit var canvas: Canvas;
    private var paint: Paint = Paint();
    private lateinit var bitmap: Bitmap;
    private lateinit var imageView: ImageView;
    private var firstTouch: Boolean = true;
    private var imagePath: String? = null;
    private var originalImage: Bitmap? = null;
    private var originalFilterImage: Bitmap? = null;
    private var originalFilterImageBlackAndWhite: Bitmap? = null;
    private var originalFilterImageInverted: Bitmap? = null;
    private var busy: Boolean = false;

    private var oldX: Float = -1f;
    private var oldY: Float = -1f;
    private var stickers: MutableList<Sticker> = ArrayList();

    private lateinit var btnEmojiHappy: ImageButton;
    private lateinit var btnEmojiNerd: ImageButton;
    private lateinit var btnEmojiLove: ImageButton;
    private lateinit var btnEmojiDead: ImageButton;
    private lateinit var btnSend: ImageView;

    private lateinit var galleryFolder: File;

    lateinit var mFusedLocationClient: FusedLocationProviderClient
    var currentLatLng : LatLng = LatLng(43.560811, 1.469408)

    enum class FilterType {
        NORMAL,
        BLACK_AND_WHITE,
        INVERTED
    }

    private var currentFilter: FilterType =
        FilterType.NORMAL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canvas)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()


        imageView = findViewById(R.id.imageView)
        paint.setColor(Color.BLACK)
        paint.strokeWidth = 10f
        btnChooseImage = findViewById(R.id.btnLoad)
        btnChooseColor = findViewById(R.id.btnColor)
        btnEmojiHappy = findViewById(R.id.btnHappy);
        btnSend = findViewById(R.id.btnSend);
        btnEmojiHappy.setOnClickListener { v -> onBtnEmojiClick() }
        btnEmojiLove = findViewById(R.id.btnLove);
        btnEmojiLove.setOnClickListener { v -> onBtnEmojiLoveClick() }
        btnEmojiNerd = findViewById(R.id.btnNerd);
        btnEmojiNerd.setOnClickListener { v -> onBtnEmojiNerdClick() }
        btnEmojiDead = findViewById(R.id.btnDead);
        btnEmojiDead.setOnClickListener { v -> onBtnEmojiDeadClick() }

        btnSend.setOnClickListener{ v -> sendPicture() }

        imageView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent?): Boolean {
                if (firstTouch) {
                    initCanvas(v);
                }
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> if (noneEmojiSelected(event)) startLine(v, event);
                    MotionEvent.ACTION_MOVE ->{
                        if (noneEmojiSelected(event)) { // TODO
                            drawLine(v, event)
                        } else {
                            drawStickers(event);
                        }
                    }
                    MotionEvent.ACTION_UP -> if (noneEmojiSelected(event)) endLine(v, event)
                }
                return true // v?.onTouchEvent(event) ?: true
            }
        })
        btnChooseImage.setOnClickListener(View.OnClickListener { chooseImage() })
        btnChooseColor.setOnClickListener(View.OnClickListener { chooseColor() })
        imagePath = intent.getStringExtra("imagePath");
        loadPicture(Uri.fromFile(File(imagePath)))

        btnChooseImage.setOnClickListener(View.OnClickListener { chooseImage() })
        btnChooseColor.setOnClickListener(View.OnClickListener { chooseColor() })
        imagePath = intent.getStringExtra("imagePath");
        loadPicture(Uri.fromFile(File(imagePath)))
        var mSensorMgr = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager;
        mSensorMgr.registerListener(
            this,
            mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_FASTEST
        );
    }

    fun drawStickers(event: MotionEvent?) {
        if (originalFilterImage != null) {
            bitmap = originalFilterImage!!.copy(Bitmap.Config.ARGB_8888, true);
            imageView.setImageBitmap(bitmap!!);
            canvas = Canvas(bitmap!!);
            firstTouch = false;

        }
        stickers.forEach { sticker ->
            if (event != null) {
                if (sticker.left < event.getX() && event.getX() < sticker.left + 256f
                    && sticker.top < event.getY() && event.getY() < sticker.top + 256f
                ) {
                    sticker.left = event.getX() - 128;
                    sticker.top = event.getY() - 128;
                    sticker.bottom = sticker.top + 256f;
                    sticker.right = sticker.left + 256f;
                }
            }
            drawSticker(sticker)
        }
    }

    fun onBtnEmojiNerdClick() {
        var addedSticker = Sticker(R.drawable.emoji_nerd, 0f, 0f, 256f, 256f)
        stickers.add(addedSticker);
        drawStickers(null);
    }

    fun onBtnEmojiLoveClick() {
        var addedSticker = Sticker(R.drawable.emoji_love, 0f, 0f, 256f, 256f)
        stickers.add(addedSticker);
        drawStickers(null);
    }

    fun onBtnEmojiDeadClick() {
        var addedSticker = Sticker(R.drawable.emoji_dead, 0f, 0f, 256f, 256f)
        stickers.add(addedSticker);
        drawStickers(null);
    }

    fun onBtnEmojiClick() {
        var addedSticker = Sticker(R.drawable.emoji_happy, 0f, 0f, 256f, 256f)
        stickers.add(addedSticker);
        drawStickers(null);
    }

    fun noneEmojiSelected(event: MotionEvent): Boolean {
        for (sticker in stickers) {
            if (sticker.left < event.getX() && event.getX() < sticker.left + sticker.right
                && sticker.top < event.getY() && event.getY() < sticker.top + sticker.bottom) {
                return false;
            }
        }

        return true;
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (isLocationEnabled()) {
            mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                var location: Location? = task.result
                if (location == null) {
                    requestNewLocationData()
                } else {
                    currentLatLng = LatLng(location.latitude, location.longitude)
                }
            }
        } else {
            Toast.makeText(this, "Veuillez activer la localisation", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }

    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            currentLatLng = LatLng(mLastLocation.latitude, mLastLocation.longitude)

        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    private fun chooseColor() {
        ColorPickerDialogBuilder
            .with(this)
            .setTitle("Choose color")
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .density(12)
            .setPositiveButton("ok") { dialog, selectedColor, allColors -> }
            .setOnColorSelectedListener { selectedColor ->
                System.out.println("ok" + selectedColor); paint.setColor(
                selectedColor
            )
            }
            .build()
            .show()
    }

    fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val exifData = data?.getData()!!
            loadPicture(exifData)
        }
    }

    private fun loadPicture(exifData: Uri) {
        val ins: InputStream? = getContentResolver()?.openInputStream(exifData);
        originalImage = BitmapFactory.decodeStream(ins);
        originalFilterImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true);
        bitmap = originalImage!!.copy(Bitmap.Config.ARGB_8888, true);
        if (bitmap != null) {
            redrawImageView()
        } else {
            System.out.println("IMG IS NULL")
        }

    }

    private fun redrawImageView() {
        imageView.setImageBitmap(bitmap);
        canvas = Canvas(bitmap);
        firstTouch = false;
    }


    private fun startLine(view: View, event: MotionEvent) {
        oldX = event.x;
        oldY = event.y;
    }

    private fun drawLine(view: View, event: MotionEvent) {
        canvas.drawLine(oldX, oldY, event.x, event.y, paint);
        oldX = event.x;
        oldY = event.y;
        view.invalidate()
    }

    private fun endLine(view: View, event: MotionEvent) {
        canvas.drawLine(oldX, oldY, event.x, event.y, paint);
        oldX = -1f
        oldY = -1f
        view.invalidate()
    }

    private fun initCanvas(view: View) {
        firstTouch = false;
        bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888);
        imageView.setImageBitmap(bitmap);
        canvas = Canvas(bitmap);
    }

    private fun drawSticker(st: Sticker) {
        // redraw scene

        // draw current sticker
        var paint0 = Paint();
        paint.isAntiAlias = false;
        paint.isFilterBitmap = true;
        paint.isDither = true;
        for (sticker in stickers) {
            var sticker = BitmapFactory.decodeResource(resources, st.drawableId);
            canvas.drawBitmap(sticker, null, RectF(st.left, st.top, st.right, st.bottom), paint0)
        }
        imageView.invalidate()
    }

    private fun applyInvertedFilter() {
        if (originalFilterImageInverted == null) {
            val width: Int = originalImage!!.getWidth()
            val height: Int = originalImage!!.getHeight()
            val bmOut = Bitmap.createBitmap(
                originalImage!!.width,
                originalImage!!.height,
                originalImage!!.getConfig()
            )
            var A: Int
            var R: Int
            var G: Int
            var B: Int
            var pixel: Int
            for (x in 0 until width) {
                for (y in 0 until height) {
                    pixel = originalImage!!.getPixel(x, y)
                    A = Color.alpha(pixel)
                    R = Color.red(pixel)
                    G = Color.green(pixel)
                    B = Color.blue(pixel)
                    bmOut.setPixel(x, y, Color.argb(A, 255 - R, 255 - G, 255 - B))
                }
            }
            originalFilterImageInverted = bmOut.copy(Bitmap.Config.ARGB_8888, true);
        }
        originalFilterImage = originalFilterImageInverted!!.copy(Bitmap.Config.ARGB_8888, true)
        drawStickers(null)
    }

    private fun applyBlackAndWhiteFilter() {
        if (originalFilterImageBlackAndWhite == null) {
            val bwBitmap =
                Bitmap.createBitmap(originalImage!!.width, originalImage!!.height, Bitmap.Config.ARGB_8888)
            val hsv = FloatArray(3)
            for (col in 0 until originalImage!!.width) {
                for (row in 0 until originalImage!!.height) {
                    Color.colorToHSV(originalImage!!.getPixel(col, row), hsv)
                    if (hsv[2] > 0.5f) {
                        bwBitmap.setPixel(col, row, -0x1)
                    } else {
                        bwBitmap.setPixel(col, row, -0x1000000)
                    }
                }
            }
            originalFilterImageBlackAndWhite = bwBitmap.copy(Bitmap.Config.ARGB_8888, true)
        }
        originalFilterImage = originalFilterImageBlackAndWhite!!.copy(Bitmap.Config.ARGB_8888, true);
        drawStickers(null)
    }

    private fun applyNormalFilter() {
        // todo
        originalFilterImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true);
        drawStickers(null)

    }

    private fun nextFilter() {
        // busy = true
        when (currentFilter) {
            FilterType.NORMAL -> {
                applyBlackAndWhiteFilter()
                currentFilter = FilterType.BLACK_AND_WHITE
            }
            FilterType.BLACK_AND_WHITE -> {
                applyInvertedFilter()
                currentFilter = FilterType.INVERTED
            }
            FilterType.INVERTED -> {
                applyNormalFilter()
                currentFilter = FilterType.NORMAL
            }
        }
        // busy = false
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // nothing
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!busy) {
            busy = true
            val curTime = System.currentTimeMillis()
            // only allow one update every 100ms.
            // println(curTime)
            if (curTime - lastUpdate > 100) {
                val diffTime = curTime - lastUpdate
                lastUpdate = curTime
                val x = event!!.values[SensorManager.DATA_X]
                val y = event!!.values[SensorManager.DATA_Y]
                val z = event!!.values[SensorManager.DATA_Z]
                val speed = Math.abs(x + y + z - last_x - last_y - last_z) / 10000 * 100
                if (speed > 1.1) {
                    Toast.makeText(this, "Changing filter...", Toast.LENGTH_LONG).show();
                    nextFilter()
                }
                last_x = x
                last_y = y
                last_z = z
            }
            busy = false
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

    fun sendPicture() {
        var outputPhoto: FileOutputStream? = null;
        try {
            createImageGallery();
            val imageFile = createImageFile(galleryFolder);
            outputPhoto = FileOutputStream(imageFile);
            imageView?.drawable?.toBitmap()?.compress(Bitmap.CompressFormat.JPEG, 70, outputPhoto);
            //var takenPhoto = TakenPhoto(imageFile.toUri())
            println(" ==> " + galleryFolder.absolutePath)
            var takenPhoto = TakenPhoto(imageFile.toUri(), currentLatLng.latitude, currentLatLng.longitude)
            firestoreViewModel.saveTakenPhoto(takenPhoto)
            firebaseStorageViewModel.saveImage(takenPhoto)
            startActivity(Intent(this, MapsActivity::class.java))
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

}