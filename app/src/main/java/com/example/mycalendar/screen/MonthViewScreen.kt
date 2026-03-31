package com.example.mycalendar.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.activity.ComponentActivity
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mycalendar.data.model.Event
import com.example.mycalendar.viewmodel.CalendarUiState
import com.example.mycalendar.viewmodel.CalendarUiState.Displaying
import com.example.mycalendar.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthViewScreen(
    onDaySelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    CalendarScreen(
        uiState = uiState,
        onDateSelected = { date ->
            viewModel.onDateSelected(date)
            onDaySelected(date)
        },
        onMonthChanged = viewModel::onMonthChanged,
        modifier = modifier
    )
}

@Composable
fun CalendarScreen(
    uiState: CalendarUiState,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChanged: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is CalendarUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is Displaying -> {
            CalendarContent(
                uiState = uiState,
                onDateSelected = onDateSelected,
                onMonthChanged = onMonthChanged,
                modifier = modifier
            )
        }
        is CalendarUiState.Error -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = uiState.errorMessage, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun CalendarContent(
    uiState: Displaying,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChanged: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentYearMonth = YearMonth.of(uiState.currentMonth.year, uiState.currentMonth.month)
    var showMonthYearPicker by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        MonthHeader(
            yearMonth = currentYearMonth,
            onPreviousMonth = { onMonthChanged(uiState.currentMonth.minusMonths(1)) },
            onNextMonth = { onMonthChanged(uiState.currentMonth.plusMonths(1)) },
            onTodayClicked = { onMonthChanged(LocalDate.now()) },
            onMonthYearClicked = { showMonthYearPicker = true }
        )

        if (showMonthYearPicker) {
            MonthYearPickerDialog(
                currentYearMonth = currentYearMonth,
                onYearMonthSelected = { onMonthChanged(it.atDay(1)) },
                onDismiss = { showMonthYearPicker = false }
            )
        }
        DayOfWeekRow()
        HorizontalDivider()
        CalendarGrid(
            yearMonth = currentYearMonth,
            selectedDate = uiState.selectedDate,
            events = uiState.events,
            onDateSelected = onDateSelected,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun MonthHeader(
    yearMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onTodayClicked: () -> Unit,
    onMonthYearClicked: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onPreviousMonth) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous month")
            }
            Text(
                text = "${yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${yearMonth.year}",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(160.dp)
                    .clickable(onClick = onMonthYearClicked)
            )
            IconButton(onClick = onNextMonth) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next month")
            }
        }
        TextButton(onClick = onTodayClicked) {
            Text("Today")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthYearPickerDialog(
    currentYearMonth: YearMonth,
    onYearMonthSelected: (YearMonth) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedYear by remember { mutableIntStateOf(currentYearMonth.year) }
    val months = java.time.Month.entries.toList()

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { selectedYear-- }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous year")
                    }
                    Text(text = selectedYear.toString(), style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = { selectedYear++ }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next year")
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    items(months) { month ->
                        val isSelected = month == currentYearMonth.month && selectedYear == currentYearMonth.year
                        val bgColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(bgColor)
                                .clickable {
                                    onYearMonthSelected(YearMonth.of(selectedYear, month))
                                    onDismiss()
                                }
                                .wrapContentSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                                style = MaterialTheme.typography.bodyMedium,
                                color = textColor,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayOfWeekRow() {
    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    Row(modifier = Modifier.fillMaxWidth()) {
        daysOfWeek.forEachIndexed { index, day ->
            val isWeekend = index == 0 || index == 6
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = if (isWeekend) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CalendarGrid(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    events: List<Event>,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val firstDayOfMonth = yearMonth.atDay(1)
    val startOffset = firstDayOfMonth.dayOfWeek.value % 7
    val daysInMonth = yearMonth.lengthOfMonth()
    val cells = List(startOffset) { null } + (1..daysInMonth).map { yearMonth.atDay(it) }
    val weeks = cells.chunked(7) { row -> row + List(7 - row.size) { null } }
    val eventsByDate = events.groupBy { it.date }

    Column(modifier = modifier.fillMaxSize()) {
        weeks.forEach { week ->
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                week.forEach { date ->
                    DayCell(
                        date = date,
                        isSelected = date == selectedDate,
                        isToday = date == LocalDate.now(),
                        eventCount = if (date != null) eventsByDate[date]?.size ?: 0 else 0,
                        onDateSelected = onDateSelected,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            HorizontalDivider()
        }
    }
}

@Composable
fun DayCell(
    date: LocalDate?,
    isSelected: Boolean,
    isToday: Boolean,
    eventCount: Int,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val isWeekend = date?.dayOfWeek?.let {
        it == java.time.DayOfWeek.SATURDAY || it == java.time.DayOfWeek.SUNDAY
    } ?: false
    val cellBgColor = if (isWeekend) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                      else MaterialTheme.colorScheme.surface
    val borderColor = MaterialTheme.colorScheme.outlineVariant

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(cellBgColor)
            .border(width = 0.5.dp, color = borderColor)
            .then(if (date != null) Modifier.clickable { onDateSelected(date) } else Modifier),
        contentAlignment = Alignment.TopCenter
    ) {
        if (date != null) {
            val circleColor = when {
                isSelected -> MaterialTheme.colorScheme.primary
                isToday -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surface.copy(alpha = 0f)
            }
            val textColor = when {
                isSelected -> MaterialTheme.colorScheme.onPrimary
                isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                else -> if (isWeekend) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(circleColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = textColor
                    )
                }
                if (eventCount > 0) {
                    Row(
                        modifier = Modifier.padding(top = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        repeat(eventCount.coerceAtMost(3)) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
            }
        }
    }
}
