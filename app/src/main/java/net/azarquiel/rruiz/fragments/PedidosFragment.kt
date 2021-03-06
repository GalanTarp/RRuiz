package net.azarquiel.rruiz.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

import net.azarquiel.rruiz.R
import net.azarquiel.rruiz.adapter.AdapterPedidos
import net.azarquiel.rruiz.model.Pedido
import net.azarquiel.rruiz.view.AddNewPedido
import net.azarquiel.rruiz.view.MainActivity
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class PedidosFragment : Fragment() {

    private lateinit var adapter: AdapterPedidos
    private lateinit var rvpedidos: RecyclerView
    private lateinit var db: FirebaseFirestore
    private var pedidos: ArrayList<Pedido> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pedidos, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        rvpedidos = view.findViewById(R.id.rvpedidos) as RecyclerView
        adapter = AdapterPedidos(requireActivity().baseContext, R.layout.rowpedido)
        initRV()
        setListener()

        val fab: FloatingActionButton = view.findViewById(R.id.fabaddpedido)
        fab.setOnClickListener {
            val intent = Intent(requireActivity().baseContext, AddNewPedido::class.java)
            startActivityForResult(intent, 0)
        }
    }


    private fun initRV() {
        rvpedidos.adapter = adapter
        rvpedidos.layoutManager = LinearLayoutManager(activity)
    }

    private fun setListener() {
        val docRef = db.collection("pedidos")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(MainActivity.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                documentToList(snapshot.documents)

            } else {
                Log.d(MainActivity.TAG, "Current data: null")
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun documentToList(documents: List<DocumentSnapshot>) {
        pedidos.clear()
        documents.forEach { d ->
            val nombre = d["nombre"] as String
            val tlf = d["tlf"] as String
            val diahora = d["diahora"] as Timestamp
            val domicilio = d["domicilio"] as Boolean
            val calle = d["calle"] as String
            val productosnombres = d["productosnombres"] as ArrayList<String>
            val productoscantidades = d["productoscantidades"] as ArrayList<Long>
            pedidos.add(Pedido(nombre = nombre, tlf = tlf, diahora = diahora.toDate(), domicilio = domicilio,
                calle = calle, productosnombres = productosnombres,
                productoscantidades = productoscantidades))
        }
        pedidos.sortWith(compareBy<Pedido> { it.diahora.year }.thenBy { it.diahora.month }.thenBy { it.diahora.date }.thenBy
            { it.diahora.hours }.thenBy { it.diahora.minutes })
        adapter.setPedidos(pedidos)
    }

}
