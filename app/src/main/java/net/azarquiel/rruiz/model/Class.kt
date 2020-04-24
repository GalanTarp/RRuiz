package net.azarquiel.rruiz.model

import android.graphics.Bitmap
import com.google.firebase.Timestamp
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.Serializable


data class Canape(var nombre:String="", var foto:String="", var descripcion : String =""):Serializable

data class Image(var nombre: String="", var foto: String="", var path: String=""):Serializable

data class Pedido(var nombre: String="", var tlf:Long=0, var diahora:Timestamp,
                  var domicilio:Boolean = false, var calle:String=""):Serializable

