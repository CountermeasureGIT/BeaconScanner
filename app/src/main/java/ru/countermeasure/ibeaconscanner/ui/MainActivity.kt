package ru.countermeasure.ibeaconscanner.ui

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import ru.countermeasure.ibeaconscanner.model.BeaconData
import ru.countermeasure.ibeaconscanner.ui.adapter.BeaconsAdapter
import ru.countermeasure.ibeaconscanner.viewmodel.MainViewModel
import ru.countermeasure.ibeaconscanner.R

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private var isStarted = false

    companion object {
        const val TAG = "MAIN_ACTIVITY"
        const val SAVED_STATE_IS_STARTED = "SAVED_STATE_IS_STARTED"
        const val PERMISSION_REQUEST_FINE_LOCATION = 1
        const val REQUEST_ENABLE_BT = 111
    }

    private lateinit var viewAdapter: BeaconsAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val onBeaconDataChangedCallback: (List<BeaconData>) -> Unit = {
        viewAdapter.submitList(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState?.let {
            isStarted = it.getBoolean(SAVED_STATE_IS_STARTED, false)
        }

        initViews()
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        switchDataObserving(isStarted)
    }

    private fun initViews() {
        viewAdapter = BeaconsAdapter()
        viewManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        recyclerView.apply {
            adapter = viewAdapter
            layoutManager = viewManager
        }

        start_stop_button.setOnClickListener {
            startStopAction()
        }
    }

    private fun startStopAction() {
        if (isStarted) {
            switchDataObserving(false)
        } else if (checkStartRequirements()) {
            switchDataObserving(true)
        }
    }

    private fun checkStartRequirements(): Boolean {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null) {
            Toast.makeText(this, getString(R.string.no_bluetooth_support), Toast.LENGTH_SHORT)
                .show()
            return false
        }
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            return false
        }
        if (!isPermissionsGranted()) {
            startPermissionsRequest()
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            if(resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, getString(R.string.bluetooth_disabled), Toast.LENGTH_SHORT)
                    .show()
            } else {
                startStopAction()
            }
        }
    }

    private fun startPermissionsRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_FINE_LOCATION
        )
    }

    private fun isPermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startStopAction()
                } else {
                    Toast.makeText(this, getString(R.string.permission_rejected), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun switchDataObserving(shouldObserve: Boolean) {
        isStarted = shouldObserve
        if (shouldObserve) {
            start_stop_button.text = resources.getString(R.string.stop)
            viewModel.beaconsLiveData.observe(this, onBeaconDataChangedCallback)
        } else {
            start_stop_button.text = resources.getString(R.string.start)
            viewModel.beaconsLiveData.removeObservers(this)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVED_STATE_IS_STARTED, isStarted)
    }
}
