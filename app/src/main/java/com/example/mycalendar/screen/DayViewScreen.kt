package com.example.mycalendar.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mycalendar.data.model.Event
import com.example.mycalendar.viewmodel.DayUiState
import com.example.mycalendar.viewmodel.DayViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale

private val HourHeight = 64.dp
private val TimeColumnWidth = 52.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayViewScreen(
    date: LocalDate,
    onBack: () -> Unit,
    viewModel: DayViewModel = viewModel(factory = DayViewModel.Factory(date))
) {
    val uiState by viewModel.uiState.collectAsState()
    val events = if (uiState is DayUiState.Displaying) (uiState as DayUiState.Displaying).events
                 else emptyList()

    val isToday = date == LocalDate.now()
    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    LaunchedEffect(Unit) {
        val targetHour = if (isToday) (LocalTime.now().hour - 1).coerceAtLeast(0) else 7
        with(density) { scrollState.scrollTo((HourHeight * targetHour).toPx().toInt()) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${date.dayOfMonth}, ${date.year}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Hour rows
            Column(modifier = Modifier.fillMaxWidth()) {
                repeat(24) { hour ->
                    HourRow(hour = hour)
                }
            }

            // Event blocks
            events.forEach { event ->
                EventBlock(event = event)
            }

            // Current time indicator
            if (isToday) {
                CurrentTimeIndicator()
            }
        }
    }
}

@Composable
fun EventBlock(event: Event) {
    val minuteHeight = HourHeight / 60f
    val startMinutes = event.startTime.hour * 60 + event.startTime.minute
    val endMinutes = event.endTime.hour * 60 + event.endTime.minute
    val yOffset = minuteHeight * startMinutes
    val blockHeight = (minuteHeight * (endMinutes - startMinutes)).coerceAtLeast(24.dp)

    Box(
        modifier = Modifier
            .offset(y = yOffset)
            .padding(start = TimeColumnWidth + 4.dp, end = 4.dp)
            .fillMaxWidth()
            .height(blockHeight)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Text(
            text = event.title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun HourRow(hour: Int) {
    val label = when {
        hour == 0 -> "12 AM"
        hour < 12 -> "$hour AM"
        hour == 12 -> "12 PM"
        else -> "${hour - 12} PM"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(HourHeight),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            modifier = Modifier
                .width(TimeColumnWidth)
                .padding(top = 2.dp, end = 8.dp),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}

@Composable
fun CurrentTimeIndicator() {
    val now = LocalTime.now()
    val minutesFromMidnight = now.hour * 60 + now.minute
    val totalMinutesInDay = 24 * 60
    val fraction = minutesFromMidnight.toFloat() / totalMinutesInDay
    val totalHeight = HourHeight * 24
    val yOffset = totalHeight * fraction

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = yOffset)
    ) {
        Box(
            modifier = Modifier
                .padding(start = TimeColumnWidth - 4.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.error)
                .align(Alignment.CenterStart)
        )
        HorizontalDivider(
            modifier = Modifier
                .padding(start = TimeColumnWidth)
                .align(Alignment.CenterStart),
            color = MaterialTheme.colorScheme.error,
            thickness = 1.5.dp
        )
    }
}
