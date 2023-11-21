package ru.hse.miem.ros.widgets.button

import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.views.details.PublisherWidgetViewHolder
import ru.hse.miem.ros.utility.Utils
import std_msgs.Bool

/**
 * TODO: Description
 *
 * @author Dragos Circa
 * @version 1.0.0
 * @created on 02.11.2020
 * @updated on 18.11.2020
 * @modified by Tanya Rykova
 * @updated on 20.03.2021
 * @modified by Maxim Kolpakov
 */
class ButtonDetailVH() : PublisherWidgetViewHolder() {
    private lateinit var textText: EditText
    private lateinit var rotationSpinner: Spinner
    private lateinit var rotationAdapter: ArrayAdapter<CharSequence>
    public override fun initView(itemView: View) {
        textText = itemView.findViewById(R.id.btnTextTypeText)
        rotationSpinner = itemView.findViewById(R.id.btnTextRotation)

        // Init spinner
        rotationAdapter = ArrayAdapter.createFromResource(
            itemView.context,
            R.array.button_rotation, android.R.layout.simple_spinner_dropdown_item
        )
        rotationSpinner.adapter = rotationAdapter
    }

    override fun bindEntity(entity: BaseEntity) {
        val buttonEntity: ButtonEntity = entity as ButtonEntity
        textText.setText(buttonEntity.text)
        val degrees: String = Utils.numberToDegrees(
            buttonEntity.rotation
        )
        rotationSpinner.setSelection(rotationAdapter.getPosition(degrees))
    }

    override fun updateEntity(entity: BaseEntity) {
        val buttonEntity: ButtonEntity? = entity as ButtonEntity?
        buttonEntity!!.text = textText.text.toString()
        val degrees: String = rotationSpinner.selectedItem.toString()
        buttonEntity.rotation = Utils.degreesToNumber(degrees)
    }

    public override fun getTopicTypes(): List<String> {
        return listOf(Bool._TYPE)
    }
}
