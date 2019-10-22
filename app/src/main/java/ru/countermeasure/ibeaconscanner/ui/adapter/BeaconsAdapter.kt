package ru.countermeasure.ibeaconscanner.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.beacon_list_item.view.*
import ru.countermeasure.ibeaconscanner.model.BeaconData
import ru.countermeasure.ibeaconscanner.R


class BeaconsAdapter : ListAdapter<BeaconData, BeaconsAdapter.ViewHolder>(
    DiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.beacon_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(beaconData: BeaconData) {
            itemView.apply {
                bt_address.text = beaconData.bt_address
                rssi.text = beaconData.rssi.toString()
                tx_power.text = beaconData.tx_power
                uuid.text = beaconData.uuid
                major.text = beaconData.major
                minor.text = beaconData.minor
            }
        }
    }
}

private class DiffCallback : DiffUtil.ItemCallback<BeaconData>() {

    override fun areItemsTheSame(oldItem: BeaconData, newItem: BeaconData): Boolean {
        return oldItem.uuid == newItem.uuid
    }

    override fun areContentsTheSame(oldItem: BeaconData, newItem: BeaconData): Boolean {
        return oldItem.rssi == newItem.rssi
    }
}