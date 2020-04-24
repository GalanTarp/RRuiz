package net.azarquiel.rruiz.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

import net.azarquiel.rruiz.R
import net.azarquiel.rruiz.adapter.AdapterPedidos

/**
 * A simple [Fragment] subclass.
 */
class PedidosFragment : Fragment() {

    private lateinit var adapter: AdapterPedidos
    private lateinit var rvpedidos: RecyclerView
    private lateinit var db: FirebaseFirestore
    private var pedidos: ArrayList<Pedidos> = ArrayList()

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

        rvcanape = view.findViewById(R.id.rvcanapes) as RecyclerView
        adapter = CustomAdapter(requireActivity().baseContext, R.layout.rowcanape)
        initRV()
        setListener()

        val fab: FloatingActionButton = view.findViewById(R.id.fabaddcanape)
        fab.setOnClickListener {  val intent = Intent(requireActivity().baseContext, AddNewCanape::class.java)
            startActivity(intent) }
    }


    private fun initRV() {
        rvcanapes.adapter = adapter
        rvcanapes.layoutManager = LinearLayoutManager(activity)
    }
/*
    private fun addData(){
        val canape = hashMapOf(
            "nombre" to "Los Angeles"
        )

        db.collection("cities")
            .add(canape as Map<String, Any>)
            .addOnSuccessListener { Log.d(MainActivity.TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(MainActivity.TAG, "Error writing document", e) }

    }*/

    private fun setListener() {
        val docRef = db.collection("canapes")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(MainActivity.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                documentToList(snapshot.documents)
                adapter.setCanapes(canapes)
            } else {
                Log.d(MainActivity.TAG, "Current data: null")
            }
        }
    }

    private fun documentToList(documents: List<DocumentSnapshot>) {
        canapes.clear()
        documents.forEach { d ->
            val nombre = d["nombre"] as String
            val desc = d["descripcion"] as String
            val foto = d["foto"] as String
            canapes.add(Canape(nombre = nombre, descripcion = desc, foto = foto))
        }
    }

}
