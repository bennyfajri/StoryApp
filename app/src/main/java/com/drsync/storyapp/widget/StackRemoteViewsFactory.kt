package com.drsync.storyapp.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.drsync.storyapp.R
import com.drsync.storyapp.ui.main.MainViewModel
import com.drsync.storyapp.util.Constant.TAG

class StackRemoteViewsFactory(
    private val mContext: Context
) : RemoteViewsService.RemoteViewsFactory {

    private val mWidgetItems = ArrayList<Bitmap>()
    private val listStory = listOf<String>()

    override fun onDataSetChanged() {
        repeat(listStory.size) {
            Log.d(TAG, "onDataSetChanged: $it")
            val bitmap = Glide.with(mContext)
                .asBitmap()
                .load(listStory[it])
                .submit()
                .get()
            mWidgetItems.add(bitmap)

        }
    }

    override fun onCreate() {}

    override fun onDestroy() {}

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.imageView, mWidgetItems[position])

        val extras = bundleOf(
            ImagesBannerWidget.EXTRA_ITEM to position
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(p0: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}
