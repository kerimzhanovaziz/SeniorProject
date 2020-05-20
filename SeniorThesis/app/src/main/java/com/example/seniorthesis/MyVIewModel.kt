package com.example.seniorthesis

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

class MyVIewModel : ViewModel() {

    val currentPositionLiveData: MutableLiveData<LatLng> = MutableLiveData<LatLng>()

        //val curpos: LiveData<LatLng> = currentPositionLiveData

}