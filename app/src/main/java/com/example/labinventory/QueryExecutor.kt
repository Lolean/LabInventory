package com.example.labinventory

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.media.Image
import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URI


class QueryExecutor: QueryInterface {

    lateinit var cursor: Cursor

    override suspend fun login (context: Context, email: String, password: String) :Cursor {

                var db = Database(context)
                var dbQuery = db.readableDatabase
                cursor = dbQuery.query("users", null, "email = ? AND password = ?",
                    arrayOf(email, password), null, null, null
                )

                return cursor

    }

    override suspend fun checkTwins (context: Context,table: String, where: String, param: Array<String>) :Boolean {
        var db = Database(context)
        var dbQuery = db.readableDatabase
        cursor = dbQuery.query(table,null,where,param,null,null,null)
        if(cursor.moveToFirst()){
            return true
        }
        else return false


    }
    override suspend fun getAll (context: Context,table: String) :Cursor {

        var db = Database(context)
        var dbQuery = db.readableDatabase
        cursor = dbQuery.query(table,null,null,null,null,null,null)
        return cursor
    }

    override suspend fun createDevice (context: Context,type: String,model: String,serial: String,qrcode: String) :Boolean {
        var db = Database(context)
        var dbQuery = db.writableDatabase
        if(checkTwins(context,"devices","serial = ?", arrayOf(serial))){
            return false
        }
        else{
            var values = ContentValues().apply {
                put("type",type)
                put("model",model)
                put("serial",serial)
                put("isTaken","No")

            }
            dbQuery.insert("devices",null,values)
            return true
        }
    }

    override suspend fun createUser (context: Context,email: String, username: String, password: String) :Boolean {

            /*GlobalScope.launch(Dispatchers.IO) {*/
                var db = Database(context)
                var dbQuery = db.writableDatabase
                if(checkTwins(context,"users","email = ? OR username = ?", arrayOf(email,username))){
                    return false
                }
                else {
                    var values = ContentValues().apply {
                        put("email", email)
                        put("username", username)
                        put("password", password)
                        put("rights", 0)
                    }
                    dbQuery.insert("users", null, values)
                    return true
                }


            /*}*/


    }

    override suspend fun modifyEntry(context: Context, table: String, where: String, param: Array<String>,column: String,data: String) {
        var db = Database(context)
        var dbQuery = db.writableDatabase
        var values = ContentValues().apply{
            put(column,data)
        }
        dbQuery.update(table,values,where,param)

    }
    override suspend fun modifyEntryInt(context: Context, table: String, where: String, param: Array<String>,column: String,data: Int) {
        var db = Database(context)
        var dbQuery = db.writableDatabase
        var values = ContentValues().apply{
            put(column,data)
        }
        dbQuery.update(table,values,where,param)

    }


    override suspend fun deleteEntry(context: Context,table: String, where: String, param: Array<String>) {
        var db = Database(context)
        var dbQuery = db.writableDatabase
        dbQuery.delete(table,where,param)

    }


}