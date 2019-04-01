package com.example.nuber

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.array
import com.beust.klaxon.obj
import com.google.android.gms.common.api.Response
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import org.json.JSONObject

import java.io.IOException
import java.net.URL

import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import com.beust.klaxon.*
import com.google.android.gms.maps.model.*


class NUberMapsActivity : SupportMapFragment(),
    OnMapReadyCallback , GoogleMap.OnMarkerClickListener{

    override fun onMarkerClick(p0: Marker?) = false

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation : Location
    val options = PolylineOptions()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        checkUserLogged()


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val LatLongB = LatLngBounds.Builder()
        // Add a marker in Sydney and move the camera
        mMap.getUiSettings().setZoomControlsEnabled(true)

        mMap.setOnMarkerClickListener(this)
        mMap.isMyLocationEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN

        options.color(Color.RED)
        options.width(5f)

        val origin = LatLng(19.2854883, -99.1348716)
        fusedLocationClient.lastLocation.addOnSuccessListener(activity!!) { location ->
            System.out.println(location)
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                placeMarkerOnMap(currentLatLng, "Destino")
                placeMarkerOnMap(origin, "Casualidades")


               val url = getURL(origin, currentLatLng)

                async {
                    // Connect to URL, download content and convert into string asynchronously
                    val result = URL(url).readText()
                    uiThread {
                        // When API call is done, create parser and convert into JsonObjec
                        val parser: Parser = Parser()
                        val stringBuilder: StringBuilder = StringBuilder(result)
                        val json: JsonObject = parser.parse(stringBuilder) as JsonObject
                        System.out.println("jsonn  $json")
                        // get to the correct element in JsonObject
                        val routes = json.array<JsonObject>("routes")
                        if (routes!!.size > 0) {
                            val points = routes!!["legs"]["steps"][0] as JsonArray<JsonObject>
                            // For every element in the JsonArray, decode the polyline string and pass all points to a List
                            val polypts = points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!)  }
                            // Add  points to polyline and bounds
                            options.add(origin)
                            LatLongB.include(origin)
                            for (point in polypts)  {
                                options.add(point)
                                LatLongB.include(point)
                            }
                            options.add(currentLatLng)
                            LatLongB.include(currentLatLng)
                            // build bounds
                            val bounds = LatLongB.build()
                            // add polyline to the map
                            mMap!!.addPolyline(options)
                            // show map with route centered
                            mMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                        }
                    }
                }
            }
        }




        setUpMap()

    }


    private fun getURL(from : LatLng, to : LatLng) : String {
        val origin = "origin=" + from.latitude + "," + from.longitude
        val dest = "destination=" + to.latitude + "," + to.longitude
        val sensor = "sensor=false"
        val params = "$origin&$dest&$sensor"
        return "https://maps.googleapis.com/maps/api/directions/json?$params&key=${R.string.google_maps_key}"
    }

    private  val LOCATION_PERMISSION_REQUEST_CODE = 666
    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(activity!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
    }

    private fun placeMarkerOnMap(location: LatLng, locationName: String) {
        val markerOptions = MarkerOptions().position(location)
        markerOptions.title(locationName)
        val marker : Marker = mMap.addMarker(markerOptions)
        marker.showInfoWindow()
    }


    private fun checkUserLogged() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(activity!!, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }




    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly
    }

    companion object {
        fun newInstance(): NUberMapsActivity = NUberMapsActivity()
    }


}
