package com.example.seniorthesis

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import io.mavsdk.System
import io.mavsdk.mavsdkserver.MavsdkServer
import io.mavsdk.mission.Mission
import io.mavsdk.telemetry.Telemetry
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_waypoints.*


@Suppress("SENSELESS_COMPARISON")
class WayPointsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private var routeOptions: PolylineOptions? = null
    val BACKEND_IP_ADDRESS = "192.168.0.102"
    private lateinit var drone: System
    var missionItems: MutableList<Mission.MissionItem> = ArrayList()
    private val MISSION_HEIGHT = 5.0f
    private val MISSION_SPEED = 1.0f
    private lateinit var location: LatLng
    private var polyline: Polyline? = null
    private lateinit var mDrone: Marker
    val model: MyVIewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waypoints)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onResume() {
        super.onResume()
        Thread(Runnable {
            val mavsdkServer = MavsdkServer()
            mavsdkServer.run("udp://:14540", 50020)
        }).start()

        drone = System(BACKEND_IP_ADDRESS, 50020)


        Thread(Runnable {
            drone.telemetry.position
                .subscribe { position: Telemetry.Position ->
                    location =
                        LatLng(position.latitudeDeg, position.longitudeDeg)
                    model.currentPositionLiveData.postValue(location)
                }
        }
        ).start()

        takeoffButton.setOnClickListener {
            if (::mMap.isInitialized) {
                drone!!.action.arm().andThen(drone!!.action.takeoff()).subscribe()
            }
        }
        clearPoints_button.setOnClickListener {
            if (::mMap.isInitialized) {
                missionItems.clear()
                mMap.clear()
                routeOptions = null
            }
        }
        landButton.setOnClickListener {
            if (::mMap.isInitialized) {
                drone.action.land().subscribe()
            }
        }
    }

    @SuppressLint("CheckResult")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener { latLng ->
            listenOnMapClick(latLng)
        }

        val cityBishkek = LatLngBounds(
            LatLng(42.78, 74.44), LatLng(42.95, 74.70)
        )

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cityBishkek.center, 10f))


        model.currentPositionLiveData.observe(this, Observer {

            if(::mDrone.isInitialized){
                if(mDrone != null)
                mDrone.remove()
            }
            mDrone = mMap.addMarker(MarkerOptions().position(it))
            Log.e("_________null", "$it")
        })


        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {
            mMap.isMyLocationEnabled = true
        }
        startMission_button.setOnClickListener {
            if (::mMap.isInitialized) {
                drone.mission
                    .setReturnToLaunchAfterMission(true)
                    .andThen(drone.mission.uploadMission(missionItems))
                    .andThen(
                        drone.action.arm()
                            .onErrorComplete()
                    )
                    .andThen(
                        drone.mission.startMission()
                    )
                    .subscribe(
                        {}
                    ) { throwable: Throwable? -> }
            }
        }
    }

    private fun generateMissionItem(latLng: LatLng): Mission.MissionItem {
        return Mission.MissionItem(
            latLng.latitude,
            latLng.longitude, MISSION_HEIGHT,
            MISSION_SPEED,
            true, Float.NaN, Float.NaN,
            Mission.MissionItem.CameraAction.NONE, Float.NaN,
            1.0
        )
    }

    private fun listenOnMapClick(latLng: LatLng) {
        mMap.addMarker(MarkerOptions().position(latLng))
        if(routeOptions == null) {
            routeOptions = PolylineOptions()
        }
        routeOptions?.add(latLng)
        polyline = mMap.addPolyline(routeOptions!!.width(20F).color(Color.rgb(31,48,88)))
        missionItems.add(generateMissionItem(latLng))

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.isMyLocationEnabled = true
            }
        }
    }
}
