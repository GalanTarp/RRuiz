package net.azarquiel.rruiz.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import net.azarquiel.rruiz.R
import net.azarquiel.rruiz.fragments.CameraFragment
import net.azarquiel.rruiz.fragments.CanapeFragment
import net.azarquiel.rruiz.fragments.GalleryFragment


class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    companion object{
        const val TAG = "AppPrueba"
    }

    private lateinit var user: FirebaseUser
    private lateinit var auth: FirebaseAuth


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
            }
            R.id.nav_gallery -> {
                fragment = GalleryFragment()
            }
            R.id.nav_canapes -> {
                fragment = CanapeFragment()
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
    }

    // Cambiamos de fragmento
    // cada vez que elegimos una opcion del menu del BottonNavigationView

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame, fragment)
        fragmentTransaction.commit()
    }

}
