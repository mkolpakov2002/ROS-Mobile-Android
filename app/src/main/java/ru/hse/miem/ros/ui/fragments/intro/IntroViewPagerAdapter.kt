package ru.hse.miem.ros.ui.fragments.intro

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import ru.hse.miem.ros.R

/**
 * TODO: Description
 *
 * @author Tanya Rykova
 * @version 1.0.0
 * @created on 19.06.20
 * @updated on
 * @modified by
 */
class IntroViewPagerAdapter(var mContext: Context?, var mListScreenItem: List<ScreenItem>) :
    PagerAdapter() {
    public override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater: LayoutInflater =
            mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layoutScreen: View = inflater.inflate(R.layout.fragent_onboarding, null)
        val imgSlide: ImageView = layoutScreen.findViewById(R.id.introImage)
        val title: TextView = layoutScreen.findViewById(R.id.introTitle)
        val description: TextView = layoutScreen.findViewById(R.id.introDescription)
        title.text = mListScreenItem[position].title
        description.text = mListScreenItem[position].description
        imgSlide.setImageResource(mListScreenItem[position].screenImage)
        container.addView(layoutScreen)
        return layoutScreen
    }

    public override fun getCount(): Int {
        return mListScreenItem.size
    }

    public override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    public override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    public fun getListScreenItem(): List<ScreenItem> {
        return mListScreenItem
    }
}
