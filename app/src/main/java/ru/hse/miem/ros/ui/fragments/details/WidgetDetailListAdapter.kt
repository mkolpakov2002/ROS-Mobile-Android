package ru.hse.miem.ros.ui.fragments.details

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.hse.miem.ros.BuildConfig
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.general.WidgetChangeListener
import ru.hse.miem.ros.ui.views.details.BaseDetailSubscriberVH
import ru.hse.miem.ros.ui.views.details.BaseDetailViewHolder
import ru.hse.miem.ros.utility.Constants
import ru.hse.miem.ros.utility.Utils
import ru.hse.miem.ros.viewmodel.DetailsViewModel
import java.lang.reflect.Constructor
import java.util.Locale

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.4
 * @created on 24.01.20
 * @updated on 15.04.20
 * @modified by Maxim Kolpakov
 * @updated on 25.09.20
 * @modified by Maxim Kolpakov
 * @updated on 10.03.20
 * @modified by Maxim Kolpakov
 */
class WidgetDetailListAdapter(private val mViewModel: DetailsViewModel) :
    RecyclerView.Adapter<BaseDetailViewHolder<BaseEntity>>(), WidgetChangeListener {
    private val mDiffer: AsyncListDiffer<BaseEntity>
    private val types: ArrayList<Class<out BaseDetailViewHolder<BaseEntity>>>
    private val diffCallback: DiffUtil.ItemCallback<BaseEntity> =
        object : DiffUtil.ItemCallback<BaseEntity>() {
            public override fun areItemsTheSame(oldItem: BaseEntity, newItem: BaseEntity): Boolean {
                return oldItem.id == newItem.id
            }

            public override fun areContentsTheSame(
                oldItem: BaseEntity,
                newItem: BaseEntity
            ): Boolean {
                return (oldItem == newItem)
            }
        }
    private var widgetChangeListener: WidgetChangeListener? = null

    init {
        mDiffer = AsyncListDiffer(this, diffCallback)
        types = ArrayList()
    }

    public override fun onCreateViewHolder(
        parent: ViewGroup,
        itemType: Int
    ): BaseDetailViewHolder<BaseEntity> {
        val viewHolderClazz: Class<out BaseDetailViewHolder<BaseEntity>> = types[itemType]
        val cons: Constructor<out BaseDetailViewHolder<BaseEntity>> =
            viewHolderClazz.getConstructor(
                View::class.java, WidgetChangeListener::class.java
            )
        val inflator: LayoutInflater = LayoutInflater.from(parent.context)
        val itemView: View = inflator.inflate(R.layout.widget_detail_base, parent, false)
        val viewHolder: BaseDetailViewHolder<BaseEntity> = cons.newInstance(itemView, this)
        viewHolder.setViewModel(mViewModel)
        return viewHolder
    }

    public override fun onBindViewHolder(holder: BaseDetailViewHolder<BaseEntity>, position: Int) {
        val widget: BaseEntity = getItem(position)

        // Get layout id
        val layoutStr: String = String.format(
            Constants.DETAIL_LAYOUT_FORMAT, widget.type!!.lowercase(
                Locale.getDefault()
            )
        )
        val detailContentLayout: Int = Utils.getResId(layoutStr, R.layout::class.java)

        // Inflate layout
        val inflator: LayoutInflater = LayoutInflater.from(holder.itemView.context)
        val inflatedView: View = inflator.inflate(detailContentLayout, null)
        holder.detailContend.removeView(holder.detailContend.getChildAt(1))
        holder.detailContend.addView(inflatedView)

        // Bind to widget
        holder.init()
        holder.bind(widget.copy())
    }

    public override fun getItemViewType(position: Int): Int {
        val entity: BaseEntity = getItem(position)
        val classPath: String = (BuildConfig.APPLICATION_ID
                + String.format(
            Constants.VIEWHOLDER_FORMAT,
            entity.type!!.lowercase(Locale.getDefault()),
            entity.type
        ))
        return try {
            val clazzObject: Class<*> = Class.forName(classPath)
            if ((clazzObject.superclass != BaseDetailViewHolder::class.java
                        && clazzObject.superclass != BaseDetailSubscriberVH::class.java)
            ) {
                -1
            } else if (types.contains(clazzObject)) {
                types.indexOf(clazzObject)
            } else {
                types.add(clazzObject as Class<out BaseDetailViewHolder<BaseEntity>>)
                types.size - 1
            }
        } catch (e: ClassNotFoundException) {
            -1
        }
    }

    public override fun getItemCount(): Int {
        return mDiffer.currentList.size
    }

    public override fun onWidgetDetailsChanged(widgetEntity: BaseEntity) {
        widgetChangeListener?.onWidgetDetailsChanged(widgetEntity)
    }

    private fun getItem(position: Int): BaseEntity {
        return mDiffer.currentList[position]
    }

    fun setWidgets(newWidgets: List<BaseEntity>) {
        for (entity: BaseEntity in newWidgets) {
            Log.i(TAG, "New Widget: $entity")
        }
        mDiffer.submitList(newWidgets)
    }

    fun setChangeListener(widgetChangeListener: WidgetChangeListener?) {
        this.widgetChangeListener = widgetChangeListener
    }

    companion object {
        var TAG: String = WidgetDetailListAdapter::class.java.simpleName
    }
}