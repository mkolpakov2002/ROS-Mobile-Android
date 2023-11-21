package ru.hse.miem.ros.widgets.rqtplot

import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import com.google.android.material.textfield.TextInputEditText
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.views.details.SubscriberWidgetViewHolder
import ru.hse.miem.ros.utility.Utils

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 29.05.21
 */
class RqtPlotDetailVH() : SubscriberWidgetViewHolder(), OnEditorActionListener {
    private lateinit var fieldEditText: TextInputEditText
    public override fun initView(itemView: View) {
        fieldEditText = itemView.findViewById(R.id.fieldEditText)
        fieldEditText.setOnEditorActionListener(this)
    }

    override fun bindEntity(entity: BaseEntity) {
        val plotEntity: RqtPlotEntity = entity as RqtPlotEntity
        fieldEditText.setText(plotEntity.fieldPath)
    }

    override fun updateEntity(entity: BaseEntity) {
        val plotEntity: RqtPlotEntity = entity as RqtPlotEntity
        if (fieldEditText.getText() == null) return
        plotEntity.fieldPath = fieldEditText.getText().toString().trim { it <= ' ' }
    }

    public override fun getTopicTypes(): List<String> {
        return ArrayList()
    }

    public override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
        when (actionId) {
            EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NEXT, EditorInfo.IME_ACTION_PREVIOUS -> {
                Utils.hideSoftKeyboard(v)
                v.clearFocus()
                forceWidgetUpdate()
                return true
            }
        }
        return false
    }

    companion object {
        private val TAG: String = RqtPlotDetailVH::class.java.simpleName
    }
}
