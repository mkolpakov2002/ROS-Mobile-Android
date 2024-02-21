package ru.hse.miem.ros.ui.fragments.device

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import ru.hse.miem.ros.R
import ru.hse.miem.ros.databinding.FragmentDeviceBinding
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
class DeviceFragment() : Fragment(), OnBackPressedListener {
    lateinit var navController: NavController
    private lateinit var mViewModel: MainViewModel
    private lateinit var binding: FragmentDeviceBinding
    private lateinit var observer: LifecycleObserver
    private val menuHost: MenuHost get() = requireActivity()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                mViewModel = ViewModelProvider(this@DeviceFragment)[MainViewModel::class.java]
//                activity?.let { mViewModel.init(it.application) }
//                arguments?.getString("configName", "")?.let { configName ->
//                    if (configName.isNotEmpty()) {
//                        mViewModel.createFirstConfig(configName)
//                    }
//                }
                mViewModel.getConfigTitle()
                    .observe(viewLifecycleOwner) { newTitle: String? -> setTitle(newTitle) }
                menuHost.addMenuProvider(object : MenuProvider {
                    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                        // Здесь вы можете добавить элементы меню
                    }

                    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                        if (menuItem.itemId == android.R.id.home) {
                            binding.drawerLayout.openDrawer(GravityCompat.START)
                            return true
                        }
                        return false
                    }
                })
                activity?.lifecycle?.removeObserver(observer)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.lifecycle?.addObserver(observer)
        binding.drawerLayout.setScrimColor(ContextCompat.getColor(requireContext(), R.color.drawerFadeColor))
        navController = findNavController(requireActivity(), R.id.fragment_container)
        activity?.let { activity ->
            // Connect toolbar to application
            val appCompatActivity = activity as AppCompatActivity
            appCompatActivity.setSupportActionBar(binding.toolbar)

            // Setup home indicator to open drawer layout
            val toggle = ActionBarDrawerToggle(
                activity, binding.drawerLayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
            )
            binding.drawerLayout.addDrawerListener(toggle)
            toggle.syncState()
        }
        // Select Master tab as home
        binding.tabs.selectTab(binding.tabs.getTabAt(0))
        Log.d("Tag", navController.currentDestination.toString())
        navController.navigate(R.id.masterFragment)

        // Setup tabs for navigation
        binding.tabs.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Log.i(TAG, "On Tab selected: " + tab.text)
                when (tab.text.toString()) {
                    "Master" -> navController.navigate(R.id.action_to_masterFragment)
                    "Details" -> navController.navigate(R.id.action_to_detailFragment)
                    "SSH" -> navController.navigate(R.id.action_to_sshFragment)
                    else -> navController.navigate(R.id.action_to_vizFragment)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
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

    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeviceBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        val TAG: String = DeviceFragment::class.java.simpleName
        fun newInstance(): DeviceFragment {
            return DeviceFragment()
        }
    }
}
