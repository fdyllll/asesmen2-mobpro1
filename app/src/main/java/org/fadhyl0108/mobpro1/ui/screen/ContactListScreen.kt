package org.fadhyl0108.mobpro1.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.fadhyl0108.mobpro1.model.Contact
import org.fadhyl0108.mobpro1.viewmodel.ContactViewModel
import org.fadhyl0108.mobpro1.ui.components.ConfirmationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListScreen(
    onContactClick: (Long) -> Unit,
    onAddContact: () -> Unit,
    onRecycleBinClick: () -> Unit,
    viewModel: ContactViewModel = viewModel()
) {
    val contacts by viewModel.allContacts.collectAsState(initial = emptyList())
    val searchResults by viewModel.searchResults.collectAsState(initial = emptyList())
    val darkMode by viewModel.darkMode.collectAsState(initial = false)
    val gridView by viewModel.gridView.collectAsState(initial = false)
    val fontSize by viewModel.fontSize.collectAsState(initial = 16)
    
    var showDeleteDialog by remember { mutableStateOf<Contact?>(null) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Kontak") },
                actions = {
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Pengaturan")
                    }
                    IconButton(onClick = onRecycleBinClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Recycle Bin")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddContact) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Kontak")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    viewModel.searchContacts(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Cari kontak...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cari") }
            )

            if (searchQuery.isEmpty()) {
                if (gridView) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(contacts) { contact ->
                            ContactGridItem(
                                contact = contact,
                                onContactClick = { onContactClick(contact.id) },
                                onDeleteClick = { showDeleteDialog = contact },
                                fontSize = fontSize
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(contacts) { contact ->
                            ContactListItem(
                                contact = contact,
                                onContactClick = { onContactClick(contact.id) },
                                onDeleteClick = { showDeleteDialog = contact },
                                fontSize = fontSize
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(searchResults) { contact ->
                        ContactListItem(
                            contact = contact,
                            onContactClick = { onContactClick(contact.id) },
                            onDeleteClick = { showDeleteDialog = contact },
                            fontSize = fontSize
                        )
                    }
                }
            }
        }
    }

    showDeleteDialog?.let { contact ->
        ConfirmationDialog(
            title = "Hapus Kontak",
            message = "Apakah Anda yakin ingin menghapus kontak ${contact.name}?",
            onDismiss = { showDeleteDialog = null },
            onConfirm = {
                viewModel.deleteContact(contact.id)
                showDeleteDialog = null
            }
        )
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Pengaturan") },
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Mode Gelap")
                        Switch(
                            checked = darkMode,
                            onCheckedChange = { viewModel.setDarkMode(it) }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tampilan Grid")
                        Switch(
                            checked = gridView,
                            onCheckedChange = { viewModel.setGridView(it) }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Ukuran Font")
                    Slider(
                        value = fontSize.toFloat(),
                        onValueChange = { viewModel.setFontSize(it.toInt()) },
                        valueRange = 12f..24f,
                        steps = 3
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListItem(
    contact: Contact,
    onContactClick: () -> Unit,
    onDeleteClick: () -> Unit,
    fontSize: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onContactClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tambahkan avatar/placeholder di sebelah kiri
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.name.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = fontSize.sp
                )
                Text(
                    text = contact.phoneNumber,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = (fontSize - 2).sp
                )
            }

            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus")
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactGridItem(
    contact: Contact,
    onContactClick: () -> Unit,
    onDeleteClick: () -> Unit,
    fontSize: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(8.dp),
        onClick = onContactClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Tambahkan avatar/placeholder di bagian atas
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contact.name.take(1).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = fontSize.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = contact.phoneNumber,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = (fontSize - 2).sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Pindahkan delete button ke bagian bawah
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus")
            }
        }
    }
}