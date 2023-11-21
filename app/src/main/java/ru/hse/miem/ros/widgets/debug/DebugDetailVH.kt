package ru.hse.miem.ros.widgets.debug

import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.views.details.SubscriberWidgetViewHolder
import ru.hse.miem.ros.utility.Utils

/**
 * TODO: Description
 *
 * @author Tanya Rykova
 * @version 1.1.0
 * @created on 17.08.20
 * @updated on 17.09.20
 * @modified by Tanya Rykova
 * @updated on 17.03.21
 * @modified by Maxim Kolpakov
 */
class DebugDetailVH() : SubscriberWidgetViewHolder(), OnEditorActionListener {
    private lateinit var messageNumberEdittext: EditText
    override fun initView(itemView: View) {
        messageNumberEdittext = itemView.findViewById(R.id.messageNumberEdittext)
        messageNumberEdittext.setOnEditorActionListener(this)
    }

    override fun bindEntity(entity: BaseEntity) {
        val entity: DebugEntity = entity as DebugEntity
        messageNumberEdittext.setText(entity.numberMessages.toString())
    }

    override fun updateEntity(entity: BaseEntity) {
        val entity: DebugEntity = entity as DebugEntity
        entity.numberMessages = messageNumberEdittext.text.toString().toInt()
    }

    public override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
        when (actionId) {
            EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NEXT, EditorInfo.IME_ACTION_PREVIOUS -> {
                itemView?.let{
                    Utils.hideSoftKeyboard(it)
                    it.requestFocus()
                }
                return true
            }
        }
        return false
    }

    public override fun getTopicTypes(): List<String> {
        return ArrayList()
    }
}