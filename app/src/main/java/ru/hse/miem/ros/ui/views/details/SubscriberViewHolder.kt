package ru.hse.miem.ros.ui.views.details

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.Topic
import ru.hse.miem.ros.viewmodel.DetailsViewModel

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 17.03.21
 */
internal class SubscriberViewHolder(private val parentViewHolder: DetailViewHolder) :
    IBaseViewHolder {
    lateinit var topicTypes: List<String>
    var viewModel: DetailsViewModel?= null
    private lateinit var topicNameTextView: AutoCompleteTextView
    private lateinit var topicTypeEditText: TextInputEditText
    private lateinit var topicNameInputLayout: TextInputLayout
    private lateinit var topicNameAdapter: ArrayAdapter<String>
    private lateinit var availableTopics: List<Topic?>
    private lateinit var topicNameItemList: MutableList<String>
    private lateinit var entity: BaseEntity
    public override fun baseInitView(view: View) {
        // Initialize Topic Edittext
        topicTypeEditText = view.findViewById(R.id.topicTypeEditText)
        topicNameTextView = view.findViewById(R.id.topicNameTextView)
        topicNameInputLayout = view.findViewById(R.id.topicNameLayout)

        // Initialize Topic Name Spinner
        topicNameItemList = ArrayList()
        topicNameAdapter = ArrayAdapter(
            view.context,
            R.layout.dropdown_menu_popup_item, topicNameItemList
        )


        /*
        topicNameAdapter = new ArrayAdapter<String>(view.getContext(),
                R.layout.dropdown_menu_popup_item, topicNameItemList) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                v.post(() -> ((TextView) v).setSingleLine(false));
                return v;
            }
        };
        */
        topicNameTextView.setAdapter(topicNameAdapter)
        topicNameTextView.setOnClickListener { clickedView: View? ->
            updateTopicNameSpinner()
            topicNameTextView.showDropDown()
        }
        topicNameInputLayout.setEndIconOnClickListener { v: View? ->
            topicNameTextView.requestFocus()
            topicNameTextView.callOnClick()
        }
        topicNameTextView.setOnItemClickListener { parent: AdapterView<*>?, v: View?, position: Int, id: Long ->
            selectNameItem(
                position
            )
        }
    }

    public override fun baseBindEntity(entity: BaseEntity) {
        this.entity = entity
        val topicName: String = entity.topic.name
        val messageType: String = entity.topic.type
        topicNameTextView.setText(topicName, false)
        topicTypeEditText.setText(messageType)
    }

    public override fun baseUpdateEntity(entity: BaseEntity) {
        entity.topic.name = topicNameTextView.text.toString()
        entity.topic.type = topicTypeEditText.getText().toString()
    }

    private fun selectNameItem(position: Int) {
        val selectedName: String = topicNameItemList[position]

        // Search for topic type required for selected name
        for (rosTopic: Topic? in availableTopics) {
            if ((rosTopic!!.name == selectedName)) {
                topicTypeEditText.setText(rosTopic.type)
            }
        }
        topicTypeEditText.clearFocus()
        parentViewHolder.forceWidgetUpdate()
    }

    private fun updateTopicNameSpinner() {
        // Get the list with all suitable topics
        topicNameItemList = ArrayList()
        viewModel?.let{
            availableTopics = it.topicList
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
        }

        // Ros has no topics -> Default name
        if (topicNameItemList.isEmpty()) {
            topicNameItemList.add(entity.topic.name)
        } else {
            topicNameItemList.sort()
        }
        topicNameAdapter.clear()
        topicNameAdapter.addAll(topicNameItemList)
    }

    companion object {
        private val TAG: String = SubscriberViewHolder::class.java.simpleName
    }
}
