@file:Suppress("DEPRECATION")

package com.example.discoorario

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {
    val TIMER_ACTIVITY = 1
    val TAG = "MainActivity"
    private val LOCATION_PERMISSION_REQUEST = 1
    private lateinit var map: GoogleMap
    private var mGoogleApiClient: GoogleApiClient? = null
    var mLocationRequest: LocationRequest? = null
    lateinit var lastMarkOption: MarkerOptions
    var mCurrLocationMarker: Marker? = null
    var previusLocation: Location? = null
    var longitude : Float = 0.0F
    var latitude : Float = 0.0F


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onResume() {
        super.onResume()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onPause() {
        super.onPause()

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
        }
    }

    private fun getLocationAccess() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true

            buildGoogleApiClient()
        } else
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
    }

    @Synchronized
    private fun buildGoogleApiClient() {

        if(mGoogleApiClient == null) {
            mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
            mGoogleApiClient!!.connect()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                map.isMyLocationEnabled = true

                buildGoogleApiClient()
            } else {
                Toast.makeText(
                    this,
                    "User has not granted location access permission",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }


    fun setNewPosition(latLng: LatLng): MarkerOptions? {
        lastMarkOption = MarkerOptions()
            .position(latLng)
            .title("My car is here!")

        map.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomIn())
        map.animateCamera(CameraUpdateFactory.zoomTo(20f), 3000, null)

        return lastMarkOption
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        getLocationAccess()

    }


    fun openActivityTimer(v: View) {
        Log.v(TAG, "onClick")
        val intent = Intent(this@MainActivity, TimerActivity::class.java)
        intent.putExtra("longitude", longitude)
        intent.putExtra("latitude", latitude)
        startActivityForResult(intent, TIMER_ACTIVITY)
    }

    override fun onConnected(p0: Bundle?) {

        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 1000
        mLocationRequest!!.fastestInterval = 1000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
            )
        }
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    override fun onLocationChanged(location: Location) {

        if ((previusLocation?.latitude != location.latitude) || (previusLocation?.longitude != location.longitude)) {
            mCurrLocationMarker?.remove()
            previusLocation = location


            val sharedPreferences: SharedPreferences = this.getSharedPreferences("sharedpreference",
                Context.MODE_PRIVATE)

            if(sharedPreferences.getInt("startMinutes", 0) == 0 && sharedPreferences.getInt("stopMinutes", 0) == 0)
            {
                val currentPosition = LatLng(location.latitude, location.longitude)

                latitude = location.latitude.toFloat()
                longitude = location.longitude.toFloat()

                mCurrLocationMarker = map.addMarker(
                    setNewPosition(currentPosition)?.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon))
                )

            }
            else
            //timer già attivo

            {
                val currentPosition = LatLng(sharedPreferences.getFloat("latitude", 0f).toDouble(), sharedPreferences.getFloat("longitude", 0f).toDouble())

                mCurrLocationMarker = map.addMarker(
                    setNewPosition(currentPosition)?.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon))
                )
            }
        }

    }


}
