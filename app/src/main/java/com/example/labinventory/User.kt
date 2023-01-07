package com.example.labinventory

import android.os.Parcelable
import kotlin.properties.Delegates

class User(email: String, username: String, id: Int, right: Int): java.io.Serializable {


    var email = email
    var username = username
    var id = id
    var right = right


}