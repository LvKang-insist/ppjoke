package com.lvkang.ppjoke.model

import androidx.annotation.Nullable
import java.io.Serializable

/**
 * @author 345
 */
class Ugc : Serializable {
    /**
     * likeCount : 153
     * shareCount : 0
     * commentCount : 4454
     * hasFavorite : false
     * hasLiked : true
     * hasdiss:false
     */
    var likeCount = 0
    var shareCount = 0
    var commentCount = 0
    var hasFavorite = false
    var hasdiss = false
    var hasLiked = false

    override fun equals(@Nullable other: Any?): Boolean {
        if (other == null || other !is Ugc)
            return false
        val newUgc = other as Ugc?
        return (likeCount == newUgc!!.likeCount
                && shareCount == newUgc.shareCount
                && commentCount == newUgc.commentCount
                && hasFavorite == newUgc.hasFavorite
                && hasLiked == newUgc.hasLiked
                && hasdiss == newUgc.hasdiss)
    }

}