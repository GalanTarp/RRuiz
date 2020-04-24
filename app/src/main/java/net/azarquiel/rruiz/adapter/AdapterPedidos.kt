package net.azarquiel.rruiz.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class AdapterPedidos(
    val context: Context,
    val layout: Int
) : RecyclerView.Adapter<AdapterPedidos.ViewHolder>() {

    private var dataList: List<StorageReference> = emptyList()
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

    internal fun setImages(images: List<StorageReference>) {
        this.dataList = images
        notifyDataSetChanged()
    }


    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: StorageReference){
            // itemview es el item de diseño
            // al que hay que poner los datos del objeto dataItem
            Log.d(TAG, dataItem.path)
            dataItem.getBytes(Long.MAX_VALUE).addOnSuccessListener {
                val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
                itemView.rowivgallery.setImageBitmap(
                    Bitmap.createScaledBitmap(bmp, itemView.rowivgallery.width,
                        itemView.rowivgallery.height, false))
            }.addOnFailureListener {
                // Handle any errors
            }
            itemView.rowgallerytv.text = dataItem.name
            itemView.tag = dataItem
        }

    }
}
