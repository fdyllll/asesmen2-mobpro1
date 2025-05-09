package org.fadhyl0108.mobpro1.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson
import org.fadhyl0108.mobpro1.data.entity.RecycleBin
import org.fadhyl0108.mobpro1.model.Contact
import org.fadhyl0108.mobpro1.ui.components.ConfirmationDialog
import org.fadhyl0108.mobpro1.viewmodel.ContactViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecycleBinScreen(
    onNavigateBack: () -> Unit,
    viewModel: ContactViewModel = viewModel()
) {
    val recycleBinItems by viewModel.recycleBinItems.collectAsState(initial = emptyList())
    var showRestoreDialog by remember { mutableStateOf<RecycleBin?>(null) }
    var showDeleteDialog by remember { mutableStateOf<RecycleBin?>(null) }
    var showEmptyDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recycle Bin") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (recycleBinItems.isNotEmpty()) {
                        IconButton(onClick = { showEmptyDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Kosongkan Recycle Bin")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (recycleBinItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Recycle Bin kosong")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recycleBinItems) { item ->
                    RecycleBinItem(
                        item = item,
                        onRestoreClick = { showRestoreDialog = item },
                        onDeleteClick = { showDeleteDialog = item }
                    )
                }
            }
        }
    }

    showRestoreDialog?.let { item ->
        val contact = try {
            Gson().fromJson(item.data, Contact::class.java)
        } catch (_: Exception) {
            null
        }

        ConfirmationDialog(
            title = "Restore Kontak",
            message = "Apakah Anda yakin ingin mengembalikan kontak ${contact?.name}?",
            onDismiss = { showRestoreDialog = null },
            onConfirm = {
                viewModel.restoreFromRecycleBin(item.id)
                showRestoreDialog = null
            }
        )
    }

    showDeleteDialog?.let { item ->
        val contact = try {
            Gson().fromJson(item.data, Contact::class.java)
        } catch (_: Exception) {
            null
        }

        ConfirmationDialog(
            title = "Hapus Permanen",
            message = "Apakah Anda yakin ingin menghapus permanen kontak ${contact?.name}?",
            onDismiss = { showDeleteDialog = null },
            onConfirm = {
                viewModel.permanentlyDeleteRecycleBinItem(item.id)
                showDeleteDialog = null
            }
        )
    }

    if (showEmptyDialog) {
        ConfirmationDialog(
            title = "Kosongkan Recycle Bin",
            message = "Apakah Anda yakin ingin menghapus semua item di Recycle Bin?",
            onDismiss = { showEmptyDialog = false },
            onConfirm = {
                viewModel.permanentlyDeleteAllDeletedContacts()
                showEmptyDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecycleBinItem(
    item: RecycleBin,
    onRestoreClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val contact = try {
        Gson().fromJson(item.data, Contact::class.java)
    } catch (_: Exception) {
        null
    }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val deletedDate = dateFormat.format(Date(item.deletedAt))

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            contact?.let {
                Text(
                    text = it.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = it.phoneNumber,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Dihapus pada: $deletedDate",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onRestoreClick) {
                    Icon(Icons.Default.Refresh, contentDescription = "Restore")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Restore")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus Permanen")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hapus Permanen")
                }
            }
        }
    }
} 