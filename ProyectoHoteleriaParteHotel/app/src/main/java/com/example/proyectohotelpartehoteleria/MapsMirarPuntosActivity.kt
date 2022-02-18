package com.example.proyectohotelpartehoteleria

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.proyectohotelpartehoteleria.databinding.ActivityMapsMirarPuntosBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date

class MapsMirarPuntosActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsMirarPuntosBinding
    lateinit var correoPerfilActual: String
    lateinit var nombreEmpresa : String
    val  puntos: MutableMap<String, LatLng> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        correoPerfilActual= intent.getStringExtra("email")!!
        buscarEmpresa()

        supportActionBar?.hide()

        binding = ActivityMapsMirarPuntosBinding.inflate(layoutInflater)
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

        // Add a marker in Sydney and move the camera
        /*val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/




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
        buscarPuntos()
    }

    fun buscarPuntos()
    {
        val db = Firebase.firestore
        var p : Map <String,String>
        var fechaActual = SimpleDateFormat("dd/M/yyyy").format(Date())
        var aux = fechaActual.split("/")



        db.collection("Rutas").get().addOnSuccessListener { result->
            for (doc in result)
            {
                p= doc.data["data"] as Map<String, String>
                if ( p["empresa"] == nombreEmpresa)
                {
                    var fechaGuardada = p["fechaFinaliza"].toString().split("/")
                    if((fechaGuardada[2].toInt() > aux[2].toInt()) ||
                        (fechaGuardada[2].toInt() == aux[2].toInt() && fechaGuardada[1].toInt() > aux[1].toInt())||
                        (fechaGuardada[2].toInt() == aux[2].toInt() && fechaGuardada[1].toInt() == aux[1].toInt() && fechaGuardada[0].toInt() >= aux[0].toInt()))
                    {
                        var auxUbi = p["ubicacion"].toString().split(",")
                        puntos.put(p["nombrePunto"].toString(), LatLng(auxUbi[0].toDouble(),auxUbi[1].toDouble()))

                    }


                }
            }
            for ( nombrePunto in puntos.keys)
            {
                mMap.addMarker(MarkerOptions().position(puntos[nombrePunto]).title(nombrePunto))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(puntos[nombrePunto]))
            }


        }

    }
}