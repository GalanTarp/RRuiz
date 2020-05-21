package net.azarquiel.rruiz.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_new_pedido.*
import net.azarquiel.rruiz.R
import net.azarquiel.rruiz.model.Canape
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates


class AddNewPedido : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private var canapes = ArrayList<Canape>()
    private var productosNombresSeleccionados = ArrayList<String>()
    private var productosCantidadSeleccionados = ArrayList<Int>()
    private var productosPrecioSeleccionados = ArrayList<Double>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_pedido)

        db = FirebaseFirestore.getInstance()
        setListener()

        newpedidoeddomicilio.isEnabled = false

        newpedidobtnfecha.setOnClickListener {
            pickDateTime()
        }

        newpedidocbdomicilio.setOnClickListener {
            newpedidoeddomicilio.isEnabled = newpedidocbdomicilio.isChecked
        }

        newpedidobtnanadir.setOnClickListener {
            showselectDialog()
        }

        newpedidobtnaceptar.setOnClickListener {
            subirPedido()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun actualizartotal(){
        var total = 0.0
        for(i in productosCantidadSeleccionados.indices){
            total += productosCantidadSeleccionados[i] * productosPrecioSeleccionados[i]
        }
        newpedidotvtotal.text = "Total: ${"%.2f".format(total)} €"
    }



    @SuppressLint("SetTextI18n")
    @Suppress("DEPRECATION")
    private fun pickDateTime() {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, day ->
            TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                val meses : List<String> = listOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")
                val pickedDateTime = Calendar.getInstance()
                pickedDateTime.set(year, month, day, hour, minute)
                newpedidotvfecha.tag = Timestamp(Date(year, month, day, hour, minute))
                if(hour<10 && minute<10){
                    newpedidotvfecha.text = "0$hour:0$minute $day de ${meses[month]}"
                }else if(hour<10){
                    newpedidotvfecha.text = "0$hour:$minute $day de ${meses[month]}"
                }else if(minute<10){
                    newpedidotvfecha.text = "$hour:0$minute $day de ${meses[month]}"
                }else {
                    newpedidotvfecha.text = "$hour:$minute $day de ${meses[month]}"
                }
            }, startHour, startMinute, true).show()
        }, startYear, startMonth, startDay).show()
    }


    private fun showselectDialog() {
        val selectDialog = AlertDialog.Builder(this)
        selectDialog.setTitle("Selecciona un tipo")
        val selectDialogItems = arrayOf("CANAPE", "OTROS")
        selectDialog.setItems(selectDialogItems
        )
        //{dialog, option}
        { _, option ->
            when (option) {
                0 -> showcantidadDialog()
                1 -> showmultichoiceDialog(0)
            }
        }
        selectDialog.show()
    }

    private fun showcantidadDialog() {
        val selectDialog = AlertDialog.Builder(this)
        selectDialog.setTitle("Selecciona cantidad")
        val selectDialogItems = arrayOf("SUELTOS", "MEDIA BANDEJA", "BANDEJA ENTERA")
        selectDialog.setItems(selectDialogItems
        )
        //{dialog, option}
        { _, option ->
            when (option) {
                0 -> showmultichoiceDialog(0)
                1 -> showmultichoiceDialog(10)
                2 -> showmultichoiceDialog(20)
            }
        }
        selectDialog.show()
    }

    private fun showmultichoiceDialog(n :Int) {
        // Late initialize an alert dialog object
        lateinit var dialog:AlertDialog

        val selectedItems = ArrayList<Int>() // Where we track the selected items

        // Initialize an array of canapes
        val arrayCanapes = arrayOfNulls<String>(canapes.size)
        for (i in 0 until canapes.size){
            arrayCanapes[i]=(canapes[i].nombre)
        }

        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(this)

        // Set a title for alert dialog
        builder.setTitle("Selecciona los productos")

        // Define multiple choice items for alert dialog
        builder.setMultiChoiceItems(arrayCanapes, null) { _, which, isChecked->
            if (isChecked) {
                // If the user checked the item, add it to the selected items
                selectedItems.add(which)
            } else if (selectedItems.contains(which)) {
                // Else, if the item is already in the array, remove it
                selectedItems.remove(Integer.valueOf(which))
            }
        }


        // Set the positive/yes button click listener
        builder.setPositiveButton("OK") { _, _ ->
            // Do something when click positive button

            for (int in selectedItems) {
                arrayCanapes[int]?.let {
                    var bandera = false
                    for(p in productosNombresSeleccionados){
                        if(p == it) {
                            bandera = true
                        }
                    }
                    if(!bandera){
                        anadirProducto(it, n)
                        productosNombresSeleccionados.add(it)
                        productosCantidadSeleccionados.add(n)
                        productosPrecioSeleccionados.add(canapes[int].precio.toDouble())
                    }

                    actualizartotal()
                }
            }
        }


        // Initialize the AlertDialog using builder object
        dialog = builder.create()

        // Finally, display the alert dialog
        dialog.show()
    }



    private fun anadirProducto(nombre : String, n : Int){


        val lh = LinearLayout (this)
        val param : LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        param.setMargins(0,8,0,8)
        lh.orientation = LinearLayout.HORIZONTAL

        val cantidad = TextView(this)
        val param2 : LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        param2.setMargins(5,0,5,0)
        cantidad.layoutParams=param2
        cantidad.text = n.toString()

        val btnmenos = Button(this)
        btnmenos.text = "-"
        btnmenos.setBackgroundResource(R.drawable.buttonlogin2)
        btnmenos.setOnClickListener {
            for(prod in productosNombresSeleccionados.indices) {
                if(nombre == productosNombresSeleccionados[prod]){
                    if(productosCantidadSeleccionados[prod]==0){
                        showSureDialog(lh, nombre)
                    }else{
                        productosCantidadSeleccionados[prod] = productosCantidadSeleccionados[prod]-1
                        cantidad.text = productosCantidadSeleccionados[prod].toString()

                        actualizartotal()
                    }
                }
            }
        }
        lh.addView(btnmenos)

        lh.addView(cantidad)

        val btnmas = Button(this)
        btnmas.text = "+"
        btnmas.setBackgroundResource(R.drawable.buttonlogin2)
        btnmas.setOnClickListener {
            for(prod in productosNombresSeleccionados.indices) {
                if(nombre == productosNombresSeleccionados[prod]){
                    productosCantidadSeleccionados[prod] = productosCantidadSeleccionados[prod]+1
                    cantidad.text = productosCantidadSeleccionados[prod].toString()

                    actualizartotal()
                }
            }
        }
        lh.addView(btnmas)

        val producto = TextView(this)
        producto.text = nombre
        lh.addView(producto)
        lh.layoutParams = param
        newpedidolinear.addView(lh)
    }

    @SuppressLint("InflateParams")
    private fun showSureDialog(lh : LinearLayout, nombre :String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eliminar producto")
        builder.setMessage("¿Estas seguro de querer retirar el producto de la lista?")
        builder.setPositiveButton("Aceptar") { _, _ ->
            newpedidolinear.removeView(lh)
            var bandera = false
            var aux by Delegates.notNull<Int>()
            for(prod in productosNombresSeleccionados.indices) {
                if (nombre == productosNombresSeleccionados[prod]) {
                    bandera = true
                    aux = prod
                }
            }
            if(bandera){
                productosNombresSeleccionados.removeAt(aux)
                productosCantidadSeleccionados.removeAt(aux)
                productosPrecioSeleccionados.removeAt(aux)

                actualizartotal()
            }
        }
        builder.show()
    }



    private fun setListener() {
        val docRef = db.collection("canapes")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                documentToList(snapshot.documents)
            }
        }
    }

    private fun documentToList(documents: List<DocumentSnapshot>) {
        canapes.clear()
        canapes.add(Canape(nombre = "Variado", precio = "0.8"))
        documents.forEach { d ->
            val nombre = d["nombre"] as String
            val precio = d["precio"] as String
            canapes.add(Canape(nombre = nombre, precio = precio))
        }
    }


    private fun subirPedido(){
        var bandera = false
        for (n in productosCantidadSeleccionados){
            if (n==0){
                bandera=true
            }
        }
        if(newpedidoednombre.text.toString() == ""){
            Toast.makeText(this, "Falta nombre",
                Toast.LENGTH_SHORT).show()
        }else if(newpedidoedtelefono.text.toString() == ""){
            Toast.makeText(this, "Falta telefono",
                Toast.LENGTH_SHORT).show()
        }else if(newpedidotvfecha.text == ""){
            Toast.makeText(this, "Falta fecha",
                Toast.LENGTH_SHORT).show()
        }else if(newpedidoeddomicilio.text.toString() == "" && newpedidocbdomicilio.isChecked){
            Toast.makeText(this, "Falta domicilio",
                Toast.LENGTH_SHORT).show()
        }else if(productosNombresSeleccionados.size == 0){
            Toast.makeText(this, "Faltan productos",
                Toast.LENGTH_SHORT).show()
        }else if(bandera){
            Toast.makeText(this, "Hay producto/s con 0 canapes",
                Toast.LENGTH_SHORT).show()
        }else{
           addData()
        }
    }

    @Suppress("DEPRECATION")
    private fun addData(){
        val pedido = hashMapOf(
            "nombre" to newpedidoednombre.text.toString(),
            "tlf" to newpedidoedtelefono.text.toString(),
            "diahora" to newpedidotvfecha.tag,
            "domicilio" to newpedidocbdomicilio.isChecked,
            "calle" to newpedidoeddomicilio.text.toString(),
            "productosnombres" to productosNombresSeleccionados,
            "productoscantidades" to productosCantidadSeleccionados/*,
            "productosprecios" to productosPrecioSeleccionados*/
        )

        db.collection("pedidos")
            .add(pedido as Map<String, Any>)
            .addOnSuccessListener { Toast.makeText(this, "DocumentSnapshot successfully written!",
                Toast.LENGTH_SHORT).show()
                intent.putExtra("pedido", pedido)
                setResult(Activity.RESULT_OK,intent)
                finish()
            }
            .addOnFailureListener {Toast.makeText(this, "Error writing document",
                Toast.LENGTH_SHORT).show() }

    }


}
