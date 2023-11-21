package ru.hse.miem.ros.ui.fragments.details

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.hse.miem.ros.ui.views.details.BaseDetailViewHolder

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 24.01.20
 * @updated on 24.01.20
 * @modified by
 */
class RecyclerItemTouchHelper(dragDirs: Int, swipeDirs: Int, private val listener: TouchListener) :
    ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {
    public override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    public override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (viewHolder != null) {
            val foregroundView: View? = (viewHolder as BaseDetailViewHolder<*>).viewForeground
            getDefaultUIUtil().onSelected(foregroundView)
        }
    }

    public override fun onChildDrawOver(
        canvas: Canvas, recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        val foregroundView: View? = (viewHolder as BaseDetailViewHolder<*>).viewForeground
        getDefaultUIUtil().onDrawOver(
            canvas, recyclerView, foregroundView, dX, dY,
            actionState, isCurrentlyActive
        )
    }

    public override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        val foregroundView: View? = (viewHolder as BaseDetailViewHolder<*>).viewForeground
        getDefaultUIUtil().clearView(foregroundView)
    }

    public override fun onChildDraw(
        canvas: Canvas, recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        val foregroundView: View = (viewHolder as BaseDetailViewHolder<*>).viewForeground
        getDefaultUIUtil().onDraw(
            canvas, recyclerView, foregroundView, dX, dY,
            actionState, isCurrentlyActive
        )
    }

    public override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.onSwiped(viewHolder, direction, viewHolder.bindingAdapterPosition)
    }

    public override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    interface TouchListener {
        fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int)
    }
}
