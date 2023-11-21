package ru.hse.miem.ros.widgets.switchbutton

import android.view.View
import android.widget.EditText
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.views.details.PublisherWidgetViewHolder
import std_msgs.Bool

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 10.05.2022
 */
class SwitchButtonDetailVH() : PublisherWidgetViewHolder() {
    private lateinit var textText: EditText
    public override fun initView(itemView: View) {
        textText = itemView.findViewById(R.id.switchTextTypeText)
    }

    override fun bindEntity(entity: BaseEntity) {
        val switchEntity: SwitchButtonEntity = entity as SwitchButtonEntity
        textText.setText(switchEntity.text)
    }

    override fun updateEntity(entity: BaseEntity) {
        val switchEntity: SwitchButtonEntity = entity as SwitchButtonEntity
        switchEntity.text = textText.text.toString()
    }

    public override fun getTopicTypes(): List<String> {
        return listOf(Bool._TYPE)
    }
}
