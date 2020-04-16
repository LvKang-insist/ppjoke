package com.lvkang.ppjoke.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elvishew.xlog.XLog
import com.hjq.toast.ToastUtils
import com.lvkang.libcommon.PixUtils
import com.lvkang.libcommon.view.PPImageView
import com.lvkang.libcommon.view.RoundFrameLayout
import com.lvkang.libcommon.view.ViewHelper
import com.lvkang.ppjoke.R
import java.io.File
import java.io.FileOutputStream

class ShareDialog(context: Context) : AlertDialog(context) {

    private val shareItems = mutableListOf<ResolveInfo>()
    private var shareContent: String? = null
    private var listener: View.OnClickListener? = null
    private lateinit var shareAdapter: ShareAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        val layout = RoundFrameLayout(context)
        layout.setBackgroundColor(Color.WHITE)
        layout.setViewOutLine(PixUtils.dp2px(20), ViewHelper.RADIUS_TOP)
        setContentView(layout)

        val gridView = RecyclerView(context)
        gridView.layoutManager = GridLayoutManager(context, 4)
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        val margin = PixUtils.dp2px(20)
        params.leftMargin = margin
        params.rightMargin = margin
        params.topMargin = margin
        params.bottomMargin = margin
        params.gravity = Gravity.CENTER
        layout.addView(gridView, params)

        window?.setGravity(Gravity.BOTTOM)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        queryShareItems()
        if (shareContent != null) {
            shareAdapter =
                ShareAdapter(shareItems, context.packageManager, shareContent!!, listener)
            gridView.adapter = shareAdapter
        } else {
            ToastUtils.show("分享内容为空")
        }

    }

    fun setShareContent(shareContent: String) {
        this.shareContent = shareContent
    }

    fun setShareItemClickListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    /**
     * 查询分享入口
     */
    private fun queryShareItems() {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        //分享内容为文本类型
        intent.type = "text/plain"
        //查询可以使用文本分享的所有入口
        val resolve = context.packageManager.queryIntentActivities(intent, 0)
        resolve.forEach {
            //只保留 qq 和 微信的入口
            when (it.activityInfo.packageName) {
                "com.tencent.mm" -> {
                    shareItems.add(it)
                }
                "com.tencent.mobileqq" -> {
                    shareItems.add(it)
                }
            }
        }
    }


    private class ShareAdapter(
        val item: MutableList<ResolveInfo>?,
        val packageManager: PackageManager,
        val shareContent: String,
        val listener: View.OnClickListener?
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_share_item, parent, false)
            return object : RecyclerView.ViewHolder(view) {}
        }

        override fun getItemCount(): Int {
            return item?.size ?: 0
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (item != null) {
                val resolveInfo = item[position]
                val shareImage = holder.itemView.findViewById<PPImageView>(R.id.share_icon)
                shareImage.setImageDrawable(resolveInfo.loadIcon(packageManager))
                val shareText = holder.itemView.findViewById<AppCompatTextView>(R.id.share_text)
                shareText.text = resolveInfo.loadLabel(packageManager)
                shareImage.setOnClickListener {

                    val pkg = resolveInfo.activityInfo.packageName
                    val cls = resolveInfo.activityInfo.name
                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.type = "text/plain"; // 纯文本
//                    intent.type = "image/*"
                    intent.component = ComponentName(pkg, cls)

                    //分享的内容
                    intent.putExtra(Intent.EXTRA_TEXT, shareContent)
//                    intent.putExtra(Intent.EXTRA_STREAM, getUri(shareImage))
                    shareImage.context.startActivity(intent)
                    listener?.onClick(it)
                }
            }

        }

        private fun getUri(shareImage: PPImageView): Uri {
            val bitmap = BitmapFactory.decodeResource(
                shareImage.context.resources,
                R.mipmap.icon_jetpack
            )
            val file = File(shareImage.context.externalCacheDir, "shareImage.png")
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
            return FileProvider.getUriForFile(
                shareImage.context,
                "${shareImage.context.packageName}.fileprovider", file
            )
        }


    }
}