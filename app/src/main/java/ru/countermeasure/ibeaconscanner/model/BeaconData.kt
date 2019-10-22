package ru.countermeasure.ibeaconscanner.model

data class BeaconData(
    val bt_address: String,
    val rssi: Int,
    val tx_power: String,
    val uuid: String,
    val major: String,
    val minor: String
)