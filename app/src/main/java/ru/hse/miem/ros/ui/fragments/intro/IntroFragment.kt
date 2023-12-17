package ru.hse.miem.ros.ui.fragments.intro

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import ru.hse.miem.ros.R
import ru.hse.miem.ros.databinding.FragmentIntroBinding
import ru.hse.miem.ros.ui.fragments.main.MainFragment
import ru.hse.miem.ros.viewmodel.IntroViewModel


/**
 * TODO: Description
 *
 * @author Tanya Rykova
 * @version 1.0.0
 * @created on 22.06.20
 * @updated on 27.07.20
 * @modified by Tanya Rykova
 */
class IntroFragment() : Fragment() {
    lateinit var screenPager: ViewPager
    lateinit var introViewPagerAdapter: IntroViewPagerAdapter
    lateinit var buttonAnimation: Animation
    lateinit var mViewModel: IntroViewModel
    lateinit var screenItems: List<ScreenItem?>
    var itemPosition: Int = 0
    var requireCheckIn: Boolean = false
    private lateinit var binding: FragmentIntroBinding
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this)[IntroViewModel::class.java]
        this.activity?.let { mViewModel.init(it.application) }
    }
    public override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireCheckIn = requireCheckIn()

        buttonAnimation =
            AnimationUtils.loadAnimation(view.context, R.anim.onboarding_buttton_animation)

        // Setup the viewPager
        screenPager = view.findViewById(R.id.screen_viewpager)
        introViewPagerAdapter = IntroViewPagerAdapter(context, if (requireCheckIn) mViewModel.onboardingScreenItems else mViewModel.updateScreenItems)
        screenItems = introViewPagerAdapter.getListScreenItem()
        screenPager.setAdapter(introViewPagerAdapter)

        // Setup tablayout
        binding.tabIndicator.setupWithViewPager(screenPager)

        // tablayout add change listener
        binding.tabIndicator.addOnTabSelectedListener(object : OnTabSelectedListener {
            public override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == screenItems.size) {
                    loadVideoScreen()
                }
            }

            public override fun onTabUnselected(tab: TabLayout.Tab) {}
            public override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // Set the video
        lifecycle.addObserver(binding.onboardingVideoView)

        // next button click listener
        binding.onboardingBtnNext.setOnClickListener { v: View? ->
            try {
                jumpToNextScreen()
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
        // Get started Button click listener
        binding.onboardingBtnGetStarted.setOnClickListener { v: View? -> loadConfigNameScreen() }
        // NameConfig Click Listener
        binding.onboardingBtnStartConfig.setOnClickListener { v: View? ->
            try {
                createFirstConfig()
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(PackageManager.NameNotFoundException::class)
    private fun createFirstConfig() {
        // Get string for first config name
        val bundle: Bundle = Bundle()
        bundle.putString("configName", binding.onboardingEditTextConfigName.text.toString())

        // Save the Prefs
        setCheckInPrefData()
        loadMainFragment(bundle)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    private fun jumpToNextScreen() {
        itemPosition = screenPager.currentItem
        itemPosition++
        if (itemPosition < screenItems.size) {
            screenPager.setCurrentItem(itemPosition)
        } else {
            if (requireCheckIn) loadVideoScreen() else {
                setUpdatePrefData()
                loadMainFragment(null)
            }
        }
    }

    private fun loadMainFragment(bundle: Bundle?) {
        // Start the next fragment
        if (activity == null) {
            return
        }
        val mainFragment: MainFragment = MainFragment()
        if (bundle != null) mainFragment.setArguments(bundle)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, mainFragment)
            .addToBackStack(null)
            .commit()
    }

    @Deprecated("Deprecated in Java")
    public override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    // show the get started button and hide the indicator and the next button
    private fun loadVideoScreen() {
        binding.onboardingBtnGetStarted.animation = buttonAnimation
        binding.onboardingBtnNext.visibility = View.INVISIBLE
        binding.onboardingBtnGetStarted.visibility = View.VISIBLE
        binding.tabIndicator.visibility = View.INVISIBLE
        screenPager.visibility = View.INVISIBLE
        binding.onboardingVideoView.visibility = View.VISIBLE
    }

    private fun loadConfigNameScreen() {
        binding.onboardingBtnGetStarted.animation = null
        binding.onboardingBtnStartConfig.animation = buttonAnimation
        binding.onboardingBtnGetStarted.visibility = View.INVISIBLE
        binding.onboardingVideoView.visibility = View.INVISIBLE
        binding.onboardingBtnStartConfig.visibility = View.VISIBLE
        binding.onboardingEditTextConfigName.visibility = View.VISIBLE
    }

    @Throws(PackageManager.NameNotFoundException::class)
    private fun setCheckInPrefData() {
        if (context == null) {
            return
        }
        val pref: SharedPreferences =
            requireContext().getSharedPreferences("introPrefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = pref.edit()
        editor.putBoolean("CheckedIn", true)
        editor.apply()
        setUpdatePrefData()
    }

    @Throws(PackageManager.NameNotFoundException::class)
    private fun setUpdatePrefData() {
        if (context == null) {
            return
        }
        val pref: SharedPreferences =
            requireContext().getSharedPreferences("introPrefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = pref.edit()
        editor.putInt(
            "VersionNumber", requireActivity().packageManager.getPackageInfo(
                requireActivity().packageName, 0
            ).versionCode
        )
        editor.apply()
    }

    // Get pref data
    private fun requireCheckIn(): Boolean {
        val pref: SharedPreferences =
            requireContext().getSharedPreferences("introPrefs", Context.MODE_PRIVATE)
        return !pref.getBoolean("CheckedIn", false)
    }

    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        private val TAG: String = IntroFragment::class.java.simpleName
        fun newInstance(): IntroFragment {
            return IntroFragment()
        }
    }
}
