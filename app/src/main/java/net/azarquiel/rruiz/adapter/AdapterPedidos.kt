package net.azarquiel.rruiz.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rowpedido.view.*
import net.azarquiel.rruiz.model.Pedido
import java.util.*

class AdapterPedidos(
    val context: Context,
    val layout: Int
) : RecyclerView.Adapter<AdapterPedidos.ViewHolder>() {


    private var dataList: List<Pedido> = emptyList()

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


    @Suppress("DEPRECATION")
    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        @SuppressLint("SetTextI18n")
        fun bind(dataItem: Pedido){
            // itemview es el item de diseño
            // al que hay que poner los datos del objeto dataItem
            itemView.rowpedidotvnombre.text = dataItem.nombre
            itemView.rowpedidotvtlf.text = dataItem.tlf
            var aux = 0L
            for(n in dataItem.productoscantidades){
                aux += n
            }
            itemView.rowpedidocbdomicilio.isChecked = dataItem.domicilio
            itemView.rowpedidotvcantidad.text = "$aux canapes"
            val fecha:Date = dataItem.diahora
            val meses : List<String> = listOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")
            if(fecha.hours<10 && fecha.minutes<10){
                itemView.rowpedidotvfecha.text =
                    "0${fecha.hours}:0${fecha.minutes} ${fecha.date} de ${meses[fecha.month]}"
            }else if(fecha.hours<10){
                itemView.rowpedidotvfecha.text =
                    "0${fecha.hours}:${fecha.minutes} ${fecha.date} de ${meses[fecha.month]}"
            }else if(fecha.minutes<10){
                itemView.rowpedidotvfecha.text =
                    "${fecha.hours}:0${fecha.minutes} ${fecha.date} de ${meses[fecha.month]}"
            }else {
                itemView.rowpedidotvfecha.text =
                    "${fecha.hours}:${fecha.minutes} ${fecha.date} de ${meses[fecha.month]}"
            }
            itemView.tag = dataItem
        }

    }
}
