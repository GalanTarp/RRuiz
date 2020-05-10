package net.azarquiel.rruiz.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import net.azarquiel.rruiz.R
import net.azarquiel.rruiz.fragments.CameraFragment
import net.azarquiel.rruiz.fragments.CanapeFragment
import net.azarquiel.rruiz.fragments.GalleryFragment
import net.azarquiel.rruiz.fragments.PedidosFragment
import net.azarquiel.rruiz.model.Canape
import net.azarquiel.rruiz.model.Image
import net.azarquiel.rruiz.model.Pedido


class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    companion object{
        const val TAG = "AppPrueba"
        private const val REQUEST_ADD = 0
    }

    private lateinit var user: FirebaseUser
    private lateinit var auth: FirebaseAuth
    private var nfragment: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)



        user = FirebaseAuth.getInstance().currentUser!!
        auth = FirebaseAuth.getInstance()

        val cajontvemail = nav_view.getHeaderView(0).cajontvemail
        val cajontvname = nav_view.getHeaderView(0).cajontvname
        cajontvemail.text = user.email.toString()
        cajontvname.text = user.displayName
        setInitialFragment()

        nav_view.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_ADD) {
                val intent = Intent(this, DetailPedido::class.java)
                intent.putExtra("pedido", data!!.getSerializableExtra("pedido") as Pedido)
                startActivity(intent)
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        var fragment: Fragment? = null
        when (item.itemId) {
            R.id.nav_camera -> {
                fragment = CameraFragment()
                nfragment = 0
            }
            R.id.nav_gallery -> {
                fragment = GalleryFragment()
                nfragment = 1
            }
            R.id.nav_canapes -> {
                fragment = CanapeFragment()
                nfragment = 2
            }
            R.id.nav_pedidos -> {
                fragment = PedidosFragment()
                nfragment = 3
            }
        }
        replaceFragment(fragment!!)
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // Iniciamos con DeviceFragment colocandolo en el FrameLayout preparado
    // para ello que reside en el layout principal activity_main.xml

    private fun setInitialFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frame,
            CanapeFragment()
        )
        fragmentTransaction.commit()
        nfragment = 2
    }

    // Cambiamos de fragmento
    // cada vez que elegimos una opcion del menu del BottonNavigationView

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame, fragment)
        fragmentTransaction.commit()
    }


    fun onClickImage(v: View){
        val storageref = v.tag as StorageReference
        storageref.downloadUrl.addOnSuccessListener {
            val imagepulsado = Image(
                foto = it.toString(),
                nombre = storageref.name,
                path = storageref.path
            )
            val intent = Intent(this, DetailGallery::class.java)
            intent.putExtra("image", imagepulsado)
            startActivity(intent)
        }.addOnFailureListener {
            Toast.makeText(this, "Download Uri Failled",
                Toast.LENGTH_SHORT).show()
        }

    }

    fun onClickCanape(v: View){
        val canapepulsado = v.tag as Canape
        val intent = Intent(this, DetailCanape::class.java)
        intent.putExtra("canape", canapepulsado)
        startActivity(intent)
    }

    fun onClickPedido(v: View){
        val pedidopulsado = v.tag as Pedido
        val intent = Intent(this, DetailPedido::class.java)
        intent.putExtra("pedido", pedidopulsado)
        startActivity(intent)
    }


}
