package net.azarquiel.rruiz.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.azarquiel.rruiz.model.Pedido

class AdapterPedidos(
    val context: Context,
    val layout: Int
) : RecyclerView.Adapter<AdapterPedidos.ViewHolder>() {

    private var dataList: List<Pedido> = emptyList()
    companion object {
        const val TAG = "adapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    internal fun setPedidos(pedidos: List<Pedido>) {
        this.dataList = pedidos
        notifyDataSetChanged()
    }


    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: Pedido){
            // itemview es el item de diseño
            // al que hay que poner los datos del objeto dataItem
            Log.d(TAG, dataItem.toString())
//            itemView.rowgallerytv.text = dataItem.name
            itemView.tag = dataItem
        }

    }
}
