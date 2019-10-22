package ru.countermeasure.ibeaconscanner.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
            if (isStarted) {
                switchDataObserving(false)
            } else if (!isPermissionsGranted()) {
                startPermissionsRequest()
            } else {
                switchDataObserving(true)
            }
        }
    }

    private fun startPermissionsRequest() {
        return
    }

    private fun isPermissionsGranted(): Boolean {
        return true
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
        Log.d(TAG, "onSaveInstanceState")
        outState.putBoolean(SAVED_STATE_IS_STARTED, isStarted)
    }
}
