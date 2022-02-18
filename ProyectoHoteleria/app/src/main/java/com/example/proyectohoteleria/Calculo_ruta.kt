package com.example.proyectohoteleria

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import kotlinx.android.synthetic.main.*
import kotlinx.android.synthetic.main.activity_trazado.*
import okhttp3.OkHttpClient
import okhttp3.Request

class Calculo_ruta : AppCompatActivity (), OnMapReadyCallback {

    companion object
    {
        const val ORIGEN_REQUEST_CODE = 1
        const val DESTINO_REQUEST_CODE = 2
        const val TAG = "MainActivity"
    }

    lateinit var mMap: GoogleMap
    var mMarkerOrigen: Marker? = null
    var mFromLatLng: LatLng? = null
    var mMarkerDestino: Marker? = null
    var mToLatLng: LatLng? = null
    val listaLatLng = ArrayList<LatLng>()
    var polyline : Polyline? = null
    var addressOrigen : String? = "Ubicación actual"
    var addressDestino: String? = null
    val REQUEST_CODE_LOCATION_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trazado)
        configurarMapa()
        configurarPlaces()

        if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION.toString()), REQUEST_CODE_LOCATION_PERMISSION)
        }
        else
        {
            getCurrentLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.size > 0)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                getCurrentLocation()
            }
            else
            {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation ()
    {

        val LR : LocationRequest = LocationRequest()

        LR.setInterval(10000)
        LR.setFastestInterval(3000)
        LR.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(LR, object :
            LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult)
            {
                super.onLocationResult(locationResult)
                LocationServices.getFusedLocationProviderClient(this@Calculo_ruta).removeLocationUpdates(this)

                if (locationResult != null && locationResult.locations.size > 0)
                {
                    val lastestLocationIndex = locationResult.locations.size - 1
                    mFromLatLng = LatLng(locationResult.locations.get(lastestLocationIndex).latitude, locationResult.locations.get(lastestLocationIndex).longitude)
                    setMarcadorOrigen(mFromLatLng!!)
                    mToLatLng = LatLng(intent.getStringExtra("latitude")!!.toDouble(), intent.getStringExtra("longitude")!!.toDouble())
                    setMarcadorDestino(mToLatLng!!)
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(mFromLatLng))
                }
            } }, Looper.getMainLooper())
    }

    private fun configurarMapa() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun configurarPlaces() {
        Places.initialize(applicationContext, getString(R.string.android_sdk_places_api_key))

        botonOrigen.setOnClickListener {
            iniciarAutoComplete(ORIGEN_REQUEST_CODE)
        }

        botonDestino.setOnClickListener {
            iniciarAutoComplete(DESTINO_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ORIGEN_REQUEST_CODE) {
            processAutocompleteResult(resultCode, data)
            { place ->
                TXOrigen.text = getString(R.string.direccion_origen, place.address)
                addressOrigen = place.address
                place.latLng?.let {
                    mFromLatLng = it
                    setMarcadorOrigen(it)
                }
            }

            return
        } else if (requestCode == DESTINO_REQUEST_CODE) {
            processAutocompleteResult(resultCode, data)
            { place ->
                TXDestino.text = getString(R.string.direccion_destino, place.address)
                addressDestino = place.address
                place.latLng?.let {
                    mToLatLng = it
                    setMarcadorDestino(it)
                }
            }

            return
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun processAutocompleteResult(
        resultCode: Int,
        data: Intent?,
        callback: (Place) -> Unit
    ) {
        Log.d(TAG, "processAutocompleteResult(resultCode=$resultCode)")

        when (resultCode) {
            Activity.RESULT_OK -> {
                data?.let {
                    val place = Autocomplete.getPlaceFromIntent(data)
                    Log.i(TAG, "Place: $place")
                    callback(place)
                }
            }
            AutocompleteActivity.RESULT_ERROR -> {
                data?.let {
                    val status = Autocomplete.getStatusFromIntent(data)
                    status.statusMessage?.let { message ->
                        Log.i(TAG, message)
                    }
                }
            }
        }
    }

    fun iniciarAutoComplete(requestCode: Int) {
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        val fields =
            listOf(Place.Field.ADDRESS, Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this)
        startActivityForResult(intent, requestCode)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMinZoomPreference(15f)
        mMap.setMaxZoomPreference(20f)
    }

    private fun agregarMarcador(latLng: LatLng, titulo: String): Marker {
        val opcionesDeMarcador = MarkerOptions().position(latLng).title(titulo)

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))

        return mMap.addMarker(opcionesDeMarcador)
    }

    private fun setMarcadorOrigen(latLng: LatLng) {

        polyline?.remove()
        mMarkerOrigen?.remove()
        mMarkerOrigen = agregarMarcador(latLng, "Ubicación actual")
        hacerLista(latLng, 1)

        if (listaLatLng.size > 1)
        {
            computarInformacionDeViaje()
        }
    }

    private fun setMarcadorDestino(latLng: LatLng) {

        polyline?.remove()
        mMarkerDestino?.remove()
        mMarkerDestino = agregarMarcador(latLng, "Destino")
        hacerLista(latLng, 2)

        if (listaLatLng.size > 1)
        {
            computarInformacionDeViaje()
        }
    }

    private fun hacerLista(latLng: LatLng, indice: Int) {

        if (indice == 1) {
            listaLatLng.add(0, latLng)
        } else if (indice == 2) {
            listaLatLng.add(1, latLng)
        }
    }

    private fun computarInformacionDeViaje()
    {
        val URL = mFromLatLng?.let { mToLatLng?.let { it1 -> getDirectionURL(it, it1) } }
        URL?.let { GetDirection(it).execute() }
    }

    fun getDirectionURL(origin: LatLng, dest: LatLng): String {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${dest.latitude},${dest.longitude}&key=AIzaSyBr2Rahmgp68p9V-QACQCeBmYZ1MqLqkac"
    }

    inner class GetDirection(val url: String) : AsyncTask<Void, Void, List<List<LatLng>>>() {
        override fun doInBackground(vararg p0: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body()!!.string()
            val result = ArrayList<List<LatLng>>()
            try {
                val respObj = Gson().fromJson(data, GoogleMapDTO::class.java)
                val path = ArrayList<LatLng>()
                for (i in 0..(respObj.routes[0].legs[0].steps.size - 1)) {
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>?) {

            val lineoption = PolylineOptions()
            if (result != null) {
                for (i in result.indices) {
                    lineoption.addAll(result[i])
                    lineoption.width(10f)
                    lineoption.color(Color.BLUE)
                    lineoption.geodesic(true)
                }

            }


            polyline = mMap.addPolyline(lineoption)
        }
    }

    public fun decodePolyline(encoded: String): List<LatLng> {

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

            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }

        return poly
    }
}
