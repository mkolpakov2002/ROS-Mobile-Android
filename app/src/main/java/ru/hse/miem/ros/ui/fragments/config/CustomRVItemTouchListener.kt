package ru.hse.miem.ros.ui.fragments.config

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 05.02.20
 * @updated on 05.02.20
 * @modified by
 */
class CustomRVItemTouchListener(
    context: Context?, recyclerView: RecyclerView?,
    private val clickListener: RecyclerViewItemClickListener?
) : RecyclerView.OnItemTouchListener {
    private val gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
            public override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }

            public override fun onLongPress(e: MotionEvent) {
                /*
                //find the long pressed view
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());

                if (child != null && clickListener != null) {
                    clickListener.onLongClick(child, recyclerView.getChildLayoutPosition(child));
                }
                */
            }
        })
    }

    public override fun onInterceptTouchEvent(
        recyclerView: RecyclerView,
        event: MotionEvent
    ): Boolean {
        val child: View? = recyclerView.findChildViewUnder(event.getX(), event.getY())
        if ((child != null) && (clickListener != null) && gestureDetector.onTouchEvent(event)) {
            clickListener.onClick(recyclerView, child, recyclerView.getChildLayoutPosition(child))
        }
        return false
    }

    public override fun onTouchEvent(recyclerView: RecyclerView, event: MotionEvent) {}
    public override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
}
