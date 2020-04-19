package net.azarquiel.rruiz.view

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add_new_canape.*
import net.azarquiel.rruiz.R
import java.io.ByteArrayOutputStream

class AddNewCanape : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var storageRef : StorageReference
    private lateinit var imagesRef : StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_canape)

        db = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().reference


        newcanapebtnfoto.setOnClickListener {

        }

        newcanapebtnaceptar.setOnClickListener {
            upphoto()
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
            "foto" to foto.toString()
        )

        db.collection("canapes")
            .add(canape as Map<String, Any>)
            .addOnSuccessListener { Toast.makeText(this, "DocumentSnapshot successfully written!",
                Toast.LENGTH_SHORT).show() }
            .addOnFailureListener {Toast.makeText(this, "Error writing document",
                Toast.LENGTH_SHORT).show() }

    }
}
