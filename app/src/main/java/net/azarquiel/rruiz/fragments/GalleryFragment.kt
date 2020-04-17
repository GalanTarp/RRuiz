package net.azarquiel.rruiz.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.firebase.storage.FirebaseStorage
import net.azarquiel.rruiz.R


/**
 * A simple [Fragment] subclass.
 */
class GalleryFragment : Fragment() {

    private lateinit var storage: FirebaseStorage
    private var fotos: ArrayList<Bitmap> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storage = FirebaseStorage.getInstance()

        // Create a storage reference from our app
        var storageRef = storage.reference

        // Create a child reference
        // imagesRef now points to "images"
        var imagesRef = storageRef.child("images.jepg")

        // ImageView in your Activity
        val imageView = view.findViewById<ImageView>(R.id.galleryiv)

        // Download directly from StorageReference using Glide
        // (See MyAppGlideModule for Loader registration)
        storageRef.child("images.jepg").getBytes(Long.MAX_VALUE).addOnSuccessListener {
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, imageView.width,
                imageView.height, false))
        }.addOnFailureListener {
            // Handle any errors
        }
/*
        if (imagesRef != null) {
            imagesRef.listAll()
                .addOnSuccessListener { listResult ->
                    listResult.prefixes.forEach { prefix ->
                        // All the prefixes under listRef.
                        // You may call listAll() recursively on them.
                    }

                    listResult.items.forEach { item ->
                        fotos.add(item)
                    }
                }
                .addOnFailureListener {
                    // Uh-oh, an error occurred!
                }
        }*/

    }
}
