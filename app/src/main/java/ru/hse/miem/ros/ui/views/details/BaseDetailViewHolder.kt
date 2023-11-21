package ru.hse.miem.ros.ui.views.details

import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.data.model.entities.widgets.IPositionEntity
import ru.hse.miem.ros.ui.general.Position
import ru.hse.miem.ros.ui.general.WidgetChangeListener
import ru.hse.miem.ros.utility.Utils
import ru.hse.miem.ros.viewmodel.DetailsViewModel

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.1
 * @created on 13.02.20
 * @updated on 10.05.20
 * @modified by Maxim Kolpakov
 * @updated on 27.07.20
 * @modified by Tanya Rykova
 * @updated on 05.11.2020
 * @modified by Maxim Kolpakov
 */
abstract class BaseDetailViewHolder<T : BaseEntity>(
    view: View,
    private var changeListener: WidgetChangeListener
) : RecyclerView.ViewHolder(view), OnEditorActionListener {
    private lateinit var viewBackground: View
    lateinit var viewForeground: View
    var detailContend: LinearLayout
    private lateinit var title: TextView
    private lateinit var openButton: ImageView
    private lateinit var updateButton: ImageButton
    private lateinit var renameButton: ImageButton
    private lateinit var xEdittext: EditText
    private lateinit var yEdittext: EditText
    private lateinit var widthEditText: EditText
    private lateinit var heightEdittext: EditText
    protected lateinit var entity: T
    protected lateinit var mViewModel: DetailsViewModel

    init {
        detailContend = view.findViewById(R.id.detailContent)
    }

    protected abstract fun initView(parentView: View)
    protected abstract fun bindEntity(entity: T)
    protected abstract fun updateEntity()
    fun init() {
        baseInitView(itemView)
        initView(itemView)
    }

    fun bind(entity: T) {
        baseBindEntity(entity)
        bindEntity(entity)
    }

    /**
     * Call this method internally to update the bound widget info
     * and subsequently force an update of the widget list.
     */
    protected fun forceWidgetUpdate() {
        baseUpdateEntity()
        updateEntity()
        changeListener.onWidgetDetailsChanged(entity)
    }

    protected open fun baseInitView(parentView: View) {
        title = parentView.findViewById(R.id.title)
        updateButton = parentView.findViewById(R.id.update_button)
        openButton = parentView.findViewById(R.id.open_button)
        renameButton = parentView.findViewById(R.id.rename_button)
        viewBackground = parentView.findViewById(R.id.view_background)
        viewForeground = parentView.findViewById(R.id.view_foreground)
        xEdittext = parentView.findViewById(R.id.x_edit_text)
        yEdittext = parentView.findViewById(R.id.y_edit_text)
        widthEditText = parentView.findViewById(R.id.width_edit_text)
        heightEdittext = parentView.findViewById(R.id.height_edit_text)
        xEdittext.setOnEditorActionListener(this)
        yEdittext.setOnEditorActionListener(this)
        widthEditText.setOnEditorActionListener(this)
        heightEdittext.setOnEditorActionListener(this)
        openButton.setOnClickListener { v: View? ->
            if (detailContend.visibility == View.GONE) {
                detailContend.visibility = View.VISIBLE
                openButton.setImageResource(R.drawable.ic_expand_less_white_24dp)
            } else {
                detailContend.visibility = View.GONE
                openButton.setImageResource(R.drawable.ic_expand_more_white_24dp)
            }
        }
        updateButton.setOnClickListener { v: View? -> forceWidgetUpdate() }
        updateButton.isEnabled = true
        renameButton.setOnClickListener { v: View? -> showRenameDialog() }
        parentView.setOnClickListener { v: View? -> parentView.requestFocus() }
        parentView.setOnFocusChangeListener { v: View?, _: Boolean ->
            Utils.hideSoftKeyboard(
                itemView
            )
        }
    }

    protected open fun baseBindEntity(entity: T) {
        this.entity = entity
        title.text = entity.name ?: "null name"
        val position: Position = (entity as IPositionEntity).getPosition()
        xEdittext.setText(position.x.toString())
        yEdittext.setText(position.y.toString())
        widthEditText.setText(position.width.toString())
        heightEdittext.setText(position.height.toString())
    }

    protected open fun baseUpdateEntity() {
        entity.name = title.text.toString()
        val position = Position()
        position.x = xEdittext.text.toString().toInt()
        position.y = yEdittext.text.toString().toInt()
        position.width = widthEditText.text.toString().toInt()
        position.height = heightEdittext.text.toString().toInt()
        (entity as IPositionEntity).setPosition(position)
    }

    public override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
        when (actionId) {
            EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NEXT, EditorInfo.IME_ACTION_PREVIOUS -> {
                itemView.requestFocus()
                return true
            }
        }
        return false
    }

    private fun showRenameDialog() {
        val context: Context = itemView.context ?: return

        // Set up the input
        val input = EditText(context)
        input.setText(entity.name ?: "null name")
        input.inputType = InputType.TYPE_CLASS_TEXT
        val dialog: AlertDialog = AlertDialog.Builder(context)
            .setTitle(R.string.rename_widget)
            .setView(input)
            .setPositiveButton(
                R.string.ok
            ) { view: DialogInterface?, which: Int ->
                rename(
                    input.text.toString()
                )
            }
            .setNegativeButton(
                R.string.cancel
            ) { view: DialogInterface, _: Int -> view.cancel() }
            .create()
        dialog.show()
    }

    private fun rename(newName: String) {
        title.text = newName.trim { it <= ' ' }
    }

    fun setViewModel(viewModel: DetailsViewModel) {
        mViewModel = viewModel
    }
}
