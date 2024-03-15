package com.example.motoengine.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.motoengine.R
import com.example.motoengine.model.Bluetooth

class BluetoothListAdapter(
    private var mBluetoothList: List<Bluetooth>,
    private var callback: (Bluetooth) -> Unit
) : RecyclerView.Adapter<BluetoothListAdapter.BluetoothViewHolder>() {

    class BluetoothViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle = itemView as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothViewHolder {
        return BluetoothViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_bluetooth_item, parent, false)
        )
    }

    override fun getItemCount(): Int = mBluetoothList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BluetoothViewHolder, position: Int) {
        holder.tvTitle.text =
            mBluetoothList[position].name + "\n" + mBluetoothList[position].address

        holder.itemView.setOnClickListener {
            callback.invoke(mBluetoothList[position])
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(data: List<Bluetooth>) {
        this.mBluetoothList = data
        notifyDataSetChanged()
    }
}