package ru.hse.miem.ros.ui.fragments.config

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
interface RecyclerViewItemClickListener {
    fun onClick(
        parent: RecyclerView,
        view: View,
        position: Int
    ) //void onLongClick(View view, int position);
}
