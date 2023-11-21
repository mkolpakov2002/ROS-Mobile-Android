package ru.hse.miem.ros.ui.views.details

import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import ru.hse.miem.ros.utility.Utils
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.data.model.entities.widgets.IPositionEntity
import ru.hse.miem.ros.ui.general.Position

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 17.03.21
 */
class WidgetViewHolder(private val parentViewHolder: DetailViewHolder) : IBaseViewHolder,
    OnEditorActionListener {
    private lateinit var xEdittext: EditText
    private lateinit var yEdittext: EditText
    private lateinit var widthEdittext: EditText
    private lateinit var heightEdittext: EditText
    private lateinit var nameEdittext: EditText
    public override fun baseInitView(view: View) {
        xEdittext = view.findViewById(R.id.x_edit_text)
        yEdittext = view.findViewById(R.id.y_edit_text)
        widthEdittext = view.findViewById(R.id.width_edit_text)
        heightEdittext = view.findViewById(R.id.height_edit_text)
        nameEdittext = view.findViewById(R.id.name_edit_text)
        xEdittext.setOnEditorActionListener(this)
        yEdittext.setOnEditorActionListener(this)
        widthEdittext.setOnEditorActionListener(this)
        heightEdittext.setOnEditorActionListener(this)
        nameEdittext.setOnEditorActionListener(this)
    }

    public override fun baseBindEntity(entity: BaseEntity) {
        nameEdittext.setText(entity.name)
        val position: Position = (entity as IPositionEntity).getPosition()
        xEdittext.setText(position.x.toString())
        yEdittext.setText(position.y.toString())
        widthEdittext.setText(position.width.toString())
        heightEdittext.setText(position.height.toString())
    }

    public override fun baseUpdateEntity(entity: BaseEntity) {
        entity.name = nameEdittext.text.toString()
        val posEntity: IPositionEntity = entity as IPositionEntity
        val position = Position()
        if (xEdittext.text.isEmpty()) {
            position.x = posEntity.getPosition().x
        } else {
            position.x = xEdittext.text.toString().toInt()
        }
        if (yEdittext.text.isEmpty()) {
            position.y = posEntity.getPosition().y
        } else {
            position.y = yEdittext.text.toString().toInt()
        }
        if (widthEdittext.text.isEmpty()) {
            position.width = posEntity.getPosition().width
        } else {
            position.width = widthEdittext.text.toString().toInt()
        }
        if (heightEdittext.text.isEmpty()) {
            position.height = posEntity.getPosition().height
        } else {
            position.height = heightEdittext.text.toString().toInt()
        }
        posEntity.setPosition(position)
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
}
