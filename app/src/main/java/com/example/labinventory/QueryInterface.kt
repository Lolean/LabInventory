package com.example.labinventory

import android.content.AsyncQueryHandler
import android.content.Context
import android.database.Cursor
import android.media.Image
import android.os.AsyncTask
import java.net.URI

interface QueryInterface {


    suspend fun login (context: Context,email: String, password: String) :Cursor
    suspend fun checkTwins (context: Context, table: String, where: String, param: Array<String>) :Boolean
    suspend fun getAll (context: Context,table: String) :Cursor
    suspend fun modifyEntry (context: Context,table: String,where: String, param: Array<String>,column: String,data: String)
    suspend fun modifyEntryInt(context: Context, table: String, where: String, param: Array<String>,column: String,data: Int)
    suspend fun createUser (context: Context,email: String, username: String, password: String) :Boolean
    suspend fun createDevice (context: Context,type: String, model: String, serial: String,qrcode: String) :Boolean
    suspend fun deleteEntry(context: Context,table: String,where: String,param: Array<String>)
    /*fun modifyEntry(table: String, where: String, param: Array<String>, )*/

}