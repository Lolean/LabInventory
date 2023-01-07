package com.example.labinventory

import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_adddevice.*
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.device_pattern.*
import kotlinx.android.synthetic.main.device_pattern.view.*
import kotlinx.android.synthetic.main.user_pattern.*
import kotlinx.android.synthetic.main.user_pattern.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.collections.MutableList

class DeviceListActivity: AppCompatActivity() {

    var dbConnect = QueryExecutor()
    lateinit var loggedUser: User
    private val deviceList = mutableListOf<Device>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        loggedUser = intent.getSerializableExtra("loggedUser") as User
        if(loggedUser.right>0){
            list_bt_addDevice.visibility=View.VISIBLE
        }
        list_tv_title.text="Devices"
        fillDeviceList()

    }

    // classe création de vue device pour la scrollview
    inner class DeviceView(context: Context, device: Device): LinearLayout(context){

        init{

            View.inflate(context,R.layout.device_pattern,this)
            devicepattern_tv_type.text = device.type
            devicepattern_tv_model.text = device.model
            devicepattern_tv_serial.text = device.serial
            devicepattern_tv_state.text = device.taken.toString()
            devicepattern_tv_username.text = device.userID.toString()
            if(loggedUser.right<1)userpattern_bt_delete.visibility=View.GONE

        }
    }

    // tableau des fonctions par boutons
    fun deviceListPress(v: View){
        when(v.id){
            list_bt_addDevice.id -> toDeviceForm()
            adddevice_bt_submit.id -> addDevice(v)
            devicepattern_bt_delete.id -> deleteDevice(v)
        }
    }

    // ouvre le formulaire de création de devicesans quitter l'activité
    fun toDeviceForm(){
        setContentView(R.layout.activity_adddevice)
    }

    // ajout de device depuis le formulaire d'ajout
    fun addDevice(v: View){
        runBlocking {
            launch(Dispatchers.IO) {

                if(adddevice_et_model.text.isNotEmpty() && adddevice_et_serial.text.isNotEmpty()){
                    var model = adddevice_et_model.text.toString()
                    var serial = adddevice_et_serial.text.toString()
                    var type = adddevice_sp_typelist.selectedItem.toString()
                    if(dbConnect.createDevice(this@DeviceListActivity,type,model,serial,"none")){

                        Snackbar.make(v, "appareil créé", Snackbar.LENGTH_SHORT).show()

                    }
                    else Snackbar.make(v, "requête de création échouée", Snackbar.LENGTH_SHORT).show()



                }

            }
        }
    }

    // coroutine pour remplir une liste de device depuis la db
    fun fillDeviceList(){
        runBlocking {
            launch(Dispatchers.IO){
                var cursor: Cursor
                var dbConnect = QueryExecutor()
                cursor = dbConnect.getAll(this@DeviceListActivity,"devices")
                Log.d("cursor","${cursor.count.toString()}")
                if(cursor.moveToFirst()){
                    //façon brute de ne pas sauter le premier device
                    var type = cursor.getString(cursor.getColumnIndexOrThrow("type"))
                    var model = cursor.getString(cursor.getColumnIndexOrThrow("model"))
                    var serial = cursor.getString(cursor.getColumnIndexOrThrow("serial"))
                    var id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
                    var taken = cursor.getString(cursor.getColumnIndexOrThrow("isTaken"))
                    if(taken.toString()=="No") taken = "Available"
                    else taken = "Taken"
                    var userID = cursor.getInt(cursor.getColumnIndexOrThrow("currentUserID"))
                    var device = Device(id,type,model,serial,taken,userID)

                    deviceList.add(device)
                    while(cursor.moveToNext()){
                        var type = cursor.getString(cursor.getColumnIndexOrThrow("type"))
                        var model = cursor.getString(cursor.getColumnIndexOrThrow("model"))
                        var serial = cursor.getString(cursor.getColumnIndexOrThrow("serial"))
                        var id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
                        var taken = cursor.getString(cursor.getColumnIndexOrThrow("isTaken"))
                        if(taken.toString()=="No") taken = "Available"
                        else taken = "Taken"
                        var userID = cursor.getInt(cursor.getColumnIndexOrThrow("currentUserID"))
                        var device = Device(id,type,model,serial,taken,userID)

                        deviceList.add(device)

                    }
                    val view = findViewById<View>(android.R.id.content)
                    Snackbar.make(view,"Liste d'utilisateur récupéré",Snackbar.LENGTH_SHORT).show()
                    //ajout à la vue
                    mapToView()

                }
                else{
                    var builder = AlertDialog.Builder(this@DeviceListActivity)
                    builder.setMessage("La requête à la base de donnée a échoué")
                    builder.setPositiveButton("Réessayer"){_,_ ->
                        fillDeviceList()
                    }
                    builder.setNegativeButton("Revenir au menu"){_, _ ->
                        val intent = Intent(this@DeviceListActivity,MenuActivity::class.java)
                        intent.putExtra("loggedUser",loggedUser)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    // mapping de chaque device de la liste dans la vue dans un pattern dédié
    fun mapToView(){
        var childLinear = LinearLayout(this@DeviceListActivity)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
        childLinear.layoutParams = layoutParams
        childLinear.orientation=LinearLayout.VERTICAL
        for(device in deviceList){
            var devicepattern = DeviceView(this@DeviceListActivity,device)
            childLinear.addView(devicepattern)
        }
        list_sv_entities.addView(childLinear)
    }

    fun deleteDevice(v: View){

        var prombtparent = v.parent as LinearLayout
        var prombtgp = prombtparent.parent as LinearLayout
        var prombttextviews = prombtgp.getChildAt(0) as LinearLayout
        var serial = prombttextviews.getChildAt(2) as TextView
        Log.d("serial",serial.text.toString())
       /* runBlocking {
            launch(Dispatchers.IO){
                dbConnect.deleteEntry(this@DeviceListActivity,"devices","serial = ?", arrayOf(serial.text.toString()))
                restart()
            }
        }*/

    }

    fun restart(){
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntentWithParentStack(intent)
        stackBuilder.startActivities()
    }


}