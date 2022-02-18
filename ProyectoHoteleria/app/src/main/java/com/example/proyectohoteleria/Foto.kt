package com.example.proyectohoteleria

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_foto.*
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.NoSuchElementException


private const val REQUEST_CODE = 42

class Foto : AppCompatActivity ()
{
    private val CAPTURE_PHOTO = 104
    private val IMAGE_PICK_CODE = 107
    private lateinit var storage : FirebaseStorage
    private lateinit var storageReference : StorageReference
    lateinit var imageUri : Uri

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foto)

        storage = FirebaseStorage.getInstance()
        storageReference = storage.getReference()

        bt_pick.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this@Foto, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
                }
                else
                {
                    escogerFoto()
                }

            }
        }

        bt_open.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this@Foto, arrayOf(Manifest.permission.CAMERA), 2)
                }
                else
                {
                    tomarFoto()
                }
            }
        }
    }

    fun tomarFoto ()
    {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAPTURE_PHOTO)
    }

    fun escogerFoto ()
    {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    fun guardarEnGaleria (view : ImageView)
    {
        var bitmapDrawable : BitmapDrawable = view.drawable as BitmapDrawable
        var bitmap : Bitmap = bitmapDrawable.bitmap
        var outputStream : FileOutputStream? = null
        var file : File = Environment.getExternalStorageDirectory()
        var dir : File = File(file.absolutePath + "/Travelers")

        dir.mkdirs()

        var filename : String = String.format("%d.png", System.currentTimeMillis())
        var outFile : File = File(dir, filename)

        try
        {
            outputStream = FileOutputStream(outFile)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

        try
        {
            outputStream?.flush()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }

        try
        {
            outputStream?.close()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    fun escalarImagen (view : ImageView)
    {
        var bitmap : Bitmap? = null

        try
        {
            var drawing : Drawable = view.drawable
            bitmap = (drawing as BitmapDrawable).bitmap
        }
        catch (e : NullPointerException)
        {
            throw NoSuchElementException("No hay dibujable en la vista.")
        }
        catch (e : ClassCastException)
        {
        }

        var width : Int = 0

        try
        {
            width = bitmap!!.width
        }
        catch (e : NullPointerException)
        {
            throw NoSuchElementException("No se encuentra el bitmap en el view/drawable.")
        }

        var height : Int = bitmap.height
        var bounding : Int = dpToPx(250)
        var xScale : Float = (bounding as Float) / width
        var yScale : Float = (bounding as Float) / height
        var scale : Float

        if (xScale <= yScale)
        {
            scale = xScale
        }
        else
        {
            scale = yScale
        }

        var matrix : Matrix? = null
        matrix?.postScale(scale, scale)

        var scaledBitmap : Bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,true)
        width = scaledBitmap.width
        height = scaledBitmap.height

        var result : BitmapDrawable = BitmapDrawable(scaledBitmap)

        view.setImageDrawable(result)
        view.layoutParams.width = width
        view.layoutParams.height = height
    }

    private fun dpToPx (dp : Int) : Int
    {
        var density : Float = applicationContext.resources.displayMetrics.density
        return Math.round((dp as Float) * density)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == CAPTURE_PHOTO)
            {
                when (requestCode)
                {
                    CAPTURE_PHOTO -> {
                        val captureImage = data!!.extras!!.get("data") as Bitmap
                        image_view.setImageBitmap(captureImage)
                    }
                    else -> {

                    }
                }
            }
            else if (requestCode == IMAGE_PICK_CODE)
            {
                imageUri = data?.data!!
                image_view.setImageURI(imageUri)
                subirFoto()
            }
        }
    }

    fun subirFoto ()
    {
        val pd : ProgressDialog = ProgressDialog(this)
        pd.setTitle("Subiendo imagen...")
        pd.show()
        val randomKey : String = UUID.randomUUID().toString()
        val riversRef : StorageReference = storageReference.child("images/" + randomKey)

        riversRef.putFile(imageUri)
            .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot>(){
                fun onSuccess (taskSnapshot : UploadTask.TaskSnapshot)
                {
                    pd.dismiss()
                    Snackbar.make(findViewById(android.R.id.content), "Imagen subida", Snackbar.LENGTH_LONG).show()
                }
            })
            .addOnFailureListener(OnFailureListener(){
                fun onFailure (exception : Exception)
                {
                    pd.dismiss()
                    Toast.makeText(applicationContext, "Falló la subida", Toast.LENGTH_LONG).show()
                }
            })
            .addOnProgressListener(OnProgressListener<UploadTask.TaskSnapshot>(){
                fun onProgress (taskSnapshot : UploadTask.TaskSnapshot)
                {
                    val progressPercent : Double = (100.00 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                    pd.setMessage("Progreso: " + progressPercent as Int + "%")
                }
            })
    }
}
