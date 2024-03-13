package com.example.motoengine.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.motoengine.R

class BluetoothRecycleView : RecyclerView.Adapter<BluetoothRecycleView.BluetoothViewHolder>() {

    class BluetoothViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothViewHolder {
        return BluetoothViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_bluetooth_item, parent, false)
        )
    }

    override fun getItemCount(): Int = 0

    override fun onBindViewHolder(holder: BluetoothViewHolder, position: Int) {

    }
}