package com.example.labinventory

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Database (context: Context) : SQLiteOpenHelper(context,"LabInvDB",null,1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE users(_id INTEGER PRIMARY KEY AUTOINCREMENT,email TEXT UNIQUE,username TEXT UNIQUE, password TEXT NOT NULL, rights INT NOT NULL)")
        db?.execSQL("CREATE TABLE devices(_id INTEGER PRIMARY KEY AUTOINCREMENT,type TEXT NOT NULL ,model TEXT UNIQUE,serial TEXT UNIQUE,isTaken TEXT NOT NULL,currentUserID INT, FOREIGN KEY (currentUserID) REFERENCES users(_id) )")
        db?.execSQL("INSERT INTO users ('email','username','password','rights') VALUES ('root@root.com','root','48690',2) ")
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS login")
        onCreate(db)
    }
}