package org.fadhyl0108.mobpro1.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recycle_bin")
data class RecycleBin(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val originalId: Long,
    val tableName: String,
    val data: String, // JSON string dari data yang dihapus
    val deletedAt: Long = System.currentTimeMillis()
) 