package com.monjaz.baiedu.ui.main.work

import android.app.AlertDialog
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView
import com.monjaz.baiedu.R
import com.monjaz.baiedu.base.BaseActivity
import kotlinx.android.synthetic.main.activity_scan_pic.*

class ScanPicActivity : BaseActivity() {

    private var mViews: MutableList<View> = ArrayList()

    private val list = arrayListOf<String>()

    private var position = 0

    override val viewLayoutId: Int get() = R.layout.activity_scan_pic

    override fun initViews() {
        list.addAll(intent!!.getSerializableExtra("pics") as ArrayList<String>)
        position = intent!!.getIntExtra("position", 0)
        initViewPager()
    }

    private fun initViewPager() {
        for (i in 0 until list.size) {
            val imageView = PhotoView(this)
            imageView.setOnClickListener { finish() }
            mViews.add(imageView)
            Glide.with(this).asBitmap()
                    .apply(RequestOptions().placeholder(R.drawable.ic_pic_place_holder))
                    .load(list[i].replace(" ", ""))
                    .into(imageView)
        }
        mViewPager.adapter = ScanPicAdapter()
        mViewPager.offscreenPageLimit = list.size
        mViewPager.currentItem = position
        tvIndicator.text = String.format("%s/%s", position + 1, list.size)
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                tvIndicator.text = String.format("%s/%s", position + 1, mViewPager.childCount)
            }
        })
    }

    inner class ScanPicAdapter : PagerAdapter() {
        override fun isViewFromObject(view: View, arg1: Any): Boolean {
            return view == arg1
        }

        override fun getCount(): Int {
            return mViews.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            container.addView(mViews[position])
            return mViews[position]
        }

        override fun destroyItem(container: ViewGroup, position: Int, arg1: Any) {
            container.removeView(arg1 as View)
        }
    }
}
