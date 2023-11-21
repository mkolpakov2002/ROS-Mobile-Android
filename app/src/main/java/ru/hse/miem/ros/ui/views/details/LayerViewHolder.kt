package ru.hse.miem.ros.ui.views.details

import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import ru.hse.miem.ros.utility.Utils
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity


/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 17.03.21
 */
internal class LayerViewHolder(private val parentViewHolder: DetailViewHolder) : IBaseViewHolder,
    TextView.OnEditorActionListener {
    private lateinit var nameEdittext: EditText
    public override fun baseInitView(view: View) {
        nameEdittext = view.findViewById(R.id.name_edit_text)
        nameEdittext.setOnEditorActionListener(this)
    }

    public override fun baseBindEntity(entity: BaseEntity) {
        nameEdittext.setText(entity.name)
    }

    public override fun baseUpdateEntity(entity: BaseEntity) {
        entity.name = nameEdittext.text.toString()
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
