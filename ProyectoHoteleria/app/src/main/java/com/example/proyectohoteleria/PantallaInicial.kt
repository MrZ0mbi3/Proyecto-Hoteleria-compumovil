package com.example.proyectohoteleria

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_pantalla_inicial.*

class PantallaInicial : AppCompatActivity() {
    lateinit var ciudades: MutableSet<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        buscarCiudades()
        setContentView(R.layout.activity_pantalla_inicial)

        perfil.setOnClickListener{
            startActivity(Intent(this.applicationContext, Foto::class.java))
        }

    }

    fun buscarCiudades()
    {
        val db = Firebase.firestore
        var p : Map <String,String>
        ciudades = mutableSetOf()

        db.collection("Rutas").get().addOnSuccessListener { resultado->
            for (doc in resultado)
            {
                p= doc.data["data"] as Map<String, String>
                if (p["ciudad"] != null)
                {
                    ciudades += p["ciudad"].toString()


                }
            }
            mostrarCiudades()

        }



    }

    private fun mostrarCiudades() {

        var tablaBotonesCiudades : TableLayout = findViewById(R.id.tableLayoutbotonesCiudad)
        tablaBotonesCiudades.removeAllViews()
        for (ciudad in ciudades)
        {
            var tableRow : View = LayoutInflater.from(this).inflate(R.layout.table_button_city,null,false)
            var botonCiudad : Button = tableRow.findViewById(R.id.botonNombreCiudad)
            botonCiudad.text = ciudad
            tablaBotonesCiudades.addView(tableRow)

        }
    }

    public fun verPuntosCiudad(view: android.view.View)
    {
        var tablerow : TableRow = view.parent as TableRow
        var boton : Button = tablerow.findViewById(R.id.botonNombreCiudad)
        var intent = Intent(this.applicationContext,mapaPuntosCiudadActivity::class.java)
        intent.putExtra("ciudad",boton.text.toString())
        startActivity(intent)


    }

    fun IniciarRedSocial(view: android.view.View)
    {
        var intent = Intent(this.applicationContext,IniciarRedesSocialesActivity::class.java)
        startActivity(intent)
    }

    fun mensajes(view: android.view.View)
    {
        var intent = Intent(this.applicationContext,Lista::class.java)
        startActivity(intent)
    }
}