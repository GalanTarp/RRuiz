package net.azarquiel.rruiz.view

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_login.*
import net.azarquiel.rruiz.R

class LoginActivity : AppCompatActivity() {

    companion object{
        const val TAG = "Login"
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var nombre : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // User is signed in
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
        } else {
            // No user is signed in
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
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "User profile updated.")
                            }
                        }
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener {
                            if (task.isSuccessful) {
                                Log.d(TAG, "Email sent.")
                            }
                        }
                    Toast.makeText(baseContext, "Se le ha enviado un correo de autentificación",
                        Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails, display a message to the user.
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
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    if (user != null) {
                        if(user.isEmailVerified){
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("user", user)
                            startActivity(intent)
                        }else{
                            Toast.makeText(baseContext, "Debe verificar su cuenta, por medio del correo enviado",
                                Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    // If sign in fails, display a message to the user.
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

    private fun showNameDialog() {

        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Escribe tu nombre")
        val dialogLayout = inflater.inflate(R.layout.alert_layout_name, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.nameet)
        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { _, _ ->
            nombre = editText.text.toString()
            Toast.makeText(applicationContext, "Tu nombre es " + editText.text.toString(), Toast.LENGTH_SHORT).show()
            createAccount(loginetemail.text.toString(), loginetpass.text.toString())}
        builder.show()

    }



    private fun validateForm(): Boolean {
        var valid = true

        val email = loginetemail.text.toString()
        if (TextUtils.isEmpty(email)) {
            loginetemail.error = "Required."
            valid = false
        } else {
            loginetemail.error = null
        }

        val password = loginetpass.text.toString()
        if (TextUtils.isEmpty(password)) {
            loginetpass.error = "Required."
            valid = false
        } else {
            loginetpass.error = null
        }

        return valid
    }
}

