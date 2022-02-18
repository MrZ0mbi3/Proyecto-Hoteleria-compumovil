package com.example.proyectohotelpartehoteleria

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class PantallaInicial : AppCompatActivity() {
    lateinit var correoPerfilActual: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        correoPerfilActual= intent.getStringExtra("email")!!
        supportActionBar?.hide()
        setContentView(R.layout.activity_pantalla_inicial)

    }


    fun RegistrarPunto(view: android.view.View)
    {
        if(ContextCompat.checkSelfPermission(this.applicationContext,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) )
            {
                Toast.makeText(this.applicationContext, R.string.AskingForMapsPermission, Toast.LENGTH_SHORT ).show()
            }
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),0)
            if ( ContextCompat.checkSelfPermission(this.applicationContext,android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED )
            {
                var intent= Intent (this.applicationContext, MapsRegistroPuntosActivity::class.java)
                intent.putExtra("email",correoPerfilActual)
                startActivity(intent)
            }
            else{
                Toast.makeText(this.applicationContext, R.string.AskingForMapsPermission, Toast.LENGTH_SHORT ).show()
            }

        }
        else
        {
            var intent= Intent (this.applicationContext, MapsRegistroPuntosActivity::class.java)
            intent.putExtra("email",correoPerfilActual)
            startActivity(intent)
        }

    }
    fun MirarPuntos(view: android.view.View)
    {
        if(ContextCompat.checkSelfPermission(this.applicationContext,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) )
            {
                Toast.makeText(this.applicationContext, R.string.AskingForMapsPermission, Toast.LENGTH_SHORT ).show()
            }
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),0)
            if ( ContextCompat.checkSelfPermission(this.applicationContext,android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED )
            {
                var intent= Intent (this.applicationContext, MapsMirarPuntosActivity::class.java)
                intent.putExtra("email",correoPerfilActual)
                startActivity(intent)
            }
            else{
                Toast.makeText(this.applicationContext, R.string.AskingForMapsPermission, Toast.LENGTH_SHORT ).show()
            }

        }
        else
        {


            var intent= Intent (this.applicationContext, MapsMirarPuntosActivity::class.java)
            intent.putExtra("email",correoPerfilActual)
            startActivity(intent)

        }

    }


}