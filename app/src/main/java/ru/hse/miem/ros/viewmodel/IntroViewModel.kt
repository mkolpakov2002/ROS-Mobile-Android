package ru.hse.miem.ros.viewmodel

import android.app.Application
import android.content.res.TypedArray
import androidx.lifecycle.ViewModel
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.repositories.ConfigRepository
import ru.hse.miem.ros.data.model.repositories.ConfigRepositoryImpl
import ru.hse.miem.ros.ui.fragments.intro.ScreenItem

/**
 * TODO: Description
 *
 * @author Tanya Rykova
 * @version 1.0.0
 * @created on 22.06.20
 * @updated on 06.08.20
 * @modified by Maxim Kolpakov
 */
class IntroViewModel() : ViewModel() {
    private lateinit var configRepo: ConfigRepository
    lateinit var application: Application

    fun init(application: Application) {
        this.application = application
        configRepo = ConfigRepositoryImpl.getInstance(application)
    }

    val onboardingScreenItems: List<ScreenItem>
        get() {
            val mList: MutableList<ScreenItem> = ArrayList()
            val titleArray: Array<String> =
                application.resources.getStringArray(R.array.intro_title)
            val descrArray: Array<String> =
                application.resources.getStringArray(R.array.intro_descr)
            val imgArray: TypedArray =
                application.resources.obtainTypedArray(R.array.intro_img)
            for (i in titleArray.indices) {
                mList.add(
                    ScreenItem(
                        titleArray[i],
                        descrArray[i],
                        imgArray.getResourceId(i, -1)
                    )
                )
            }
            imgArray.recycle()
            val mListUpdate: List<ScreenItem> = updateScreenItems
            mList.addAll(mListUpdate)
            return mList
        }
    val updateScreenItems: List<ScreenItem>
        get() {
            val mList: MutableList<ScreenItem> = ArrayList()
            val titleArray: Array<String> =
                application.resources.getStringArray(R.array.update_title)
            val descrArray: Array<String> =
                application.resources.getStringArray(R.array.update_descr)
            val imgArray: TypedArray =
                application.resources.obtainTypedArray(R.array.update_img)
            for (i in titleArray.indices) {
                mList.add(
                    ScreenItem(
                        titleArray[i],
                        descrArray[i],
                        imgArray.getResourceId(i, -1)
                    )
                )
            }
            imgArray.recycle()
            return mList
        }

    companion object {
        private val TAG: String = IntroViewModel::class.java.simpleName
    }
}
