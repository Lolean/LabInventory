package com.example.labinventory

import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.user_pattern.*
import kotlinx.android.synthetic.main.user_pattern.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.log
import kotlin.collections.MutableList

class UserListActivity: AppCompatActivity() {

    lateinit var loggedUser: User
    private val userList = mutableListOf<User>()
    var dbConnect = QueryExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        loggedUser = intent.getSerializableExtra("loggedUser") as User

        /* Contrôle d'accès */
        if(loggedUser.right==0){ /* Controle des droits de l'utilisateurs */
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Vous n'avez pas les droits nécessaires pour voir ces informations")
            builder.setPositiveButton("OK") {_,_ ->
                val intentretour = Intent(this@UserListActivity,MenuActivity::class.java)
                intentretour.putExtra("loggedUser", loggedUser)
                startActivity(intentretour)
                finish()
            }

        }
        /* mise en page de la vue */
        list_tv_title.text="Users"
        list_bt_addDevice.visibility=View.GONE
        fillUserList()


    }
    // classe interne pour la vue
    inner class UserView(context: Context, user: User): LinearLayout(context){

        init{

            View.inflate(context, R.layout.user_pattern,this)

            userpattern_tv_email.text = user.email
            userpattern_tv_username.text = user.username
            userpattern_tv_right.text = user.right.toString()
            if(userpattern_tv_right.text=="2") {
                userpattern_bt_promote.visibility = View.GONE
                userpattern_bt_delete.visibility = View.GONE
            }
            if(loggedUser.right==1 && userpattern_tv_right.text=="1"){
                userpattern_bt_promote.visibility=View.GONE
                userpattern_bt_delete.visibility=View.GONE
            }
        }
    }
    // remplir la Userlist pour la vue
    fun fillUserList(){
        runBlocking {
            launch(Dispatchers.IO){

                var cursor: Cursor
                var dbConnect = QueryExecutor()
                cursor = dbConnect.getAll(this@UserListActivity,"users")
                if(cursor.moveToFirst()){
                    //façon brute de ne pas sauter le premier utilisateur
                    var email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                    var username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
                    var id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
                    var right = cursor.getInt(cursor.getColumnIndexOrThrow("rights"))
                    var user = User(email,username,id,right)

                    userList.add(user)
                    while(cursor.moveToNext()){
                        var email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                        var username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
                        var id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
                        var right = cursor.getInt(cursor.getColumnIndexOrThrow("rights"))
                        var user = User(email,username,id,right)

                        userList.add(user)
                    }
                    val view = findViewById<View>(android.R.id.content)
                    Snackbar.make(view,"Liste d'utilisateur récupéré",Snackbar.LENGTH_SHORT).show()
                    //ajout à la vue
                    mapToView()
                }
                else{
                    var builder = AlertDialog.Builder(this@UserListActivity)
                    builder.setMessage("La requête à la base de donnée a échoué")
                    builder.setPositiveButton("Réessayer"){_,_ ->
                        fillUserList()
                    }
                    builder.setNegativeButton("Revenir au menu"){_, _ ->
                        val intent = Intent(this@UserListActivity,MenuActivity::class.java)
                        intent.putExtra("loggedUser",loggedUser)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    // mapping de chaque user de la list dans la scrollview
    fun mapToView(){

        var childLinear = LinearLayout(this@UserListActivity)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
        childLinear.layoutParams = layoutParams
        childLinear.orientation=LinearLayout.VERTICAL
        for(user in userList){
           var userpattern = UserView(this@UserListActivity,user)
            childLinear.addView(userpattern)
        }
        list_sv_entities.addView(childLinear)


    }

    // list des fonctions par boutons
    fun userListPress(v: View){
        when(v.id){
            userpattern_bt_promote.id -> promoteUser(v)
            userpattern_bt_delete.id -> deleteUser(v)
        }
    }

    fun promoteUser(v: View){
        // Code brut pour récupérer les text views de la vue
        var prombtparent = v.parent as LinearLayout
        var prombtgp = prombtparent.parent as LinearLayout
        var prombttextviews = prombtgp.getChildAt(0) as LinearLayout
        var usernametv = prombttextviews.getChildAt(1) as TextView
        //thread de modification
        runBlocking {
            launch(Dispatchers.IO) {
                dbConnect.modifyEntryInt(this@UserListActivity,"users","username = ?", arrayOf(usernametv.text.toString()),"rights",1)
                restart()
            }
        }

    }

    fun deleteUser(v: View){
        // même logique que plus haut
        var delbtparent = v.parent as LinearLayout
        var delbtgp = delbtparent.parent as LinearLayout
        var delbttextviews = delbtgp.getChildAt(0) as LinearLayout

        var usernametv = delbttextviews.getChildAt(1) as TextView
        runBlocking {
            launch(Dispatchers.IO) {
                dbConnect.deleteEntry(this@UserListActivity,"users","username = ?", arrayOf(usernametv.text.toString()))
                restart()

            }
        }


    }

    fun restart(){
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntentWithParentStack(intent)
        stackBuilder.startActivities()
    }





}