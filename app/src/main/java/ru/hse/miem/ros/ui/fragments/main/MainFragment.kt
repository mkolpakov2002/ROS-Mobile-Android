package ru.hse.miem.ros.ui.fragments.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import ru.hse.miem.ros.R
import ru.hse.miem.ros.viewmodel.MainViewModel

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.1
 * @created on 10.01.2020
 * @updated on 27.07.2020
 * @modified by Tanya Rykova
 * @updated on 05.11.2020
 * @modified by Maxim Kolpakov
 */
class MainFragment() : Fragment(), OnBackPressedListener {
    lateinit var tabLayout: TabLayout
    lateinit var navController: NavController
    lateinit var drawerLayout: DrawerLayout
    lateinit var toolbar: Toolbar
    lateinit var mViewModel: MainViewModel
    public override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tabLayout = view.findViewById(R.id.tabs)
        toolbar = view.findViewById(R.id.toolbar)
        drawerLayout = view.findViewById(R.id.drawer_layout)
        navController = findNavController(requireActivity(), R.id.fragment_container)
        drawerLayout.setScrimColor(resources.getColor(R.color.drawerFadeColor))

        // Connect toolbar to application
        if (activity is AppCompatActivity) {
            val activity: AppCompatActivity? = activity as AppCompatActivity?
            activity!!.setSupportActionBar(toolbar)

            // Setup home indicator to open drawer layout
            val toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(
                activity, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
            )
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()
        }

        // Select Master tab as home
        tabLayout.selectTab(tabLayout.getTabAt(0))
        navController.navigate(R.id.action_to_masterFragment)

        // Setup tabs for navigation
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            public override fun onTabSelected(tab: TabLayout.Tab) {
                Log.i(TAG, "On Tab selected: " + tab.text)
                when (tab.text.toString()) {
                    "Master" -> navController.navigate(R.id.action_to_masterFragment)
                    "Details" -> navController.navigate(R.id.action_to_detailFragment)
                    "SSH" -> navController.navigate(R.id.action_to_sshFragment)
                    else -> navController.navigate(R.id.action_to_vizFragment)
                }
            }

            public override fun onTabUnselected(tab: TabLayout.Tab) {}
            public override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    @Deprecated("Deprecated in Java")
    public override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        activity?.let { mViewModel.init(it.application) }
        if (arguments != null) {
            mViewModel.createFirstConfig(requireArguments().getString("configName"))
        }
        mViewModel.getConfigTitle()
            .observe(getViewLifecycleOwner()) { newTitle: String? -> setTitle(newTitle) }
    }

    private fun setTitle(newTitle: String?) {
        if ((newTitle == toolbar.title.toString())) {
            return
        }
        toolbar.setTitle(newTitle)
    }

    public override fun onBackPressed(): Boolean {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            return true
        }
        return navController.popBackStack()
    }

    @Deprecated("Deprecated in Java")
    public override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId: Int = item.itemId
        if (itemId == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    companion object {
        val TAG: String = MainFragment::class.java.simpleName
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }
}
