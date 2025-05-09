package org.fadhyl0108.mobpro1.data

import androidx.room.*
import org.fadhyl0108.mobpro1.model.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts WHERE isDeleted = 0 ORDER BY name ASC")
    fun getAllContacts(): Flow<List<Contact>>

    @Query("SELECT * FROM contacts WHERE isDeleted = 1 ORDER BY updatedAt DESC")
    fun getDeletedContacts(): Flow<List<Contact>>

    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactById(id: Long): Contact?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact): Long

    @Update
    suspend fun updateContact(contact: Contact)

    @Query("UPDATE contacts SET isDeleted = 1, updatedAt = :timestamp WHERE id = :id")
    suspend fun softDeleteContact(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE contacts SET isDeleted = 0, updatedAt = :timestamp WHERE id = :id")
    suspend fun restoreContact(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM contacts WHERE isDeleted = 1")
    suspend fun permanentlyDeleteAllDeletedContacts()

    @Query("SELECT * FROM contacts WHERE name LIKE '%' || :query || '%' OR phoneNumber LIKE '%' || :query || '%'")
    fun searchContacts(query: String): Flow<List<Contact>>
} 