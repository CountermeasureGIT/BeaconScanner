package ru.countermeasure.ibeaconscanner.viewmodel

import androidx.lifecycle.ViewModel
import ru.countermeasure.ibeaconscanner.model.BeaconsLiveData

class MainViewModel : ViewModel() {
    val beaconsLiveData : BeaconsLiveData =
        BeaconsLiveData()
}