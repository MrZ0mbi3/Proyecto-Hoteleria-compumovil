package com.example.proyectohotelpartehoteleria

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class RegistrarActivity : AppCompatActivity() {
    private lateinit var functions: FirebaseFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_registrar)
        functions = Firebase.functions
    }

    fun Registrar(view: android.view.View)
    {
        var nombre: EditText = findViewById(R.id.editTextNombreRegister)
        var email: EditText = findViewById(R.id.editTextEmailRegister)
        var telefono: EditText = findViewById(R.id.editTextPhoneRegister)
        var pass : EditText = findViewById(R.id.editTextPasswordRegister)

        if (nombre.text.isNotEmpty() && email.text.isNotEmpty() && telefono.text.isNotEmpty() && pass.text.isNotEmpty())
        {
            val data : MutableMap<String, Any> = HashMap()
            data ["nombre"]= nombre.text.toString()
            data ["email"]= email.text.toString()
            data ["telefono"]= telefono.text.toString()


            FirebaseFunctions.getInstance().getHttpsCallable("registrarEmpresa")
                .call(data)
                .continueWith{it->
                    val resultado = it.result?.data as String
                    Toast.makeText(this, resultado, Toast.LENGTH_SHORT).show()
                }
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.text.toString(), pass.text.toString()).addOnCompleteListener { it->
                if (it.isSuccessful)
                {
                    Toast.makeText(this,R.string.RegisterCompleted, Toast.LENGTH_SHORT).show()
                    finish()
                }
                else
                {
                    Toast.makeText(this,R.string.RegisterProblem, Toast.LENGTH_SHORT).show()
                }
            }

        }
        else
        {
            Toast.makeText(this,R.string.Emptyfield, Toast.LENGTH_SHORT).show()
        }
    }
}