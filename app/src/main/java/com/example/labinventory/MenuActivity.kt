package com.example.labinventory

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity: AppCompatActivity() {

    lateinit var loggedUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        loggedUser = intent.getSerializableExtra("loggedUser") as User
        menu_tv_welcome.text="Welcome ${loggedUser.username}"
        if(loggedUser.right>0) menu_bt_users.visibility=View.VISIBLE
    }

    fun menuClick(v: View){
        when(v.id){
            menu_bt_users.id -> toUsers()
            menu_bt_devices.id -> toDevices()
        }
    }

    fun toUsers(){
        val intent = Intent(this@MenuActivity,UserListActivity::class.java)
        intent.putExtra("loggedUser",loggedUser)
        startActivity(intent)
        finish()

    }

    fun toDevices(){
        val intent = Intent(this@MenuActivity,DeviceListActivity::class.java)
        intent.putExtra("loggedUser",loggedUser)
        startActivity(intent)
        finish()


    }

}