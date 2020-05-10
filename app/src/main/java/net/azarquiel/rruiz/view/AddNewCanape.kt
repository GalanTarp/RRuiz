package net.azarquiel.rruiz.view

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add_new_canape.*
import net.azarquiel.rruiz.R
import java.io.ByteArrayOutputStream
import java.io.IOException

class AddNewCanape : AppCompatActivity() {

    companion object {
        const val REQUEST_GALLERY = 1
    }

    private lateinit var db: FirebaseFirestore
    private lateinit var storageRef : StorageReference
    private lateinit var imagesRef : StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_canape)

        db = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().reference


        newcanapebtnfoto.setOnClickListener {
            photoFromGallery()
        }

        newcanapebtnaceptar.setOnClickListener {
            if(newcanapeednombre.text.toString() != "" && newcanapeeddesc.text.toString() != "" ){
                upphoto()
            }else{
                Toast.makeText(this, "Nombre y Descripción son requeridos", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    private fun photoFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_GALLERY)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data?.let {
                when (requestCode) {
                    REQUEST_GALLERY -> {
                        val contentURI = data.data
                        try {
                            val bitmap = MediaStore.Images.Media.getBitmap(
                                this.contentResolver,
                                contentURI
                            )
                            newcanapeiv.setImageBitmap(bitmap)

                        } catch (e: IOException) {
                            Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    else -> {
                    }
                }

            }
        }
    }

    @Suppress("DEPRECATION")
    private fun upphoto(){
        // Get the data from an ImageView as bytes
        newcanapeiv.isDrawingCacheEnabled = true
        newcanapeiv.buildDrawingCache()
        val bitmap = (newcanapeiv.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        // Create a child reference
        // imagesRef now points to "images"
        imagesRef = storageRef.child("canapes/${newcanapeednombre.text}.jpeg")


        val uploadTask = imagesRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(this, "La foto fallo", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            Toast.makeText(this, "La foto se subio con exito", Toast.LENGTH_SHORT).show()
            getUri()
        }
    }

    private fun getUri(){
        storageRef.child("canapes/${newcanapeednombre.text}.jpeg").downloadUrl.addOnSuccessListener {
            Toast.makeText(this, "Download Uri Correctly",
                Toast.LENGTH_SHORT).show()
            addData(it)
        }.addOnFailureListener {
            Toast.makeText(this, "Download Uri Failled",
                Toast.LENGTH_SHORT).show()
        }
    }

    @Suppress("DEPRECATION")
    private fun addData(foto : Uri){
        val canape = hashMapOf(
            "nombre" to newcanapeednombre.text.toString(),
            "descripcion" to newcanapeeddesc.text.toString(),
            "foto" to foto.toString(),
            "precio" to newcanapeedprecio.text.toString()
        )

        db.collection("canapes")
            .add(canape as Map<String, Any>)
            .addOnSuccessListener { Toast.makeText(this, "DocumentSnapshot successfully written!",
                Toast.LENGTH_SHORT).show()
                super.onBackPressed()
            }
            .addOnFailureListener {Toast.makeText(this, "Error writing document",
                Toast.LENGTH_SHORT).show() }

    }
}
