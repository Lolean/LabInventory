package com.example.labinventory

import android.media.Image

class Device(id: Int,type: String,model: String,serial: String,taken: String,userID: Int): java.io.Serializable {

    var type = type
    var model = model
    var serial = serial
    var taken = taken
    var userID = userID


}