package net.azarquiel.rruiz.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_login.*
import net.azarquiel.rruiz.R
import net.azarquiel.rruiz.fragments.CameraFragment

class LoginActivity : AppCompatActivity() {

    companion object{
        const val REQUEST_PERMISSION = 200
        const val REQUEST_GALLERY = 1

        const val TAG = "Login"
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var nombre : String
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val ad : AnimationDrawable = layoutlogin.background as AnimationDrawable
        ad.setEnterFadeDuration(1000)
        ad.setExitFadeDuration(1000)
        ad.start()

        progressBar= findViewById(R.id.progressBar)
        progressBar.visibility = View.INVISIBLE

        auth = FirebaseAuth.getInstance()

        checkPermiss()

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // User is signed in
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("user", user)
            intent.putExtra("muestropedido", false)
            startActivity(intent)
        }

        loginbtnsignin.setOnClickListener {
            signIn(loginetemail.text.toString(), loginetpass.text.toString())
        }
        loginbtnregister.setOnClickListener {
            showNameDialog()

        }
        loginbtnlogout.setOnClickListener {
            signOut()
            Toast.makeText(baseContext, "Sesion cerrada",
                Toast.LENGTH_SHORT).show()
        }

    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.

    }

    private fun createAccount(email: String, password: String) {
        Log.d(TAG, "createAccount:$email")
        if (!validateForm()) {
            return
        }
        progressBar.visibility = View.VISIBLE
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(nombre)
                        .build()
                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                Log.d(TAG, "User profile updated.")
                            }
                        }
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener {
                            if (task.isSuccessful) {
                                Log.d(TAG, "Email sent.")
                            }
                        }
                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(baseContext, "Se le ha enviado un correo de autentificación",
                        Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails, display a message to the user.
                    progressBar.visibility = View.INVISIBLE
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        // [END create_user_with_email]
    }

    private fun signIn(email: String, password: String) {
        Log.d(TAG, "signIn:$email")
        if (!validateForm()) {
            return
        }

        progressBar.visibility = View.VISIBLE
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    if (user != null) {
                        if(user.isEmailVerified){
                            progressBar.visibility = View.INVISIBLE
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("user", user)
                            startActivity(intent)
                        }else{
                            progressBar.visibility = View.INVISIBLE
                            Toast.makeText(baseContext, "Debe verificar su cuenta, por medio del correo enviado",
                                Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    progressBar.visibility = View.INVISIBLE
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }

            }
        // [END sign_in_with_email]
    }

    private fun signOut() {
        auth.signOut()
    }

    @SuppressLint("InflateParams")
    private fun showNameDialog() {

        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Escribe tu nombre")
        val dialogLayout = inflater.inflate(R.layout.alert_layout_name, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.nameet)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Aceptar") { _, _ ->
            nombre = editText.text.toString()
            progressBar.visibility = View.VISIBLE
            Toast.makeText(applicationContext, "Tu nombre es " + editText.text.toString(), Toast.LENGTH_SHORT).show()
            createAccount(loginetemail.text.toString(), loginetpass.text.toString())}
        builder.show()

    }



    private fun validateForm(): Boolean {
        var valid = true

        val email = loginetemail.text.toString()
        if (TextUtils.isEmpty(email)) {
            loginetemail.error = "Requerido."
            valid = false
        } else {
            loginetemail.error = null
        }

        val password = loginetpass.text.toString()
        if (TextUtils.isEmpty(password)) {
            loginetpass.error = "Requerido."
            valid = false
        } else {
            loginetpass.error = null
        }

        return valid
    }

    private fun checkPermiss() {
        if (    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                REQUEST_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Para utilizar la APP debes aceptar", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}

