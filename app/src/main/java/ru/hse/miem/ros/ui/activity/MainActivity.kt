package ru.hse.miem.ros.ui.activity

import android.Manifest
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import ru.hse.miem.ros.R
import ru.hse.miem.ros.databinding.ActivityMainBinding
import ru.hse.miem.ros.ui.fragments.device.OnBackPressedListener

class MainActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_container) as NavHostFragment
        navController = navHostFragment.navController
        if (savedInstanceState == null) {
            if (requiresIntro()) {
                navController.navigate(R.id.introFragment)
            } else {
                navController.navigate(R.id.homeFragment)
            }
        }
        this.requestPermissions()
        onBackPressedDispatcher.addCallback(this /* lifecycle owner */, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.main_container)
                if (fragment is OnBackPressedListener) {
                    if (fragment.onBackPressed()) {
                        return
                    }
                }
            }
        })
    }

    private val listener = NavController.OnDestinationChangedListener { controller, destination, arguments ->
        Log.d("Current destination", destination.label.toString())
    }
    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(listener)
    }
    override fun onPause() {
        navController.removeOnDestinationChangedListener(listener)
        super.onPause()
    }

    private fun requestPermissions() {
        val permissions: Array<String> = arrayOf(
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        ActivityCompat.requestPermissions(this, permissions, LOCATION_PERM)
    }

    // Check in required if update is available or onboarding has not been done yet
    private fun requiresIntro(): Boolean {
        val pref: SharedPreferences =
            applicationContext.getSharedPreferences("introPrefs", MODE_PRIVATE)
        return (pref.getInt("VersionNumber", 0).toLong() !=
                packageManager.getPackageInfo(packageName, 0).longVersionCode) ||
                !pref.getBoolean("CheckedIn", false)
    }

    companion object {
        private const val LOCATION_PERM: Int = 101
        var TAG: String = MainActivity::class.java.simpleName
    }
}
