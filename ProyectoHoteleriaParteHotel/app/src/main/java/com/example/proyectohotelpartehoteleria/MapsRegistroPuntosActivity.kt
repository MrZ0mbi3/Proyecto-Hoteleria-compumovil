package com.example.proyectohotelpartehoteleria

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.proyectohotelpartehoteleria.databinding.ActivityMapsRegistroPuntosBinding
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase

class MapsRegistroPuntosActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsRegistroPuntosBinding
    private lateinit var punto: LatLng
    lateinit var correoPerfilActual: String
    lateinit var nombreEmpresa : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        correoPerfilActual= intent.getStringExtra("email")!!
        buscarEmpresa()


        binding = ActivityMapsRegistroPuntosBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        mMap.setOnMapLongClickListener {it ->
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(it))
            punto = it

        }
    }

    fun GuardarPunto(view: android.view.View)
    {
        var campoNombrePunto: EditText = findViewById(R.id.editTextNombrePunto)
        var campoTagsPunto: EditText = findViewById(R.id.editTextTagsPunto)
        var campoDia: EditText = findViewById(R.id.editTextNumberDiaRegistroPunto)
        var campoMes: EditText = findViewById(R.id.editTextNumberMesRegistroPunto)
        var campoAnio: EditText = findViewById(R.id.editTextAnioRegistroPunto)
        var campoCiudad: EditText = findViewById(R.id.editTextCiudadPunto)



        if(campoAnio.text.isNotEmpty() && campoDia.text.isNotEmpty() && campoMes.text.isNotEmpty() && campoNombrePunto.text.isNotEmpty() && punto != null && campoCiudad.text.isNotEmpty())
        {
            val data : MutableMap<String, Any> = HashMap()
            data ["empresa"]= nombreEmpresa
            data ["nombrePunto"]= campoNombrePunto.text.toString()
            data ["tags"] = campoTagsPunto.text.toString()
            data ["fechaFinaliza"] = campoDia.text.toString()+"/"+campoMes.text.toString()+"/"+campoAnio.text.toString()
            data ["ubicacion"] = punto.latitude.toString()+","+punto.longitude.toString()
            data ["ciudad"] = campoCiudad.text.toString()

            FirebaseFunctions.getInstance().getHttpsCallable("registrarRuta")
                .call(data)
                .continueWith{it->
                    finish()
                }
        }
        else
        {
            Toast.makeText(this,R.string.Emptyfield, Toast.LENGTH_SHORT).show()
        }

    }

    fun buscarEmpresa ()
    {
        val db = Firebase.firestore
        var p : Map <String,String>
        db.collection("Empresas").get().addOnSuccessListener { resultado->
            for (documento in resultado)
            {
                p= documento.data["data"] as Map<String, String>
                if (p["email"] == correoPerfilActual)
                {
                    nombreEmpresa = p["nombre"].toString()
                }

            }
        }
    }


}