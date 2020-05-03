package net.azarquiel.rruiz.view

import android.annotation.SuppressLint
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import kotlinx.android.synthetic.main.activity_detail_pedido.*
import net.azarquiel.rruiz.R
import net.azarquiel.rruiz.model.Pedido

class DetailPedido : AppCompatActivity() {

    private lateinit var pedido: Pedido

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_pedido)

        pedido = intent.getSerializableExtra("pedido") as Pedido

        pintar()
    }

    @Suppress("DEPRECATION")
    private fun pintar() {
        detallepedidotvnombre.text = pedido.nombre
        detallepedidotvtlf.text = pedido.tlf
        detallepedidocbdomicilio.isChecked = pedido.domicilio
        detallepedidotvdomicilio.text = pedido.calle

        for(n in pedido.productosnombres.indices) {
            val lh = LinearLayout(this)
            val param: LinearLayout.LayoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            param.weight = 1F
            if(n==0){
                param.setMargins(24,16,24,8)
            }else if (n==pedido.productosnombres.size-1){
                param.setMargins(24,8,24,16)
            }else {
                param.setMargins(24, 8, 24, 8)
            }
            lh.orientation = LinearLayout.HORIZONTAL

            val cantidad = TextView(this)
            cantidad.text = pedido.productoscantidades[n].toString()
            cantidad.setTextSize(16F)
            cantidad.setTextColor(resources.getColor(R.color.colorAccent))
            cantidad.setTypeface(null, Typeface.BOLD)
            val paramc: LinearLayout.LayoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            paramc.rightMargin = 16
            cantidad.layoutParams = paramc
            lh.addView(cantidad)

            val producto = TextView(this)
            producto.text = pedido.productosnombres[n]
            producto.setTextSize(16F)
            producto.setTextColor(resources.getColor(R.color.colorAccent))
            lh.addView(producto)


            lh.layoutParams = param
            detallepedidolv.addView(lh)
        }
    }
}
