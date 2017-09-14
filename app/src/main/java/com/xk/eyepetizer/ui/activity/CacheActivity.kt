package com.xk.eyepetizer.ui.activity

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import com.gyf.barlibrary.ImmersionBar
import com.tt.lvruheng.eyepetizer.adapter.VideoListAdapter
import com.tt.lvruheng.eyepetizer.utils.ObjectSaveUtils
import com.tt.lvruheng.eyepetizer.utils.SPUtils
import com.xk.eyepetizer.R
import com.xk.eyepetizer.mvp.model.bean.Item
import kotlinx.android.synthetic.main.activity_cache.*

class CacheActivity : AppCompatActivity() {
    var mList = ArrayList<Item>()
    lateinit var mAdapter: VideoListAdapter
    var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            var list = msg?.data?.getParcelableArrayList(Item)("beans")
            if (list?.size?.compareTo(0) == 0) {
                tv_hint.visibility = View.VISIBLE
            } else {
                tv_hint.visibility = View.GONE
                if (mList.size > 0) {
                    mList.clear()
                }
                list?.let { mList.addAll(it) }
                mAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cache)
        ImmersionBar.with(this).transparentBar().barAlpha(0.3f).fitsSystemWindows(true).init()
        setToolbar()
        DataAsyncTask(mHandler, this).execute()
    }

    private fun setToolbar() {
        setSupportActionBar(toolbar)
        var bar = supportActionBar
        bar?.title = "我的缓存"
        bar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private class DataAsyncTask(handler: Handler, activity: CacheActivity) : AsyncTask<Void, Void, ArrayList<Item>>() {
        var activity: CacheActivity = activity
        var handler = handler
        override fun doInBackground(vararg params: Void?): ArrayList<Item> {
            var list = ArrayList<Item>()
            var count: Int = SPUtils.getInstance(activity,"downloads").getInt("count")
            var i = 1
            while (i.compareTo(count) <= 0) {
                var bean: Item
                if (ObjectSaveUtils.getValue(activity, "download$i") == null) {
                    continue
                } else {
                    bean = ObjectSaveUtils.getValue(activity, "download$i") as Item
                }
                list.add(bean)
                i++
            }
            return list
        }

        override fun onPostExecute(result: ArrayList<Item>?) {
            super.onPostExecute(result)
            var message = handler.obtainMessage()
            var bundle = Bundle()
            bundle.putParcelableArrayList("beans", result)
            message.data = bundle
            handler.sendMessage(message)
        }
    }
}