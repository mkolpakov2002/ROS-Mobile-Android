package ru.hse.miem.ros.ui.views.details

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.SubscriberWidgetEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.Topic
import ru.hse.miem.ros.ui.general.WidgetChangeListener

/**
 * TODO: Description
 *
 * @author Tanya Rykova
 * @version 1.0.0
 * @created on 17.09.20
 * @updated on 05.11.2020
 * @modified by Maxim Kolpakov
 */
abstract class BaseDetailSubscriberVH<T : SubscriberWidgetEntity>(
    view: View,
    updateListener: WidgetChangeListener
) : BaseDetailViewHolder<T>(view, updateListener) {
    private lateinit var topicNameTextView: AutoCompleteTextView
    private lateinit var topicTypeEditText: TextInputEditText
    private lateinit var topicNameInputLayout: TextInputLayout
    private lateinit var availableTopics: List<Topic?>
    private lateinit var topicNameItemList: MutableList<String>
    private lateinit var topicNameAdapter: ArrayAdapter<String>
    abstract val topicTypes: List<String>
    override fun baseInitView(parentView: View) {
        super.baseInitView(parentView)
        Log.i(TAG, "init")

        // Initialize Topic Edittext
        topicTypeEditText = parentView.findViewById(R.id.topicTypeEditText)
        topicNameInputLayout = parentView.findViewById(R.id.topicNameLayout)

        // Initialize Topic Name Spinner
        topicNameItemList = ArrayList()
        topicNameTextView = parentView.findViewById(R.id.topicNameTextView)
        topicNameAdapter = ArrayAdapter(
            parentView.context,
            R.layout.dropdown_menu_popup_item, topicNameItemList
        )
        topicNameTextView.setAdapter(topicNameAdapter)
        topicNameTextView.setOnClickListener {
            updateTopicNameSpinner()
            topicNameTextView.showDropDown()
        }
        topicNameInputLayout.setEndIconOnClickListener { v: View? ->
            topicNameTextView.requestFocus()
            topicNameTextView.callOnClick()
        }
        topicNameTextView.setOnItemClickListener {
            parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            selectNameItem(
                position
            )
        }
    }

    override fun baseBindEntity(entity: T) {
        super.baseBindEntity(entity)
        val topicName: String = entity!!.topic.name
        val messageType: String = entity.topic.type
        topicNameTextView.setText(topicName, false)
        topicTypeEditText.setText(messageType)
    }

    override fun baseUpdateEntity() {
        super.baseUpdateEntity()
        entity!!.topic.name = topicNameTextView.text.toString()
        entity!!.topic.type = topicTypeEditText.getText().toString()
    }

    private fun selectNameItem(position: Int) {
        val selectedName: String = topicNameItemList[position]

        // Search for topic type required for selected name
        for (rosTopic: Topic? in availableTopics) {
            if ((rosTopic!!.name == selectedName)) {
                topicTypeEditText.setText(rosTopic.type)
            }
        }
        itemView.requestFocus()
    }

    private fun updateTopicNameSpinner() {
        // Get the list with all suitable topics
        topicNameItemList = ArrayList()
        availableTopics = mViewModel.topicList
        for (rosTopic: Topic? in availableTopics) {
            if (topicTypes.isEmpty()) {
                topicNameItemList.add(rosTopic!!.name)
            }
            for (topicType: String in topicTypes) {
                if ((rosTopic!!.type == topicType)) {
                    topicNameItemList.add(rosTopic.name)
                    break
                }
            }
        }

        // Ros has no topics -> Default name
        if (topicNameItemList.isEmpty()) {
            entity?.let { topicNameItemList.add(it.topic.name) }
        } else {
            topicNameItemList.sort()
        }
        topicNameAdapter.clear()
        topicNameAdapter.addAll(topicNameItemList)
    }

    companion object {
        val TAG: String = BaseDetailViewHolder::class.java.simpleName
    }
}
