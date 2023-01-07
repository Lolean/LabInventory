package com.example.labinventory

import android.app.TaskStackBuilder
import android.content.Intent


import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }



    fun Press(v: View){
        when(v.id){
            main_bt_login.id -> toLogPage(main_bt_login.text.toString())
            main_bt_signup.id -> toLogPage(main_bt_signup.text.toString())
        }
    }

    fun toLogPage(s: String){
        val intent = Intent(this@MainActivity,LoginActivity::class.java)
        intent.putExtra("context",s)
        startActivity(intent)
        finish()
    }
}