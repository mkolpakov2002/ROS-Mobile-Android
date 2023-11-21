package ru.hse.miem.ros.ui.fragments.details

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.general.WidgetChangeListener
import ru.hse.miem.ros.utility.Utils
import ru.hse.miem.ros.viewmodel.DetailsViewModel

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 13.03.21
 */
class DetailMainFragment() : Fragment(), RecyclerWidgetItemTouchHelper.TouchListener,
    WidgetChangeListener {
    private var navController: NavController? = null
    private lateinit var viewModel: DetailsViewModel
    private lateinit var addWidgetCard: MaterialCardView
    private lateinit var noWidgetTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: WidgetListAdapter
    private var currentWidgetPath: List<String>? = null
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail_main, container, false)
    }

    public override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[DetailsViewModel::class.java]
        viewModel.select(null)
        currentWidgetPath = ArrayList()

        // Find views
        noWidgetTextView = view.findViewById(R.id.no_widget_text)
        recyclerView = view.findViewById(R.id.recyclerview)
        addWidgetCard = view.findViewById(R.id.add_widget_card)
        navController = findNavController(view)

        // React on new widget click action
        addWidgetCard.setOnClickListener { v: View? -> showDialogWithWidgetNames() }

        // Setup recyclerview
        mAdapter =
            WidgetListAdapter { entity: BaseEntity -> onWidgetClicked(entity) }
        val layoutManager = LinearLayoutManager(
            context
        )
        recyclerView.suppressLayout(true)
        recyclerView.setLayoutManager(layoutManager)
        recyclerView.setItemAnimator(DefaultItemAnimator())
        recyclerView.setAdapter(mAdapter)
        val touchHelper: ItemTouchHelper.SimpleCallback = RecyclerWidgetItemTouchHelper(
            0,
            ItemTouchHelper.LEFT, this
        )
        ItemTouchHelper(touchHelper).attachToRecyclerView(recyclerView)

        // Bind view model widgets etc.
        viewModel.currentWidgets.observe(
            getViewLifecycleOwner(),
            Observer { newWidgets: List<BaseEntity> -> mAdapter.setWidgets(newWidgets) }
        )
        viewModel.widgetsEmpty().observe(
            getViewLifecycleOwner()
        ) { empty: Boolean? -> noWidgetTextView.visibility = if ((empty)!!) View.VISIBLE else View.GONE }
    }

    private fun onWidgetClicked(entity: BaseEntity) {
        Log.i(TAG, "Clicked " + entity.name)
        viewModel.select(entity.id)
        navController!!.navigate(R.id.action_detailOverview_to_depth1)
    }

    private fun showDialogWithWidgetNames() {
        val context: Context = context ?: return
        val widgetNames: Array<String> = resources.getStringArray(R.array.widget_names)
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
        dialogBuilder.setTitle("Create Widget")
        dialogBuilder.setItems(
            widgetNames
        ) { dialog: DialogInterface?, item: Int ->
            val title: String = widgetNames[item]
            val descName: String = title + "_description"
            val description: String = Utils.getStringByName(context, descName)
            val dialogChecker: AlertDialog.Builder = AlertDialog.Builder(
                requireContext()
            )
            dialogChecker.setTitle(title)
            dialogChecker.setMessage(description)
            dialogChecker.setPositiveButton(
                "Create"
            ) { dialog1: DialogInterface?, which: Int ->
                viewModel.createWidget(title)
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
        // get the removed item name to display it in snack bar
        val deletedWidget: BaseEntity? = mAdapter.getItem(index)

        if (deletedWidget!=null){
            // remove the item from recycler view
            viewModel.deleteWidget(deletedWidget)

            // showing snack bar with Undo option
            val undoText: String = getString(R.string.widget_undo, deletedWidget.name)
            val snackbar: Snackbar = Snackbar.make(requireView(), undoText, Snackbar.LENGTH_LONG)
            snackbar.setAction("UNDO") { view: View? ->
                // undo is selected, restore the deleted item
                viewModel.restoreWidget()
            }
            snackbar.setActionTextColor(resources.getColor(R.color.color_attention))
            snackbar.show()
        }
    }

    public override fun onWidgetDetailsChanged(widgetEntity: BaseEntity) {
        viewModel.updateWidget(widgetEntity)
    }

    companion object {
        var TAG: String = DetailMainFragment::class.java.simpleName
    }
}
