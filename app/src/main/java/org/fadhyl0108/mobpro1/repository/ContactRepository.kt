package org.fadhyl0108.mobpro1.repository

import com.google.gson.Gson
import org.fadhyl0108.mobpro1.data.ContactDao
import org.fadhyl0108.mobpro1.data.dao.RecycleBinDao
import org.fadhyl0108.mobpro1.model.Contact
import org.fadhyl0108.mobpro1.data.entity.RecycleBin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ContactRepository(
    private val contactDao: ContactDao,
    private val recycleBinDao: RecycleBinDao
) {
    val allContacts: Flow<List<Contact>> = contactDao.getAllContacts()
    val deletedContacts: Flow<List<Contact>> = contactDao.getDeletedContacts()
    val recycleBinItems: Flow<List<RecycleBin>> = recycleBinDao.getAllDeletedItems()

    suspend fun insertContact(contact: Contact): Long {
        return contactDao.insertContact(contact)
    }

    suspend fun updateContact(contact: Contact) {
        contactDao.updateContact(contact)
    }

    suspend fun deleteContact(id: Long) {
        val contact = contactDao.getContactById(id)
        contact?.let {
            // Simpan ke RecycleBin sebelum dihapus
            val recycleBinItem = RecycleBin(
                originalId = it.id,
                tableName = "contacts",
                data = Gson().toJson(it)
            )
            recycleBinDao.insert(recycleBinItem)
            contactDao.softDeleteContact(id)
        }
    }

    suspend fun restoreContact(id: Long) {
        contactDao.restoreContact(id)
        // Hapus dari RecycleBin setelah di-restore
        recycleBinDao.getByOriginalId("contacts", id)?.let {
            recycleBinDao.delete(it)
        }
    }

    suspend fun permanentlyDeleteAllDeletedContacts() {
        contactDao.permanentlyDeleteAllDeletedContacts()
        // Hapus semua item terkait dari RecycleBin
        recycleBinItems.first().forEach { recycleBinDao.delete(it) }
    }

    suspend fun permanentlyDeleteRecycleBinItem(id: Long) {
        recycleBinDao.deleteById(id)
    }

    fun searchContacts(query: String): Flow<List<Contact>> {
        return contactDao.searchContacts(query)
    }

    suspend fun getContactById(id: Long): Contact? {
        return contactDao.getContactById(id)
    }

    suspend fun restoreFromRecycleBin(recycleBinId: Long) {
        val recycleBinItem = recycleBinDao.getById(recycleBinId)
        recycleBinItem?.let {
            val contact = Gson().fromJson(it.data, Contact::class.java)
            contactDao.restoreContact(contact.id)
            recycleBinDao.delete(it)
        }
    }
} 