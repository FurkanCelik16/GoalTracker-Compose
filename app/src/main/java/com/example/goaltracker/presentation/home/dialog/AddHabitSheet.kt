package com.example.goaltracker.presentation.home.dialog

import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goaltracker.core.common.ui.components.ModernDropdown
import com.example.goaltracker.core.common.ui.components.Input
import com.example.goaltracker.core.common.util.SmartSheetBackHandler
import com.example.goaltracker.core.common.util.disableVerticalSwipe
import com.example.goaltracker.core.model.HabitDifficulty
import com.example.goaltracker.core.model.HabitType
import com.example.goaltracker.core.model.Period
import com.example.goaltracker.core.model.TimeOfDay
import com.example.goaltracker.presentation.home.components.HabitChip
import com.example.goaltracker.core.theme.ErrorColor
import com.example.goaltracker.core.theme.SuccessColor
import com.example.goaltracker.core.theme.WarningColor
import com.example.goaltracker.core.theme.Poppins
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitSheet(
    onDismiss: () -> Unit,
    onSave: (String, String, Period, HabitDifficulty, HabitType, TimeOfDay, List<Int>, Int, Int) -> Unit,
    isEditMode: Boolean = false,
    initialName: String = "",
    initialCategory: String = "",
    initialPeriod: Period? = null,
    initialDifficulty: HabitDifficulty = HabitDifficulty.MEDIUM,
    initialTimeOfDay: TimeOfDay = TimeOfDay.ANYTIME,
    initialType: HabitType = HabitType.POSITIVE,
    initialSelectedDays: List<Int> = emptyList(),
    initialPeriodInterval: Int = 1,
    initialTargetCount: Int = 1
) {
    var name by remember { mutableStateOf(initialName) }
    var category by remember { mutableStateOf(initialCategory) }
    var period by remember { mutableStateOf(initialPeriod ?: Period.DAILY) }
    var difficulty by remember { mutableStateOf(initialDifficulty) }
    var timeOfDay by remember { mutableStateOf(initialTimeOfDay) }
    var type by remember { mutableStateOf(initialType) }
    var selectedDays by remember { mutableStateOf(initialSelectedDays) }
    var periodIntervalString by remember { mutableStateOf(initialPeriodInterval.toString()) }
    var targetCount by remember { mutableIntStateOf(initialTargetCount) }

    var showError by remember { mutableStateOf(false) }
    val shakeOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val isKeyboardOpen = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    SmartSheetBackHandler(keyboardController, focusManager)

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null,
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = false
        )
    ) {
        BackHandler(enabled = true) {
            if (isKeyboardOpen) {
                keyboardController?.hide()
                focusManager.clearFocus()
            } else {
                onDismiss()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = shakeOffset.value.dp).disableVerticalSwipe()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp,bottom = 24.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
                .padding(bottom = 40.dp)
        ) {
            Text(
                text = if (isEditMode) "Alışkanlığı Düzenle" else "Yeni Alışkanlık",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Input(
                value = name,
                onValueChange = {
                    name = it
                    if (it.isNotBlank()) showError = false
                },
                placeholder = "Alışkanlık Adı (Örn: Kitap Oku)",
                imeAction = ImeAction.Next,
            )
            if (showError && name.isBlank()) {
                Text(
                    text = "Lütfen bir isim girin",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ModernDropdown(
                value = category,
                onValueChange = {
                    category = it
                    if (it.isNotBlank()) showError = false
                },
                options = listOf("Sağlık", "Kişisel Gelişim", "Sosyal", "Disiplin", "Spor", "Eğitim", "Diğer"),
                placeholder = "Kategori Seç"
            )
            if (showError && category.isBlank()) {
                Text(
                    text = "Lütfen bir kategori seçin",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            SheetSection(title = "Ne Sıklıkla?", icon = Icons.Outlined.Repeat) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(Period.DAILY to "Günlük", Period.WEEKLY to "Haftalık", Period.CUSTOM to "Özel").forEach { (p, label) ->
                        HabitChip(
                            selected = period == p,
                            label = label,
                            onClick = {
                                period = p
                                if (p != Period.DAILY) type = HabitType.POSITIVE
                            }
                        )
                    }
                }
                if (period == Period.DAILY) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Günde kaç kez?",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (targetCount > 1) {
                                Text(
                                    text = "Sayaçlı takip açılacak",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(12.dp))
                                .padding(4.dp)
                        ) {
                            IconButton(
                                onClick = { if (targetCount > 1) targetCount-- },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Text("-", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            }

                            Text(
                                text = targetCount.toString(),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 12.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            IconButton(
                                onClick = { if (targetCount < 50) targetCount++ },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Text("+", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                if (period == Period.WEEKLY) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val days = listOf("P", "S", "Ç", "P", "C", "C", "P")
                        days.forEachIndexed { index, dayInitial ->
                            val dayValue = index + 1
                            val isSelected = selectedDays.contains(dayValue)
                            val bgColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest
                            val txtColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(bgColor)
                                    .clickable { selectedDays = if (isSelected) selectedDays - dayValue else selectedDays + dayValue }
                            ) {
                                Text(text = dayInitial, color = txtColor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                if (period == Period.CUSTOM) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = periodIntervalString,
                        onValueChange = {
                            if (it.all { c -> c.isDigit() } && it.length <= 3) periodIntervalString = it
                        },
                        label = { Text("Tekrar Aralığı") },
                        suffix = { Text("günde bir") },

                        singleLine = true,
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onKeyEvent { event ->
                                if (event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                                    focusManager.clearFocus()
                                    true
                                } else {
                                    false
                                }
                            },

                        shape = RoundedCornerShape(12.dp)
                    )
                }

            }

            Spacer(modifier = Modifier.height(24.dp))

            SheetSection(title = "Zaman Dilimi", icon = Icons.Outlined.AccessTime) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val times = listOf(TimeOfDay.ANYTIME to "Hepsi", TimeOfDay.MORNING to "Sabah", TimeOfDay.AFTERNOON to "Öğle", TimeOfDay.EVENING to "Akşam")
                    times.forEach { (t, label) ->
                        HabitChip(
                            selected = timeOfDay == t,
                            label = label,
                            onClick = { timeOfDay = t },
                            modifier = Modifier.defaultMinSize(minWidth = 80.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SheetSection(title = "Hedef Tipi", icon = Icons.Outlined.Flag) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HabitChip(
                        selected = type == HabitType.POSITIVE,
                        label = "Kazan (+)",
                        color = SuccessColor,
                        onClick = { type = HabitType.POSITIVE },
                        modifier = Modifier.weight(1f)
                    )

                    val isDaily = period == Period.DAILY
                    HabitChip(
                        selected = type == HabitType.NEGATIVE,
                        label = "Bırak (-)",
                        color = if (isDaily) ErrorColor else ErrorColor.copy(alpha = 0.3f),
                        onClick = { if (isDaily) type = HabitType.NEGATIVE },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SheetSection(title = "Zorluk Seviyesi", icon = Icons.Outlined.Speed) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val diffs = listOf(HabitDifficulty.EASY to "Kolay", HabitDifficulty.MEDIUM to "Orta", HabitDifficulty.HARD to "Zor")
                    val colors = listOf(SuccessColor, WarningColor, ErrorColor)
                    diffs.forEachIndexed { index, (d, label) ->
                        HabitChip(
                            selected = difficulty == d,
                            label = label,
                            color = colors[index],
                            onClick = { difficulty = d },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && category.isNotBlank()) {
                        val interval = periodIntervalString.toIntOrNull() ?: 1
                        onSave(name.trim(), category.trim(), period, difficulty, type, timeOfDay, selectedDays, interval, targetCount)
                        onDismiss()
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
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Text(
                    text = if (isEditMode) "Değişiklikleri Kaydet" else "Alışkanlığı Oluştur",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins
                )
            }

            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.ime))
        }
    }
}

@Composable
fun SheetSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        content()
    }
}