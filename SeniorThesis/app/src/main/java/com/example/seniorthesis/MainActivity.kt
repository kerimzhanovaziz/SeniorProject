package com.example.seniorthesis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.things.pio.PeripheralManager

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        onClickEvent()

        val manager = PeripheralManager.getInstance()
        val deviceList: List<String> = manager.uartDeviceList
        if (deviceList.isEmpty()) {
            Log.i("________", "No UART port available on this device.")
        } else {
            Log.i("________", "List of available devices: $deviceList")
        }
    }



    private fun onClickEvent() {

        flightmodeButton.setOnClickListener {
            val intent = Intent(this, WayPointsActivity::class.java)
            startActivity(intent)
        }

        rcontrButton.setOnClickListener{
            val intent = Intent (this, RemoteContrActivity::class.java)
            startActivity(intent)
        }





    }
}
