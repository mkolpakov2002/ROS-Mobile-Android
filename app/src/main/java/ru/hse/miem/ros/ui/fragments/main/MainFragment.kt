package ru.hse.miem.ros.ui.fragments.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import ru.hse.miem.ros.R
import ru.hse.miem.ros.databinding.FragmentMainBinding
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
    lateinit var navController: NavController
    lateinit var mViewModel: MainViewModel
    private lateinit var binding: FragmentMainBinding
    public override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.drawerLayout.setScrimColor(resources.getColor(R.color.drawerFadeColor))
        navController = findNavController(requireActivity(), R.id.fragment_container)
        activity?.let {
            // Connect toolbar to application
            val activity: AppCompatActivity = activity as AppCompatActivity
            activity.setSupportActionBar(binding.toolbar)

            // Setup home indicator to open drawer layout
            val toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(
                activity, binding.drawerLayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
            )
            binding.drawerLayout.addDrawerListener(toggle)
            toggle.syncState()
        }
        // Select Master tab as home
        binding.tabs.selectTab(binding.tabs.getTabAt(0))
        navController.navigate(R.id.action_to_masterFragment)

        // Setup tabs for navigation
        binding.tabs.addOnTabSelectedListener(object : OnTabSelectedListener {
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
        if ((newTitle == binding.toolbar.title.toString())) {
            return
        }
        binding.toolbar.setTitle(newTitle)
    }

    public override fun onBackPressed(): Boolean {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return true
        }
        return navController.popBackStack()
    }

    @Deprecated("Deprecated in Java")
    public override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId: Int = item.itemId
        if (itemId == android.R.id.home) {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        val TAG: String = MainFragment::class.java.simpleName
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }
}
