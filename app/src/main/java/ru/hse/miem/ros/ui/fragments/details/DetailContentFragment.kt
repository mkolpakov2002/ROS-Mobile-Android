package ru.hse.miem.ros.ui.fragments.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import com.google.android.material.button.MaterialButton
import ru.hse.miem.ros.BuildConfig
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.general.WidgetChangeListener
import ru.hse.miem.ros.ui.views.details.DetailViewHolder
import ru.hse.miem.ros.utility.Constants
import ru.hse.miem.ros.utility.Utils
import ru.hse.miem.ros.viewmodel.DetailsViewModel
import java.lang.reflect.Constructor
import java.util.Locale

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 15.03.21
 */
class DetailContentFragment() : Fragment(), WidgetChangeListener {
    private lateinit var navController: NavController
    private lateinit var viewModel: DetailsViewModel
    private lateinit var widgetContainer: ViewGroup
    private lateinit var backButtonOverview: MaterialButton
    private lateinit var backButtonGroup: MaterialButton
    private lateinit var widgetHolder: DetailViewHolder
    //private lateinit var Binding
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail_content, container, false)
    }

    public override fun onDetach() {
        super.onDetach()
        if (this::widgetHolder.isInitialized) {
            widgetHolder.forceWidgetUpdate()
        }
    }

    public override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (this::viewModel.isInitialized) {
            viewModel.popPath(1)
        }
        navController = findNavController(view)
        viewModel = ViewModelProvider(this)[DetailsViewModel::class.java]
        widgetContainer = view.findViewById(R.id.widget_container)
        backButtonOverview = view.findViewById(R.id.back_button_overview)
        backButtonGroup = view.findViewById(R.id.back_button_group)
        viewModel.widget.observe(
            getViewLifecycleOwner()
        ) { baseEntity: BaseEntity -> initView(baseEntity) }
        // Construct back buttons
        backButtonOverview.setOnClickListener { v: View? ->
            navController.popBackStack(
                R.id.detailOverviewFragment,
                false
            )
        }
        backButtonGroup.setOnClickListener { v: View? -> navController.popBackStack() }
    }

    private fun initView(baseEntity: BaseEntity) {
        var entity: BaseEntity? = null
        val widgetPath: List<Long>? = viewModel.widgetPath.getValue()
        if (widgetPath!!.size == 1) {
            backButtonGroup.visibility = View.INVISIBLE
            entity = baseEntity
        } else if (widgetPath.size == 2) {
            backButtonGroup.text = baseEntity.name
            backButtonGroup.visibility = View.VISIBLE
            entity = baseEntity.getChildById((widgetPath[1]))
        }
        try {
            // create and init widget view
            val layoutStr: String = String.format(
                Constants.DETAIL_LAYOUT_FORMAT, entity!!.type!!.lowercase(
                    Locale.getDefault()
                )
            )
            val detailContentLayout: Int = Utils.getResId(layoutStr, R.layout::class.java)
            val inflator: LayoutInflater = LayoutInflater.from(widgetContainer.context)
            val itemView: View = inflator.inflate(detailContentLayout, widgetContainer, false)

            // Create and init view holder
            val viewholderClassPath: String = (BuildConfig.APPLICATION_ID
                    + String.format(
                Constants.VIEWHOLDER_FORMAT,
                entity.type!!.lowercase(Locale.getDefault()),
                entity.type
            ))
            val clazzObject: Class<DetailViewHolder> =
                Class.forName(viewholderClassPath) as Class<DetailViewHolder>
            val cons: Constructor<DetailViewHolder> = clazzObject.getConstructor()
            widgetHolder = cons.newInstance()
            widgetHolder.widgetChangeListener = this
            widgetHolder.viewModel = viewModel
            widgetHolder.itemView = itemView
            widgetHolder.widget = entity

            // Add view
            widgetContainer.removeAllViews()
            widgetContainer.addView(itemView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    public override fun onWidgetDetailsChanged(widgetEntity: BaseEntity) {
        viewModel.updateWidget(widgetEntity)
    }

    companion object {
        var TAG: String = DetailContentFragment::class.java.simpleName
    }
}
