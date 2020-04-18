package net.azarquiel.rruiz.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_camera.*
import net.azarquiel.rruiz.R
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 */
class CameraFragment : Fragment() {

    companion object {
        const val REQUEST_PERMISSION = 200
        const val REQUEST_GALLERY = 1
        const val TAG = "kk"
    }

    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef : StorageReference
    private lateinit var imagesRef : StorageReference
    private var puedosubir = false
    private lateinit var nombre :String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermiss()

        val camerabtnsel: Button = view.findViewById(R.id.camerabtnsel)
        camerabtnsel.setOnClickListener { photoFromGallary() }
        val camerabtnup: Button = view.findViewById(R.id.camerabtnup)
        camerabtnup.setOnClickListener {
            Log.d(TAG,"kokokoko")
            showNameDialog() }

        storage = FirebaseStorage.getInstance()

        // Create a storage reference from our app
        storageRef = storage.reference



    }

    private fun photoFromGallary() {
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
                                activity!!.baseContext.contentResolver,
                                contentURI
                            )
                            cameraiv.setImageBitmap(bitmap)

                        } catch (e: IOException) {
                            Toast.makeText(activity!!.baseContext, "Failed!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    else -> {
                    }
                }

            }
        }
    }

    @SuppressLint("InflateParams")
    private fun showNameDialog() {

        val builder = AlertDialog.Builder(activity!!)
        val inflater = layoutInflater
        builder.setTitle("Escribe un nombre para la foto")
        val dialogLayout = inflater.inflate(R.layout.alert_layout_galleryname, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.alertgalleryname)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Aceptar") { _, _ ->
            nombre = editText.text.toString()
            Toast.makeText(
                activity!!,
                "El nombre es " + editText.text.toString(),
                Toast.LENGTH_SHORT
            ).show()
            upphoto()
        }
        builder.show()
    }

    @Suppress("DEPRECATION")
    private fun upphoto(){
        // Get the data from an ImageView as bytes
        cameraiv.isDrawingCacheEnabled = true
        cameraiv.buildDrawingCache()
        val bitmap = (cameraiv.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()


        // Create a child reference
        // imagesRef now points to "images"
        imagesRef = storageRef.child("$nombre.jepg")


        val uploadTask = imagesRef.putBytes(data)
        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
            println("Upload is $progress% done")
        }.addOnPausedListener {
            println("Upload is paused")
        }.addOnFailureListener {
            Toast.makeText(
                activity!!.baseContext,
                "La foto fallo",
                Toast.LENGTH_SHORT
            ).show()
        }.addOnSuccessListener {
            Toast.makeText(
                activity!!.baseContext,
                "La foto se subio con exito",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkPermiss() {
        if (    ContextCompat.checkSelfPermission(activity!!.baseContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ||
            ContextCompat.checkSelfPermission(activity!!.baseContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ||
            ContextCompat.checkSelfPermission(activity!!.baseContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity!!,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                REQUEST_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity!!.baseContext, "Para subir fotos debes aceptar", Toast.LENGTH_SHORT).show()
            }else{
                puedosubir=true
            }
        }
    }
}
