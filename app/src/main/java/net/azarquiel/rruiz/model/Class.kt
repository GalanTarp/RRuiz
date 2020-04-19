package net.azarquiel.rruiz.model

import android.graphics.Bitmap
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

data class Canape(var nombre:String="")

data class CanapeIn(var nombre:String="", var foto: ByteArrayInputStream, var descripcion : String ="")

data class CanapeOut(var nombre:String="", var foto: ByteArrayOutputStream, var descripcion : String ="")
