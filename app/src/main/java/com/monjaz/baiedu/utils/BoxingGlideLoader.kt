package com.monjaz.baiedu.utils

import android.graphics.Bitmap
import android.widget.ImageView

import com.bilibili.boxing.loader.IBoxingCallback
import com.bilibili.boxing.loader.IBoxingMediaLoader
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.monjaz.baiedu.R
import com.monjaz.baiedu.manage.MyApplication

/**
 * Created by 俊康 on 2017/9/2.
 */

class BoxingGlideLoader : IBoxingMediaLoader {

    override fun displayThumbnail(img: ImageView, absPath: String, width: Int, height: Int) {
        val path = "file://$absPath"
        try {
            // https://github.com/bumptech/glide/issues/1531
            Glide.with(MyApplication.getContext()).load(path)
                    .apply(RequestOptions.centerCropTransform().placeholder(R.drawable.ic_boxing_default_image)
                            .centerCrop().override(width, height)).into(img)
        } catch (ignore: IllegalArgumentException) {
        }

    }

    override fun displayRaw(img: ImageView, absPath: String, width: Int, height: Int, callback: IBoxingCallback?) {
        val path = "file://$absPath"
        val request = Glide.with(MyApplication.getContext())
                .asBitmap()
                .load(path)
        if (width > 0 && height > 0) {
            request.apply(RequestOptions().override(width, height))
        }
        request.listener(object : RequestListener<Bitmap> {
            override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Bitmap>, isFirstResource: Boolean): Boolean {
                if (callback != null) {
                    callback.onFail(e)
                    return true
                }
                return false
            }

            override fun onResourceReady(resource: Bitmap?, model: Any, target: Target<Bitmap>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                if (resource != null && callback != null) {
                    img.setImageBitmap(resource)
                    callback.onSuccess()
                    return true
                }
                return false
            }
        }).into(img)
    }

}
