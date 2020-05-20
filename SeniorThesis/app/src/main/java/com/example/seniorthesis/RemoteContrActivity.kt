package com.example.seniorthesis

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_remote_contr.*


class RemoteContrActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remote_contr)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

      //  leftjoystick.setOnMoveListener { angle, strength ->
      //  }

      //  rightjoystick.setOnMoveListener {angle, strength ->

            /*textView_angle_right.setText(getString(angle)+ "°")
            textView_strength_right.setText(getString(strength)+ "°")
            textView_coordinate_right.setText(
                String.format(
                    "x%03d:y%03d",
                    rightjoystick.getNormalizedX(),
                    leftjoystick.getNormalizedY()
                )
            )*/
       // }

    }
}
