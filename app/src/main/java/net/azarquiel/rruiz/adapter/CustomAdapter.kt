package net.azarquiel.rruiz.adapter

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rowcanape.view.*
import net.azarquiel.rruiz.model.Canape

class CustomAdapter(
    val context: Context,
    val layout: Int
                    ) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    private var dataList: List<Canape> = emptyList()

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

    internal fun setCanapes(canapes: List<Canape>) {
        this.dataList = canapes
        notifyDataSetChanged()
    }


    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: Canape){
            // itemview es el item de diseño
            // al que hay que poner los datos del objeto dataItem

            Picasso.get().load(dataItem.foto).into(itemView.rowivcanape)

            itemView.rowtvnombre.text = dataItem.nombre


            val ad : AnimationDrawable = itemView.backgroudrowcanape.background as AnimationDrawable
            ad.setEnterFadeDuration(500)
            ad.setExitFadeDuration(1000)
            ad.start()
            itemView.tag = dataItem
        }

    }
}