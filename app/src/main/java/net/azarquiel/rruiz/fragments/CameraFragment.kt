package net.azarquiel.rruiz.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
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
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.android.synthetic.main.nav_header_main.*
import net.azarquiel.rruiz.R
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 */
class CameraFragment : Fragment() {

    companion object {
        const val REQUEST_GALLERY = 1
    }

    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef : StorageReference
    private lateinit var imagesRef : StorageReference
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


        val camerabtnsel: Button = view.findViewById(R.id.camerabtnsel)
        camerabtnsel.setOnClickListener { photoFromGallery() }
        val camerabtnup: Button = view.findViewById(R.id.camerabtnup)
        camerabtnup.setOnClickListener { showNameDialog() }
        val camerabtnrotate: Button = view.findViewById(R.id.camerabtnrotate)
        camerabtnrotate.setOnClickListener {
            val aux = cameraiv.drawable.toBitmap()
            val matrix = Matrix()
            matrix.postRotate(90F)
            cameraiv.setImageBitmap(Bitmap.createBitmap(aux, 0,0,aux.width,aux.height, matrix,true)) }
        storage = FirebaseStorage.getInstance()

        // Create a storage reference from our app
        storageRef = storage.reference



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
                                requireActivity().baseContext.contentResolver,
                                contentURI
                            )
                            cameraiv.setImageBitmap(bitmap)

                        } catch (e: IOException) {
                            Toast.makeText(requireActivity().baseContext, "Failed!", Toast.LENGTH_SHORT)
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

        val builder = AlertDialog.Builder(requireActivity())
        val inflater = layoutInflater
        builder.setTitle("Escribe un nombre para la foto")
        val dialogLayout = inflater.inflate(R.layout.alert_layout_galleryname, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.alertgalleryname)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Aceptar") { _, _ ->
            nombre = editText.text.toString()
            Toast.makeText(
                requireActivity(),
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
        imagesRef = storageRef.child("foro/$nombre.jpeg")


        val uploadTask = imagesRef.putBytes(data)
        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
            println("Upload is $progress% done")
        }.addOnPausedListener {
            println("Upload is paused")
        }.addOnFailureListener {
            Toast.makeText(
                requireActivity().baseContext,
                "La foto fallo",
                Toast.LENGTH_SHORT
            ).show()
        }.addOnSuccessListener {
            Toast.makeText(
                requireActivity().baseContext,
                "La foto se subio con exito",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


}
