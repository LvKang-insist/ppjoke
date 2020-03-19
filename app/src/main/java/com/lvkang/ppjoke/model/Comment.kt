package com.lvkang.ppjoke.model

import androidx.annotation.Nullable
import java.io.Serializable

/**
 * @author 345
 * 评论
 */
class Comment : Serializable {
    /**
     * id : 784
     * itemId : 6739143063064549000
     * commentId : 6739212214408380000
     * userId : 65200808093
     * commentType : 1
     * createTime : 1569095152
     * commentCount : 4454
     * likeCount : 152
     * commentText : 看见没。比甜蜜暴击好看一万倍！
     * imageUrl : null
     * videoUrl : null
     * width : 0
     * height : 0
     * hasLiked : false
     * author : {"id":978,"userId":65200808093,"name":"带鱼裹上面包糠","avatar":"","description":null,"likeCount":0,"topCommentCount":0,"followCount":0,"followerCount":0,"qqOpenId":null,"expires_time":0,"score":0,"historyCount":0,"commentCount":0,"favoriteCount":0,"feedCount":0,"hasFollow":false}
     * ugc : {"likeCount":153,"shareCount":0,"commentCount":4454,"hasFavorite":false,"hasLiked":true}
     */
    var id = 0
    var itemId: Long = 0
    var commentId: Long = 0
    var userId: Long = 0
    var commentType = 0
    var createTime: Long = 0
    var commentCount = 0
    var likeCount = 0
    var commentText: String? = null
    var imageUrl: String? = null
    var videoUrl: String? = null
    var width = 0
    var height = 0
    var hasLiked = false
    var author: User? = null
    var ugc: Ugc? = null

    override fun equals(@Nullable other: Any?): Boolean {
        if (other == null || other !is Comment)
            return false
        val newComment = other as Comment?
        return (likeCount == newComment!!.likeCount
                && hasLiked == newComment.hasLiked
                && author != null && author == newComment.author
                && ugc != null && ugc == newComment.ugc)
    }
}