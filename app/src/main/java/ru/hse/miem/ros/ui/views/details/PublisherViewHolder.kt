package ru.hse.miem.ros.ui.views.details

import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import com.google.android.material.textfield.TextInputEditText
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.utility.Utils
import ru.hse.miem.ros.viewmodel.DetailsViewModel

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 17.03.21
 */
class PublisherViewHolder(private val parentViewHolder: DetailViewHolder) : IBaseViewHolder,
    OnEditorActionListener {
    var topicTypes: List<String>? = null
    var viewModel: DetailsViewModel? = null
    private lateinit var topicNameEditText: TextInputEditText
    private lateinit var topicTypeEditText: TextInputEditText
    public override fun baseInitView(view: View) {
        // Initialize Topic Edittext
        topicNameEditText = view.findViewById(R.id.topicNameEditText)
        topicTypeEditText = view.findViewById(R.id.topicTypeEditText)
        topicNameEditText.setOnEditorActionListener(this)
        topicTypeEditText.setOnEditorActionListener(this)
    }

    public override fun baseBindEntity(entity: BaseEntity) {
        val topicName: String = entity.topic.name
        val messageType: String = entity.topic.type
        topicNameEditText.setText(topicName)
        topicTypeEditText.setText(messageType)
    }

    public override fun baseUpdateEntity(entity: BaseEntity) {
        entity.topic.name = topicNameEditText.getText().toString()
        entity.topic.type = topicTypeEditText.getText().toString()
    }

    public override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
        when (actionId) {
            EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NEXT, EditorInfo.IME_ACTION_PREVIOUS -> {
                Utils.hideSoftKeyboard(v)
                v.clearFocus()
                parentViewHolder.forceWidgetUpdate()
                return true
            }
        }
        return false
    }

    companion object {
        val TAG: String = PublisherViewHolder::class.java.simpleName
    }
}
