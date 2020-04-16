package com.lvkang.libnetwork.cache

import androidx.room.*

@Dao
interface CacheDao {
    //插入一条数据，如果有冲突，按照指定的方式执行
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(cache: Cache): Long

    //查询，cache 表中 key 的列
    @Query("select *from cache where `key`=:key")
    fun getCache(key: String): Cache

    //删除
    @Delete
    fun delete(cache: Cache)

    //更新一条数据
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(cache: Cache)
}