package ru.hse.miem.ros.ui.activity

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import ru.hse.miem.ros.R
import ru.hse.miem.ros.databinding.ActivityMainBinding
import ru.hse.miem.ros.ui.fragments.intro.IntroFragment
import ru.hse.miem.ros.ui.fragments.main.MainFragment
import ru.hse.miem.ros.ui.fragments.main.OnBackPressedListener

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.1
 * @created on 16.01.20
 * @updated on 19.06.20
 * @modified by Tanya Rykova
 * @updated on 27.07.20
 * @modified by Tanya Rykova
 */
class MainActivity() : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            if (savedInstanceState == null && requiresIntro()) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, IntroFragment.newInstance())
                    .commitNow()
            } else {
                val myToolbar: Toolbar? = findViewById(R.id.toolbar)
                setSupportActionBar(myToolbar)
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, MainFragment.newInstance())
                        .commitNow()
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        this.requestPermissions()
    }

    @Deprecated("Deprecated in Java")
    public override fun onBackPressed() {
        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.main_container)
        if (fragment is OnBackPressedListener) {
            if (fragment.onBackPressed()) {
                return
            }
        }
        super.onBackPressed()
    }

    private fun requestPermissions() {
        val permissions: Array<String> = arrayOf(
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        ActivityCompat.requestPermissions(this, permissions, LOCATION_PERM)
    }

    // Check in required if update is available or onboarding has not been done yet
    @Throws(PackageManager.NameNotFoundException::class)
    private fun requiresIntro(): Boolean {
        val pref: SharedPreferences =
            applicationContext.getSharedPreferences("introPrefs", MODE_PRIVATE)
        return (pref.getInt(
            "VersionNumber",
            0
        ) != packageManager.getPackageInfo(packageName, 0).versionCode) ||
                !pref.getBoolean("CheckedIn", false)
    }

    companion object {
        private val LOCATION_PERM: Int = 101
        var TAG: String = MainActivity::class.java.simpleName
    }
}
