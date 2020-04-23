package net.azarquiel.rruiz.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_gallery.*
import net.azarquiel.rruiz.R
import net.azarquiel.rruiz.model.Image
import java.io.File

class DetailGallery : AppCompatActivity() {

    private lateinit var image:Image
    private lateinit var reference: StorageReference

//    private var mScaleGestureDetector: ScaleGestureDetector? = null
//    private var mScaleFactor = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_gallery)
        image = intent.getSerializableExtra("image") as Image
        pintar()
        reference = FirebaseStorage.getInstance().reference.child(image.path)
//        mScaleGestureDetector = ScaleGestureDetector(this, ScaleListener())

    }
    private fun pintar() {
        Picasso.get().load(image.foto).into(gallerydetailiv)
        gallerydetailtv.text = image.nombre
    }

    /*override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        mScaleGestureDetector?.onTouchEvent(motionEvent)
        return true
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            mScaleFactor *= scaleGestureDetector.scaleFactor
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f))
            gallerydetailiv?.setScaleX(mScaleFactor)
            gallerydetailiv?.setScaleY(mScaleFactor)
            return true
        }
    }*/
}
