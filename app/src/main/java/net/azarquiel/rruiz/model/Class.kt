package net.azarquiel.rruiz.model

import com.google.firebase.Timestamp
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList


data class Canape(var nombre:String="", var foto:String="", var descripcion : String ="",
                  var precio: String=""):Serializable

data class Image(var nombre: String="", var foto: String="", var path: String=""):Serializable

data class Pedido(var nombre: String="", var tlf:String = "", var diahora:Date,
                  var domicilio:Boolean = false, var calle:String="",
                  var productosnombres : ArrayList<String>,
                  var productoscantidades : ArrayList<Long>):Serializable