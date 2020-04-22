package com.lvkang.ppjoke.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lvkang.ppjoke.model.Feed

class FeedDetailActivity : AppCompatActivity() {
    companion object {
        fun startFeedDetailActivity(context: Context, item: Feed?, mCategory: String) {
            val intent = Intent(context, FeedDetailActivity::class.java)
            intent.putExtra(KEY_FEED, item)
            intent.putExtra(KEY_GATEGORY, mCategory)
            context.startActivity(intent)
        }

        const val KEY_FEED = "key_feed"
        const val KEY_GATEGORY = "key_category"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val feed = intent.getSerializableExtra(KEY_FEED) as Feed?
        if (feed == null) {
            finish()
            return
        }
        var viewHandler: ViewHandler? = null
        if (feed.itemType == Feed.TYPE_IMAGE) {
            viewHandler = ImageViewHandler(this)
        } else {
            viewHandler = VideoViewHandler(this)
        }
        viewHandler.bindInitData(feed)
    }
}