package org.fadhyl0108.mobpro1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import org.fadhyl0108.mobpro1.data.ContactDatabase
import org.fadhyl0108.mobpro1.model.Contact
import org.fadhyl0108.mobpro1.repository.ContactRepository
import org.fadhyl0108.mobpro1.data.UserPreferences
import org.fadhyl0108.mobpro1.data.entity.RecycleBin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ContactRepository
    private val userPreferences: UserPreferences
    
    val allContacts: Flow<List<Contact>>
        get() = repository.allContacts
    val deletedContacts: Flow<List<Contact>>
        get() = repository.deletedContacts
    val recycleBinItems: Flow<List<RecycleBin>>
        get() = repository.recycleBinItems

    val darkMode: Flow<Boolean>
        get() = userPreferences.darkMode
    val gridView: Flow<Boolean>
        get() = userPreferences.gridView
    val fontSize: Flow<Int>
        get() = userPreferences.fontSize

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Contact>>(emptyList())
    val searchResults: StateFlow<List<Contact>> = _searchResults.asStateFlow()

    init {
        val database = ContactDatabase.getDatabase(application)
        repository = ContactRepository(database.contactDao(), database.recycleBinDao())
        userPreferences = UserPreferences(application)
    }

    fun insertContact(contact: Contact) = viewModelScope.launch {
        repository.insertContact(contact)
    }

    fun updateContact(contact: Contact) = viewModelScope.launch {
        repository.updateContact(contact)
    }

    fun deleteContact(id: Long) = viewModelScope.launch {
        repository.deleteContact(id)
    }

    fun restoreContact(id: Long) = viewModelScope.launch {
        repository.restoreContact(id)
    }

    fun restoreFromRecycleBin(recycleBinId: Long) = viewModelScope.launch {
        repository.restoreFromRecycleBin(recycleBinId)
    }

    fun permanentlyDeleteAllDeletedContacts() = viewModelScope.launch {
        repository.permanentlyDeleteAllDeletedContacts()
    }

    fun permanentlyDeleteRecycleBinItem(id: Long) = viewModelScope.launch {
        repository.permanentlyDeleteRecycleBinItem(id)
    }

    fun setDarkMode(enabled: Boolean) = viewModelScope.launch {
        userPreferences.setDarkMode(enabled)
    }

    fun setGridView(enabled: Boolean) = viewModelScope.launch {
        userPreferences.setGridView(enabled)
    }

    fun setFontSize(size: Int) = viewModelScope.launch {
        userPreferences.setFontSize(size)
    }

    fun searchContacts(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            repository.searchContacts(query).collect { contacts ->
                _searchResults.value = contacts
            }
        }
    }

    suspend fun getContactById(id: Long): Contact? {
        return repository.getContactById(id)
    }
}