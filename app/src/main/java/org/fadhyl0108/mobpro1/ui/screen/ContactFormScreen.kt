package org.fadhyl0108.mobpro1.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.fadhyl0108.mobpro1.model.Contact
import org.fadhyl0108.mobpro1.ui.components.ConfirmationDialog
import org.fadhyl0108.mobpro1.util.ValidationUtils
import org.fadhyl0108.mobpro1.viewmodel.ContactViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactFormScreen(
    contactId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: ContactViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    
    var showValidationError by remember { mutableStateOf(false) }
    var validationMessage by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(contactId) {
        contactId?.let {
            viewModel.getContactById(it)?.let { contact ->
                name = contact.name
                phoneNumber = contact.phoneNumber
                email = contact.email
                address = contact.address
                category = contact.category
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (contactId == null) "Tambah Kontak" else "Edit Kontak") },
                navigationIcon = {
                    IconButton(onClick = { showConfirmDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama") },
                modifier = Modifier.fillMaxWidth(),
                isError = showValidationError && !ValidationUtils.validateNotEmpty(name)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Nomor Telepon") },
                modifier = Modifier.fillMaxWidth(),
                isError = showValidationError && !ValidationUtils.validatePhoneNumber(phoneNumber)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                isError = showValidationError && !ValidationUtils.validateEmail(email)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Alamat") },
                modifier = Modifier.fillMaxWidth(),
                isError = showValidationError && !ValidationUtils.validateNotEmpty(address)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Kategori") },
                modifier = Modifier.fillMaxWidth(),
                isError = showValidationError && !ValidationUtils.validateNotEmpty(category)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (validateInput(name, phoneNumber, email, address, category) { message ->
                        validationMessage = message
                    }) {
                        val contact = Contact(
                            id = contactId ?: 0,
                            name = name,
                            phoneNumber = phoneNumber,
                            email = email,
                            address = address,
                            category = category
                        )
                        if (contactId == null) {
                            viewModel.insertContact(contact)
                        } else {
                            viewModel.updateContact(contact)
                        }
                        onNavigateBack()
                    } else {
                        showValidationError = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (contactId == null) "Simpan" else "Update")
            }
        }
    }

    if (showConfirmDialog) {
        ConfirmationDialog(
            title = "Konfirmasi",
            message = "Apakah Anda yakin ingin keluar? Perubahan yang belum disimpan akan hilang.",
            onDismiss = { showConfirmDialog = false },
            onConfirm = onNavigateBack
        )
    }

    if (showValidationError) {
        AlertDialog(
            onDismissRequest = { showValidationError = false },
            title = { Text("Validasi Error") },
            text = { Text(validationMessage) },
            confirmButton = {
                TextButton(onClick = { showValidationError = false }) {
                    Text("OK")
                }
            }
        )
    }
}

private fun validateInput(
    name: String,
    phoneNumber: String,
    email: String,
    address: String,
    category: String,
    onValidationError: (String) -> Unit
): Boolean {
    if (!ValidationUtils.validateNotEmpty(name)) {
        onValidationError("Nama tidak boleh kosong")
        return false
    }
    if (!ValidationUtils.validatePhoneNumber(phoneNumber)) {
        onValidationError("Nomor telepon tidak valid")
        return false
    }
    if (!ValidationUtils.validateEmail(email)) {
        onValidationError("Email tidak valid")
        return false
    }
    if (!ValidationUtils.validateNotEmpty(address)) {
        onValidationError("Alamat tidak boleh kosong")
        return false
    }
    if (!ValidationUtils.validateNotEmpty(category)) {
        onValidationError("Kategori tidak boleh kosong")
        return false
    }
    return true
} 