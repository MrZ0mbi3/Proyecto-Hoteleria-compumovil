package com.example.proyectohoteleria

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class InicioSesionActivity : AppCompatActivity() {
    private val GOOGLE_SIGN_IN_CODE_RESULT = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_inicio_sesion)
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
    fun signUp(view: android.view.View)
    {
        var intent = Intent(this.applicationContext, RegistrarActivity::class.java)
        startActivity(intent)

    }

    fun IniciarSesionConGoogle(view: android.view.View)
    {
        val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("683050107027-oip9sj5nlpm2fh0p2tfl4oue50gdc2p2.apps.googleusercontent.com")
            .requestEmail()
            .build()
        val googleClient = GoogleSignIn.getClient(this, googleConf)
        googleClient.signOut()
        startActivityForResult(googleClient.signInIntent,GOOGLE_SIGN_IN_CODE_RESULT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN_CODE_RESULT)
        {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            var intent = Intent(this.applicationContext,PantallaInicial::class.java)
            try {
                val account= task.getResult(ApiException::class.java)

                if (account != null)
                {

                    FirebaseAuth.getInstance().signInWithCredential(GoogleAuthProvider.getCredential(account.idToken,null)).addOnCompleteListener { it->
                        if (it.isSuccessful)
                        {
                            Toast.makeText(this,account.email,Toast.LENGTH_SHORT)
                            startActivity(intent)

                        }
                        else{
                            Toast.makeText(this,R.string.InvalidSignIn,Toast.LENGTH_SHORT)

                        }

                    }
                }

            } catch (e : ApiException)
            {
                Toast.makeText(this,R.string.InvalidSignIn,Toast.LENGTH_SHORT)

            }


        }
    }


}