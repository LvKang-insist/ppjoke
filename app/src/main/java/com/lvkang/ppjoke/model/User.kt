package com.lvkang.ppjoke.model

import android.text.TextUtils
import java.io.Serializable


class User : Serializable {
    /**
     * id : 962
     * userId : 3223400206308231
     * name : 二师弟请随我来
     * avatar :
     * description :
     * likeCount : 0
     * topCommentCount : 0
     * followCount : 0
     * followerCount : 0
     * qqOpenId : null
     * expires_time : 0
     * score : 0
     * historyCount : 0
     * commentCount : 0
     * favoriteCount : 0
     * feedCount : 0
     * hasFollow : false
     */
    var id = 0
    var userId: Long = 0
    var name: String? = null
    var avatar: String? = null
    var description: String? = null
    var likeCount = 0
    var topCommentCount = 0
    var followCount = 0
    var followerCount = 0
    var qqOpenId: String? = null
    var expires_time: Long = 0
    var score = 0
    var historyCount = 0
    var commentCount = 0
    var favoriteCount = 0
    var feedCount = 0
    var hasFollow = false


    override fun equals(other: Any?): Boolean {
        if (other == null || other is User) return false
        val newUser = other as User?
        return TextUtils.equals(name, newUser!!.name)
                && TextUtils.equals(avatar, newUser.avatar)
                && TextUtils.equals(description, newUser.description)
                && likeCount == newUser.likeCount
                && topCommentCount == newUser.topCommentCount
                && followCount == newUser.followCount
                && followerCount == newUser.followerCount
                && qqOpenId == newUser.qqOpenId
                && expires_time == newUser.expires_time
                && score == newUser.score
                && historyCount == newUser.historyCount
                && commentCount == newUser.commentCount
                && favoriteCount == newUser.favoriteCount
                && feedCount == newUser.feedCount
                && hasFollow == newUser.hasFollow
    }

}