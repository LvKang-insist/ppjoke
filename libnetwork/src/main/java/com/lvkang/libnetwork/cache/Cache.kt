package com.lvkang.libnetwork.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * @name ppjoke
 * @class name：com.lvkang.libnetwork.cache
 * @author 345 QQ:1831712732
 * @time 2020/3/14 20:58
 * @descriptionx
 */
@Entity(tableName = "cache")
class Cache : Serializable {

    //主键约束，保证 key 的唯一性
    @PrimaryKey
    var key: String? = null

    @ColumnInfo
    var data:ByteArray? = null


}