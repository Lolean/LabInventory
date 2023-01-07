package com.example.labinventory

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking



class LoginActivity : AppCompatActivity() {

    /* classe de requête à la db sqlite */
    var dbConnect = QueryExecutor()

    lateinit var loggedUser: User

    /* Création de l'activité */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btContext = intent.getStringExtra("context")
        if(btContext=="LOG IN") setLogin()
        else setRegister()

    }
    /* switch de fonctions/boutons */
    fun logPagePress(v: View) {
        when(v.id){
            login_bt_toLogin.id -> setLogin()
            login_bt_toRegister.id -> setRegister()
            login_bt_submit.id -> submit(login_bt_submit.text.toString(),v)
        }
    }
    /* changement visuel vers login */
    private fun setLogin(){
        login_tv_login.visibility=View.VISIBLE
        login_tv_signup.visibility=View.GONE
        login_et_username.visibility=View.GONE
        login_et_confirmPassword.visibility=View.GONE
        login_bt_submit.text="LOGIN"
        login_block_toRegister.visibility=View.VISIBLE
        login_block_toLogin.visibility=View.GONE
    }
    /* Changement visuel vers register */
    private fun setRegister(){
        login_tv_login.visibility=View.GONE
        login_tv_signup.visibility=View.VISIBLE
        login_et_username.visibility=View.VISIBLE
        login_et_confirmPassword.visibility=View.VISIBLE
        login_bt_submit.text="SIGNUP"
        login_block_toRegister.visibility=View.GONE
        login_block_toLogin.visibility=View.VISIBLE
    }
    /* fonction de login / register */
    private fun submit(s: String,v: View) {
        runBlocking {
            launch(Dispatchers.IO) {
            /* LOGIN */
            if (login_et_email.text.isNotEmpty() && login_et_password.text.isNotEmpty()) {
                var email = login_et_email.text.toString()

                if (s == "LOGIN") {
                    var pswd = login_et_password.text.toString().hashCode().toString()
                    var cursor = dbConnect.login(this@LoginActivity, email, pswd)


                    if (cursor.moveToFirst()) {
                        Snackbar.make(v, "Connexion réussie", Snackbar.LENGTH_SHORT).show()
                        loggedUser = User(cursor.getString(cursor.getColumnIndexOrThrow("email")),cursor.getString(cursor.getColumnIndexOrThrow("username")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("_id")),cursor.getInt(cursor.getColumnIndexOrThrow("rights")))
                        val intent = Intent(this@LoginActivity,MenuActivity::class.java)
                        intent.putExtra("loggedUser",loggedUser)
                        startActivity(intent)
                        finish()

                    } else Snackbar.make(v, "Connexion échouée", Snackbar.LENGTH_SHORT).show()
                }
                /* REGISTER */
                else {

                    if (login_et_username.text.isNotEmpty() && login_et_confirmPassword.text.isNotEmpty()) {
                        if (login_et_password.text.toString() == login_et_confirmPassword.text.toString()) {
                            var pswd = login_et_password.text.toString().hashCode().toString()
                            var username = login_et_username.text.toString()

                            if (dbConnect.createUser(this@LoginActivity, email, username, pswd)) {
                                Snackbar.make(v, "utilisateur créé", Snackbar.LENGTH_SHORT).show()


                            } else Snackbar.make(
                                v,
                                "Requête de création échouée",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }

                }

            } else Snackbar.make(
                v,
                "Les champs requis n'ont pas été remplis",
                Snackbar.LENGTH_SHORT
            ).show()
        }
        }

    }
}