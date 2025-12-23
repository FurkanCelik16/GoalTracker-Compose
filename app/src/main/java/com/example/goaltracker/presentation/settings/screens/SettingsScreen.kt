package com.example.goaltracker.presentation.settings.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.goaltracker.core.common.ui.components.DeleteConfirmDialog
import com.example.goaltracker.core.common.ui.components.TopBar
import com.example.goaltracker.presentation.settings.components.*
import com.example.goaltracker.presentation.settings.model.SettingsViewModel
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val systemInDarkTheme = isSystemInDarkTheme()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val isSoundEnabled by viewModel.isSoundOn.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()

    var showResetDialog by remember { mutableStateOf(false) }
    var showNameDialog by remember { mutableStateOf(false) }

    if (showNameDialog) {
        var tempName by remember { mutableStateOf(userName) }
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("İsmini Düzenle") },
            text = {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("Adınız") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.updateName(tempName)
                    showNameDialog = false
                }) { Text("Kaydet") }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) { Text("İptal") }
            }
        )
    }

    if (showResetDialog) {
        DeleteConfirmDialog(
            onDismissDelete = { showResetDialog = false },
            onConfirmDelete = {
                viewModel.resetAllData()
                showResetDialog = false
                Toast.makeText(context, "Tüm veriler temizlendi!", Toast.LENGTH_SHORT).show()
            },
            itemName = "TÜM VERİLERİ",
            itemType = "kalıcı olarak",
            warningMessage = "Tüm hedefler ve alışkanlıklar silinecek. Emin misin?"
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopBar(title = "Ayarlar", text = "Uygulamanı Kişiselleştir.")
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            SectionTitle("Hesap")
            ProfileHeader(
                userName = userName,
                onEditClick = { showNameDialog = true }
            )

            SectionTitle("Genel")
            SettingsCard {
                SettingsNavigationItem(
                    icon = Icons.Default.Notifications,
                    title = "Bildirimler",
                    onClick = { openNotificationSettings(context) }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))

                SettingsSwitchItem(
                    icon = Icons.Default.DarkMode,
                    title = "Karanlık Mod",
                    checked = isDarkMode ?: systemInDarkTheme,
                    onCheckedChange = { viewModel.toggleDarkMode(it) }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))

                SettingsSwitchItem(
                    icon = Icons.AutoMirrored.Filled.VolumeUp,
                    title = "Ses Efektleri",
                    checked = isSoundEnabled,
                    onCheckedChange = { viewModel.toggleSound(it) }
                )
            }

            SectionTitle("Destek")
            SettingsCard {
                SettingsNavigationItem(
                    icon = Icons.Default.Star,
                    title = "Uygulamayı Oyla",
                    onClick = { openWebLink(context, "https://play.google.com/store/apps") }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))

                SettingsNavigationItem(
                    icon = Icons.Default.Mail,
                    title = "Bize Ulaşın",
                    onClick = { sendEmail(context) }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))

                SettingsNavigationItem(
                    icon = Icons.Default.PrivacyTip,
                    title = "Gizlilik Politikası",
                    onClick = { openWebLink(context, "https://www.google.com/policies/privacy") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showResetDialog = true }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Verileri Sıfırla / Çıkış Yap",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

fun openNotificationSettings(context: Context) {
    val intent = Intent().apply {
        action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    }
    context.startActivity(intent)
}

fun openWebLink(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "Tarayıcı bulunamadı", Toast.LENGTH_SHORT).show()
    }
}

fun sendEmail(context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        this.data = "mailto:destek@goaltracker.com".toUri()
        putExtra(Intent.EXTRA_SUBJECT, "GoalTracker Geri Bildirim")
    }
    try {
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "E-posta uygulaması bulunamadı", Toast.LENGTH_SHORT).show()
    }
}