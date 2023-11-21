package ru.hse.miem.ros.ui.fragments.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import java.util.Collections

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 13.03.21
 */
class WidgetListAdapter(private val clickListener: WidgetClickListener) :
    RecyclerView.Adapter<WidgetListAdapter.ViewHolder>() {
    private val currentWidgets: MutableList<BaseEntity>
    private val compareByTime: Comparator<BaseEntity>

    init {
        currentWidgets = ArrayList()
        compareByTime = Comparator { o1: BaseEntity, o2: BaseEntity ->
            o2.creationTime.compareTo(o1.creationTime)
        }
    }

    public override fun onCreateViewHolder(viewGroup: ViewGroup, itemType: Int): ViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_widget_card_list, viewGroup, false)
        return ViewHolder(view)
    }

    public override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val widget: BaseEntity = currentWidgets[position]
        holder.setEntity(widget)
        holder.card.setOnClickListener { v: View? ->
            clickListener.onClick(widget)
        }
    }

    public override fun getItemCount(): Int {
        return currentWidgets.size
    }

    fun setWidgets(newWidgets: List<BaseEntity>) {
        currentWidgets.clear()
        currentWidgets.addAll((newWidgets))
        Collections.sort(currentWidgets, compareByTime)
        notifyDataSetChanged()
    }

    fun getItem(index: Int): BaseEntity {
        return currentWidgets[index]
    }

    fun interface WidgetClickListener {
        fun onClick(entity: BaseEntity)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameTextView: TextView
        var typeTextView: TextView
        var viewBackground: View
        var viewForeground: View

        init {
            nameTextView = itemView.findViewById(R.id.widget_name_textview)
            typeTextView = itemView.findViewById(R.id.widget_type_textview)
            viewBackground = itemView.findViewById(R.id.view_background)
            viewForeground = itemView.findViewById(R.id.view_foreground)
        }

        val card: CardView
            get() {
                return itemView as CardView
            }

        fun setEntity(entity: BaseEntity?) {
            val formatText: String = itemView.getResources().getString(R.string.widget_list_type)
            val typeText: String = String.format(formatText, entity!!.type)
            nameTextView.text = entity.name
            typeTextView.text = typeText
        }
    }

    companion object {
        var TAG: String = WidgetDetailListAdapter::class.java.simpleName
    }
}
