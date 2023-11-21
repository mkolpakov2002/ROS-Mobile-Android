package ru.hse.miem.ros.ui.fragments.config

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 05.02.20
 * @updated on 05.02.20
 * @modified by
 */
class CustomLinearLayoutManager(context: Context?) : LinearLayoutManager(context) {
    private var isScrollEnabled: Boolean = false
    fun setScrollEnabled(flag: Boolean) {
        isScrollEnabled = flag
    }

    public override fun canScrollVertically(): Boolean {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically()
    }
}
