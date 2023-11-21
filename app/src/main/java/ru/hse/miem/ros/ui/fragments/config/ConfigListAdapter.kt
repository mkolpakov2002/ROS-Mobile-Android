package ru.hse.miem.ros.ui.fragments.config

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.hse.miem.ros.R
import ru.hse.miem.ros.ui.fragments.config.ConfigListAdapter.MyViewHolder

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.2
 * @created on 24.01.20
 * @updated on 05.02.20
 * @modified by
 */
class ConfigListAdapter() : RecyclerView.Adapter<MyViewHolder>() {
    var configNameList: MutableList<String> = ArrayList()

    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.config_chooser_item, parent, false)
        return MyViewHolder(itemView)
    }

    public override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val name: String = configNameList[position]
        holder.name.text = name
    }

    public override fun getItemCount(): Int {
        return configNameList.size
    }

    fun setConfigs(newConfigs: List<String>) {
        configNameList.clear()
        configNameList.addAll((newConfigs))
        notifyDataSetChanged()
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView

        init {
            name = view.findViewById(R.id.config_name_textview)
        }
    }
}