package ru.countermeasure.ibeaconscanner.model

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.RemoteException
import org.altbeacon.beacon.*

class BeaconsDataSource(private val context: Context) : BeaconConsumer, RangeNotifier {
    private val beaconManager: BeaconManager =
        BeaconManager.getInstanceForApplication(context)
    private val region = Region("my-region", null, null, null)
    private var data: List<BeaconData> = emptyList()

    private var listener: ((List<BeaconData>) -> Unit)? = null

    override fun getApplicationContext(): Context = context

    override fun unbindService(p0: ServiceConnection?) {
        applicationContext.unbindService(p0!!)
    }

    override fun bindService(p0: Intent?, p1: ServiceConnection?, p2: Int): Boolean {
        return applicationContext.bindService(p0, p1!!, p2)
    }

    override fun onBeaconServiceConnect() {
        beaconManager.removeAllRangeNotifiers()
        beaconManager.addRangeNotifier(this)

        try {
            beaconManager.startRangingBeaconsInRegion(region)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    override fun didRangeBeaconsInRegion(p0: MutableCollection<Beacon>?, p1: Region?) {
        val beacons = p0?.map {
            BeaconData(
                bt_address = it.bluetoothAddress,
                rssi = it.rssi,
                tx_power = it.txPower.toString(),
                uuid = it.identifiers[0].toString(),
                major = it.identifiers[1].toString(),
                minor = it.identifiers[2].toString()
            )
        } ?: emptyList()
        data = beacons

        notifyListeners()
    }

    private fun notifyListeners() {
        listener?.invoke(data)
    }

    fun requestBeaconsUpdate(listener: (List<BeaconData>) -> Unit) {
        this.listener = listener
        beaconManager.bind(this)
    }

    fun stopBeaconsUpdate() {
        beaconManager.unbind(this)
    }
}