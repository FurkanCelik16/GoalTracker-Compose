package com.example.goaltracker.presentation.goals.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goaltracker.core.common.ui.components.Input
import com.example.goaltracker.core.model.Goal
import com.example.goaltracker.core.model.GoalType
import com.example.goaltracker.presentation.goals.components.goalIcons
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalSheet(
    onDismiss: () -> Unit,
    onSave: (Goal) -> Unit,
    isEditMode: Boolean = false,
    initialGoal: Goal? = null
) {
    var title by remember { mutableStateOf(initialGoal?.title ?: "") }
    var selectedType by remember { mutableStateOf(initialGoal?.type ?: GoalType.RECURRING) }
    var targetAmount by remember { mutableStateOf(initialGoal?.targetAmount?.toInt()?.toString() ?: "") }
    var selectedIconIndex by remember { mutableIntStateOf(initialGoal?.iconIndex ?: 0) }
    var selectedDate by remember { mutableStateOf(initialGoal?.endDate ?: LocalDate.now().plusMonths(1)) }

    var isDecimalAllowed by remember {
        mutableStateOf(initialGoal?.targetAmount?.let { it % 1 != 0f } ?: false)
    }

    var showError by remember { mutableStateOf(false) }
    val shakeOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    var showDatePicker by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = surfaceColor,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = shakeOffset.value.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                },
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isEditMode) "Hedefi Düzenle" else "Yeni Hedef",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = onSurfaceColor
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Hedef Tipi",
                    style = MaterialTheme.typography.labelSmall,
                    color = onSurfaceVariantColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TypeSelectionCard(
                        title = "Her Gün",
                        icon = Icons.Default.Repeat,
                        isSelected = selectedType == GoalType.RECURRING,
                        onClick = { selectedType = GoalType.RECURRING },
                        modifier = Modifier.weight(1f)
                    )
                    TypeSelectionCard(
                        title = "Birikim",
                        icon = Icons.Default.AccountBalance,
                        isSelected = selectedType == GoalType.ACCUMULATIVE,
                        onClick = { selectedType = GoalType.ACCUMULATIVE },
                        modifier = Modifier.weight(1f)
                    )
                    TypeSelectionCard(
                        title = "Tek Sefer",
                        icon = Icons.Default.TaskAlt,
                        isSelected = selectedType == GoalType.BINARY,
                        onClick = { selectedType = GoalType.BINARY },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Column {
                Input(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) showError = false
                    },
                    placeholder = "Hedefin Adı",
                    imeAction = ImeAction.Next
                )
                if (showError && title.isBlank()) {
                    Text(
                        text = "Lütfen bir isim girin",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }
            }

            AnimatedVisibility(visible = selectedType != GoalType.BINARY) {
                Column {
                    Input(
                        value = targetAmount,
                        onValueChange = { input ->
                            if (input.isNotBlank()) showError = false
                            if (isDecimalAllowed) {
                                if (input.count { it == '.' } <= 1 && input.all { it.isDigit() || it == '.' }) {
                                    targetAmount = input
                                }
                            } else {
                                if (input.all { it.isDigit() }) {
                                    targetAmount = input
                                }
                            }
                        },
                        placeholder = if (selectedType == GoalType.RECURRING) "Günlük Miktar" else "Toplam Hedef",
                        keyboardType = if (isDecimalAllowed) KeyboardType.Decimal else KeyboardType.Number,
                        imeAction = ImeAction.Done,
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                    )
                    if (showError && targetAmount.isBlank()) {
                        Text(
                            text = "Lütfen bir miktar girin",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, start = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Küsüratlı Değer",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = onSurfaceColor
                            )
                            Text(
                                text = "Örn: 2.5 Litre, 1.5 Km",
                                style = MaterialTheme.typography.labelSmall,
                                color = onSurfaceVariantColor
                            )
                        }
                        Switch(
                            checked = isDecimalAllowed,
                            onCheckedChange = {
                                isDecimalAllowed = it
                                if (!it && targetAmount.contains(".")) {
                                    targetAmount = targetAmount.substringBefore(".")
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = primaryColor
                            )
                        )
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Bir İkon Seç",
                    style = MaterialTheme.typography.labelSmall,
                    color = onSurfaceVariantColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(goalIcons.size) { index ->
                        val (icon, color) = goalIcons[index]
                        val isSelected = selectedIconIndex == index
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) color else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedIconIndex = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else onSurfaceVariantColor
                            )
                        }
                    }
                }
            }

            val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.forLanguageTag("tr"))
            OutlinedCard(
                onClick = { showDatePicker = true },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = primaryColor)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Bitiş Tarihi", style = MaterialTheme.typography.labelSmall)
                        Text(
                            text = selectedDate.format(dateFormatter),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Button(
                onClick = {
                    val finalAmount = if (selectedType == GoalType.BINARY) 1f else targetAmount.toFloatOrNull()

                    if (title.isNotBlank() && finalAmount != null) {
                        onSave(
                            Goal(
                                id = initialGoal?.id ?: 0,
                                title = title.trim(),
                                targetAmount = finalAmount,
                                currentAmount = initialGoal?.currentAmount ?: 0f,
                                iconIndex = selectedIconIndex,
                                endDate = selectedDate,
                                type = selectedType
                            )
                        )
                    } else {
                        showError = true
                        scope.launch {
                            shakeOffset.animateTo(
                                targetValue = 0f,
                                animationSpec = keyframes {
                                    durationMillis = 400
                                    0f at 0
                                    (-10f) at 50
                                    10f at 100
                                    (-10f) at 150
                                    10f at 200
                                    0f at 400
                                }
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = if (isEditMode) "KAYDET" else "HEDEFİ BAŞLAT 🚀")
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate =
                            Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    showDatePicker = false
                }) {
                    Text("SEÇ", fontWeight = FontWeight.Bold, color = primaryColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("İPTAL")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = primaryColor,
                    todayDateBorderColor = primaryColor
                ),
                showModeToggle = false
            )
        }
    }
}

@Composable
fun TypeSelectionCard(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(
            alpha = 0.5f
        )
    val contentColor =
        if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor =
        if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)

    Column(
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            ),
            color = contentColor,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}