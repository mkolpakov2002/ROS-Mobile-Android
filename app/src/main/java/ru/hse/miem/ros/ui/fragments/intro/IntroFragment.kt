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
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import ru.hse.miem.ros.R
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
    lateinit var tabIndicator: TabLayout
    lateinit var buttonNext: Button
    lateinit var buttonGetStarted: Button
    lateinit var buttonAnimation: Animation
    lateinit var buttonConfiguration: Button
    lateinit var editTextConfigName: EditText
    lateinit var videoView: YouTubePlayerView
    lateinit var mViewModel: IntroViewModel
    lateinit var screenItems: List<ScreenItem?>
    var itemPosition: Int = 0
    var requireCheckIn: Boolean = false
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this)[IntroViewModel::class.java]
        this.activity?.let { mViewModel.init(it.application) }
    }
    public override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireCheckIn = requireCheckIn()

        // Init Views
        buttonNext = view.findViewById(R.id.onboarding_btn_next)
        buttonGetStarted = view.findViewById(R.id.onboarding_btn_getStarted)
        buttonConfiguration = view.findViewById(R.id.onboarding_btn_startConfig)
        editTextConfigName = view.findViewById(R.id.onboarding_editText_configName)
        tabIndicator = view.findViewById(R.id.tabIndicator)
        videoView = view.findViewById(R.id.onboarding_video_view)
        buttonAnimation =
            AnimationUtils.loadAnimation(view.context, R.anim.onboarding_buttton_animation)

        // Setup the viewPager
        screenPager = view.findViewById(R.id.screen_viewpager)
        introViewPagerAdapter = IntroViewPagerAdapter(context, if (requireCheckIn) mViewModel.onboardingScreenItems else mViewModel.updateScreenItems)
        screenItems = introViewPagerAdapter.getListScreenItem()
        screenPager.setAdapter(introViewPagerAdapter)

        // Setup tablayout
        tabIndicator.setupWithViewPager(screenPager)

        // tablayout add change listener
        tabIndicator.addOnTabSelectedListener(object : OnTabSelectedListener {
            public override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == screenItems.size) {
                    loadVideoScreen()
                }
            }

            public override fun onTabUnselected(tab: TabLayout.Tab) {}
            public override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // Set the video
        lifecycle.addObserver(videoView)

        // next button click listener
        buttonNext.setOnClickListener { v: View? ->
            try {
                jumpToNextScreen()
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
        // Get started Button click listener
        buttonGetStarted.setOnClickListener { v: View? -> loadConfigNameScreen() }
        // NameConfig Click Listener
        buttonConfiguration.setOnClickListener { v: View? ->
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
        bundle.putString("configName", editTextConfigName.text.toString())

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
        buttonGetStarted.animation = buttonAnimation
        buttonNext.visibility = View.INVISIBLE
        buttonGetStarted.visibility = View.VISIBLE
        tabIndicator.visibility = View.INVISIBLE
        screenPager.visibility = View.INVISIBLE
        videoView.visibility = View.VISIBLE
    }

    private fun loadConfigNameScreen() {
        buttonGetStarted.animation = null
        buttonConfiguration.animation = buttonAnimation
        buttonGetStarted.visibility = View.INVISIBLE
        videoView.visibility = View.INVISIBLE
        buttonConfiguration.visibility = View.VISIBLE
        editTextConfigName.visibility = View.VISIBLE
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
    ): View? {
        return inflater.inflate(R.layout.fragment_intro, container, false)
    }

    companion object {
        private val TAG: String = IntroFragment::class.java.simpleName
        fun newInstance(): IntroFragment {
            return IntroFragment()
        }
    }
}
