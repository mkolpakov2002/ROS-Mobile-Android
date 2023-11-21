package ru.hse.miem.ros.widgets.label

import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.views.details.SilentWidgetViewHolder
import ru.hse.miem.ros.utility.Utils
import ru.hse.miem.ros.viewmodel.DetailsViewModel

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
class LabelDetailVH() : SilentWidgetViewHolder() {
    override var viewModel: DetailsViewModel? = null
    private lateinit var labelTextText: EditText
    private lateinit var labelTextRotationSpinner: Spinner
    private lateinit var rotationAdapter: ArrayAdapter<CharSequence>
    public override fun initView(itemView: View) {
        labelTextText = itemView.findViewById(R.id.labelText)
        labelTextRotationSpinner = itemView.findViewById(R.id.labelTextRotation)

        // Init spinner
        rotationAdapter = ArrayAdapter.createFromResource(
            itemView.context,
            R.array.button_rotation, android.R.layout.simple_spinner_dropdown_item
        )
        labelTextRotationSpinner.adapter = rotationAdapter
    }

    override fun bindEntity(entity: BaseEntity) {
        val labelEntity: LabelEntity = entity as LabelEntity
        val position: Int = rotationAdapter.getPosition(
            Utils.numberToDegrees(
                labelEntity.rotation
            )
        )
        labelTextText.setText(labelEntity.text)
        labelTextRotationSpinner.setSelection(position)
    }

    override fun updateEntity(entity: BaseEntity) {
        val rotation: Int = Utils.degreesToNumber(
            labelTextRotationSpinner.selectedItem.toString()
        )
        (entity as LabelEntity).text = labelTextText.text.toString()
        entity.rotation = rotation
    }
}
