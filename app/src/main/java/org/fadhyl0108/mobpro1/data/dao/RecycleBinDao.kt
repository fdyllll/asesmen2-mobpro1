package org.fadhyl0108.mobpro1.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.fadhyl0108.mobpro1.data.entity.RecycleBin

@Dao
interface RecycleBinDao {
    @Query("SELECT * FROM recycle_bin ORDER BY deletedAt DESC")
    fun getAllDeletedItems(): Flow<List<RecycleBin>>

    @Insert
    suspend fun insert(item: RecycleBin)

    @Delete
    suspend fun delete(item: RecycleBin)

    @Query("SELECT * FROM recycle_bin WHERE id = :id")
    suspend fun getById(id: Long): RecycleBin?

    @Query("SELECT * FROM recycle_bin WHERE tableName = :tableName AND originalId = :originalId")
    suspend fun getByOriginalId(tableName: String, originalId: Long): RecycleBin?

    @Query("DELETE FROM recycle_bin")
    suspend fun deleteAll()

    @Query("DELETE FROM recycle_bin WHERE id = :id")
    suspend fun deleteById(id: Long)
} 