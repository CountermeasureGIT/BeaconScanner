package ru.countermeasure.ibeaconscanner.model

import android.util.Log
import androidx.lifecycle.LiveData
import ru.countermeasure.ibeaconscanner.App

class BeaconsLiveData : LiveData<List<BeaconData>>() {
    private val beaconDataSource = BeaconsDataSource(App.applicationContext())
    private val listener = { data: List<BeaconData> ->
        value = data
    }

    override fun onActive() {
        super.onActive()
        beaconDataSource.requestBeaconsUpdate(listener)
    }

    override fun onInactive() {
        super.onInactive()
        beaconDataSource.stopBeaconsUpdate()
    }
}