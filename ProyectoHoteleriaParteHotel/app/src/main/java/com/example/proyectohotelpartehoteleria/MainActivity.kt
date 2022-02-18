package com.example.proyectohotelpartehoteleria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
    }

    fun signUp(view: android.view.View)
    {
        var intent = Intent(this.applicationContext, RegistrarActivity::class.java)
        startActivity(intent)

    }

    fun signIn(view: android.view.View)
    {
        var campoEmail: EditText = findViewById(R.id.editTextEmailAddress)
        var campoPassword: EditText = findViewById(R.id.editTextPasswordSignIn)
        var intent = Intent(this.applicationContext,PantallaInicial::class.java)
        if (campoEmail.text.isNotEmpty() && campoPassword.text.isNotEmpty())
        {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(campoEmail.text.toString(),campoPassword.text.toString()).addOnCompleteListener { it->
                if (it.isSuccessful)
                {
                    intent.putExtra("email", campoEmail.text.toString())
                    campoEmail.text=null
                    campoPassword.text=null


                    startActivity(intent)
                }
                else
                {
                    Toast.makeText(this,R.string.InvalidSignIn, Toast.LENGTH_SHORT).show()
                }

            }


        }
        else
        {
            Toast.makeText(this,R.string.Emptyfield, Toast.LENGTH_SHORT).show()

        }

    }

}



