package com.example.proyectohoteleria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth

class IniciarRedesSocialesActivity : AppCompatActivity() {
    private val callbackmanager = CallbackManager.Factory.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar_redes_sociales)
    }

    fun IniciarsesionFacebook(view: android.view.View)
    {
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
        LoginManager.getInstance().registerCallback(callbackmanager,
            object : FacebookCallback<LoginResult>{
                override fun onSuccess(result: LoginResult) {
                    result?.let {
                        val token = it.accessToken
                        val credential = FacebookAuthProvider.getCredential(token.token)
                        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { it->
                            if (it.isSuccessful)
                            {
                                Toast.makeText(this@IniciarRedesSocialesActivity,it.result?.user?.email,Toast.LENGTH_SHORT).show()


                            }
                            else{
                                Toast.makeText(this@IniciarRedesSocialesActivity,R.string.InvalidSignIn,Toast.LENGTH_SHORT).show()

                            }
                        }
                    }
                    Toast.makeText(this@IniciarRedesSocialesActivity,R.string.SignInFacebookSuccessful,Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: FacebookException) {
                    Toast.makeText(this@IniciarRedesSocialesActivity,R.string.ErrorWithFacebookAccount,Toast.LENGTH_SHORT).show()
                }

                override fun onCancel() {
                    TODO("Not yet implemented")
                }
            }
        )

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackmanager.onActivityResult(requestCode,resultCode,data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}