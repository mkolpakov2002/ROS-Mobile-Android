package ru.hse.miem.ros.ui.views.details

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.fragments.details.RecyclerWidgetItemTouchHelper
import ru.hse.miem.ros.ui.fragments.details.WidgetListAdapter
import ru.hse.miem.ros.utility.Utils

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 17.03.21
 */
abstract class WidgetGroupViewHolder() : DetailViewHolder(),
    RecyclerWidgetItemTouchHelper.TouchListener {
    private val widgetViewHolder: WidgetViewHolder = WidgetViewHolder(this)
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: WidgetListAdapter
    private lateinit var addLayerCard: MaterialCardView

    public override fun baseInitView(view: View) {
        widgetViewHolder.baseInitView(view)
        recyclerView = view.findViewById(R.id.recyclerview)
        addLayerCard = view.findViewById(R.id.add_layer_card)

        // React on new widget click action
        addLayerCard.setOnClickListener { v: View? -> showDialogWithLayerNames() }

        // Setup recyclerview
        mAdapter =
            WidgetListAdapter { entity: BaseEntity -> onLayerClicked(entity) }
        val layoutManager = LinearLayoutManager(view.context)
        recyclerView.suppressLayout(true)
        recyclerView.setLayoutManager(layoutManager)
        recyclerView.setItemAnimator(DefaultItemAnimator())
        recyclerView.setAdapter(mAdapter)
        val touchHelper: ItemTouchHelper.SimpleCallback = RecyclerWidgetItemTouchHelper(
            0,
            ItemTouchHelper.LEFT, this
        )
        ItemTouchHelper(touchHelper).attachToRecyclerView(recyclerView)
    }

    public override fun baseBindEntity(entity: BaseEntity) {
        widgetViewHolder.baseBindEntity(entity)
        mAdapter.setWidgets(entity.childEntities)
    }

    public override fun baseUpdateEntity(entity: BaseEntity) {
        widgetViewHolder.baseUpdateEntity(entity)
    }

    private fun onLayerClicked(entity: BaseEntity) {
        Log.i(TAG, "Clicked layer: " + entity.id)
        viewModel?.let {
            it.select(entity.id)
            itemView?.let { it1 ->
                findNavController(it1).navigate(R.id.action_depth1_to_depth2)
            }
        }
    }

    public override fun onSwiped(
        viewHolder: RecyclerView.ViewHolder,
        direction: Int,
        position: Int
    ) {
        if (viewHolder is WidgetListAdapter.ViewHolder) {
            deleteWidget(viewHolder.getAdapterPosition())
        }
    }

    private fun deleteWidget(index: Int) {
        itemView?.let{
            val context: Context = it.context ?: return

            // get the removed item name to display it in snack bar
            val deletedWidget = mAdapter.getItem(index)

            // remove the item from recycler view
            viewModel?.deleteWidget(deletedWidget)

            // showing snack bar with Undo option
            val undoText: String = context.getString(R.string.widget_undo, deletedWidget.name)
            val snackbar: Snackbar = Snackbar.make(it, undoText, Snackbar.LENGTH_LONG)
            snackbar.setAction(
                "UNDO"
            ) { view: View? -> viewModel?.restoreWidget() }
            snackbar.setActionTextColor(context.resources.getColor(R.color.color_attention))
            snackbar.show()
        }
    }

    private fun showDialogWithLayerNames() {
        val context: Context = itemView?.context ?: return
        val layerNames: Array<String> = context.resources.getStringArray(R.array.layer_names)
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
        dialogBuilder.setTitle("Create Layer")
        dialogBuilder.setItems(
            layerNames
        ) { dialog: DialogInterface?, item: Int ->
            val title: String = layerNames[item]
            val descName: String = title + "_description"
            val description: String = Utils.getStringByName(context, descName)
            val dialogChecker: AlertDialog.Builder = AlertDialog.Builder(context)
            dialogChecker.setTitle(title)
            dialogChecker.setMessage(description)
            dialogChecker.setPositiveButton(
                "Create"
            ) { dialog1: DialogInterface?, which: Int ->
                viewModel?.createWidget(title)
            }
            dialogChecker.setNegativeButton("Cancel", null)
            val dialogCheckerObject: AlertDialog = dialogChecker.create()
            dialogCheckerObject.show()
        }

        //Create alert dialog object via builder
        val alertDialogObject: AlertDialog = dialogBuilder.create()

        //Show the dialog
        alertDialogObject.show()
    }

    companion object {
        var TAG: String = WidgetGroupViewHolder::class.java.simpleName
    }
}
