package ru.hse.miem.ros.ui.fragments.ssh

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.hse.miem.ros.R
import ru.hse.miem.ros.ui.fragments.ssh.SshRecyclerViewAdapter.SshViewHolder

class SshRecyclerViewAdapter() : RecyclerView.Adapter<SshViewHolder?>() {
    private val dataset: ArrayList<String> = ArrayList()

    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SshViewHolder {
        val v: TextView = LayoutInflater.from(parent.context)
            .inflate(R.layout.ssh_text_view, parent, false) as TextView
        return SshViewHolder(v)
    }

    public override fun onBindViewHolder(holder: SshViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val data: String = dataset[position]
        holder.textView.text = data
    }

    fun addItem(item: String) {
        dataset.add(item)

        // Remove data in circular fashion
        while (dataset.size > 1000) {
            dataset.removeAt(0)
        }
        notifyItemInserted(dataset.size - 1)
    }

    // Return the size of your dataset (invoked by the layout manager)
    public override fun getItemCount(): Int {
        return dataset.size
    }

    /**
     * Provide a reference to the views for each data item.
     * Complex data items may need more than one view per item, and
     * you provide access to all the views for a data item in a view holder.
     */
    class SshViewHolder(// each data item is just a string in this case
        var textView: TextView
    ) : RecyclerView.ViewHolder(textView)
}
