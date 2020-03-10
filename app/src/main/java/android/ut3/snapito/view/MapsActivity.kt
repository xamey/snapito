package android.ut3.snapito.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import android.ut3.snapito.R
import android.ut3.snapito.model.maps.MyClusterItem
import android.ut3.snapito.renderer.ClusteredMarkerRender
import android.ut3.snapito.viewmodel.FirebaseStorageViewModel
import android.ut3.snapito.viewmodel.FirestoreViewModel
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.ClusterManager
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MapsActivity(

) : AppCompatActivity(), OnMapReadyCallback {


    private val firestoreViewModel: FirestoreViewModel by inject()
    private val firebaseStorageViewModel: FirebaseStorageViewModel by inject()

    private lateinit var mMap: GoogleMap
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mManager: ClusterManager<MyClusterItem>
    private lateinit var clusteredMarkerRender: ClusteredMarkerRender



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
    }

    fun initMarkers() {
        firestoreViewModel.getStoredPhotos().observe(this, Observer {
            mManager.clearItems()
            it.forEach{
                    photo -> mManager.addItem(
                MyClusterItem(
                    photo.lat,
                    photo.long,
                    photo.title,
                    photo.author
                )
            )
            }
        })
    }

    fun handleClusterEvents() {
        mManager.setOnClusterClickListener { cluster ->
            //check if cluster is just pictures at the same position
            var item = cluster.items.elementAt(0)
            if (cluster.items.filter { s -> item.position == s.position}.size == cluster.size) {
                //TODO afficher plusieurs photos
                return@setOnClusterClickListener true
            }
            var builder = LatLngBounds.builder()
            cluster.items.forEach{ item ->
                builder.include(item.position)
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100))
            return@setOnClusterClickListener true
        }
        mManager.setOnClusterItemClickListener { item ->
            firebaseStorageViewModel.title = item.title
            startActivity(Intent(this, ShowPictureActivity::class.java))
            return@setOnClusterItemClickListener true
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setUpClusterer()
    }

    private fun setUpClusterer() {
        mManager = ClusterManager(this, mMap)
        clusteredMarkerRender =
            ClusteredMarkerRender(
                this,
                mMap,
                mManager
            )
        mManager.renderer = clusteredMarkerRender
        mMap.setOnCameraIdleListener(mManager)
        mMap.setOnMarkerClickListener(mManager)
        handleClusterEvents()
        initMarkers()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        var latlng: LatLng = LatLng(location.latitude, location.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12f))

                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
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
            var latlng: LatLng = LatLng(mLastLocation.latitude, mLastLocation.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng))
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Granted. Start getting the location information
            }
        }
    }



}
