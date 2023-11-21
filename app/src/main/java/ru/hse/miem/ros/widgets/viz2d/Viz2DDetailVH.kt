package ru.hse.miem.ros.widgets.viz2d

import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import com.google.android.material.textfield.TextInputEditText
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.views.details.WidgetGroupViewHolder
import ru.hse.miem.ros.utility.Utils
import ru.hse.miem.ros.viewmodel.DetailsViewModel

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 08.03.21
 */
class Viz2DDetailVH() : WidgetGroupViewHolder(), OnEditorActionListener {
    private lateinit var frameEditText: TextInputEditText
    override var viewModel: DetailsViewModel? = null
    override fun initView(itemView: View) {
        // Initialize Topic Edittext
        frameEditText = itemView.findViewById(R.id.frame_edit_text)
        frameEditText.setOnEditorActionListener(this)
    }

    override fun bindEntity(entity: BaseEntity) {
        val frame: String = (entity as Viz2DEntity?)!!.frame
        frameEditText.setText(frame)
    }

    override fun updateEntity(entity: BaseEntity) {
        val viz2d: Viz2DEntity = entity as Viz2DEntity
        viz2d.frame = frameEditText.getText().toString()
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
}
