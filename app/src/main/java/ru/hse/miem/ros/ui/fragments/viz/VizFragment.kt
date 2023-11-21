package ru.hse.miem.ros.ui.fragments.viz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageButton
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.switchmaterial.SwitchMaterial
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.RosData
import ru.hse.miem.ros.data.model.repositories.rosRepo.node.BaseData
import ru.hse.miem.ros.ui.general.DataListener
import ru.hse.miem.ros.ui.general.WidgetChangeListener
import ru.hse.miem.ros.viewmodel.VizViewModel

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.2
 * @created on 10.01.20
 * @updated on 21.04.20
 * @modified by Tanya Rykova
 */
class VizFragment() : Fragment(), DataListener, WidgetChangeListener {
    private lateinit var mViewModel: VizViewModel
    private lateinit var widgetViewGroupview: WidgetViewGroup
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var optionsOpenButton: ImageButton
    private lateinit var vizEditModeSwitch: SwitchMaterial
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_viz, container, false)
    }

    public override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        widgetViewGroupview = view.findViewById(R.id.widget_groupview)
        widgetViewGroupview.setDataListener(this)
        widgetViewGroupview.setOnWidgetDetailsChanged(this)
        optionsOpenButton = view.findViewById(R.id.viz_options_open_button)
        drawerLayout = view.findViewById(R.id.viz_options_drawer)
        drawerLayout.setScrimColor(resources.getColor(R.color.drawerFadeColor))
        vizEditModeSwitch = view.findViewById(R.id.edit_viz_switch)
    }

    @Deprecated("Deprecated in Java")
    public override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this)[VizViewModel::class.java]
        mViewModel.currentWidgets.observe(
            getViewLifecycleOwner()
        ) { widgetEntities: List<BaseEntity> ->
            widgetViewGroupview.setWidgets(
                widgetEntities,
                mViewModel.lastRosData
            )
        }
        mViewModel.data.observe(
            getViewLifecycleOwner(),
            Observer { data: RosData -> widgetViewGroupview.onNewData(data) }
        )
        vizEditModeSwitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            widgetViewGroupview.setVizEditMode(
                isChecked
            )
        }
        optionsOpenButton.setOnClickListener {
            if (drawerLayout.isDrawerOpen(
                    GravityCompat.END
                )
            ) {
                drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                drawerLayout.openDrawer(GravityCompat.END)
            }
        }
    }

    public override fun onNewWidgetData(data: BaseData) {
        mViewModel.publishData(data)
    }

    public override fun onWidgetDetailsChanged(widgetEntity: BaseEntity) {
        mViewModel.updateWidget(widgetEntity)
    }

    companion object {
        val TAG: String = VizFragment::class.java.simpleName
        fun newInstance(): VizFragment {
            return VizFragment()
        }
    }
}
