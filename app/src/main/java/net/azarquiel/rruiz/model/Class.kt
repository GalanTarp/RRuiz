package net.azarquiel.rruiz.model

import android.graphics.Bitmap
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.Serializable


data class Canape(var nombre:String="", var foto:String="", var descripcion : String =""):Serializable

data class Image(var nombre: String="", var foto: String="", var path: String=""):Serializable

