package com.example.mycalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mycalendar.screen.DayViewScreen
import com.example.mycalendar.screen.MonthViewScreen
import com.example.mycalendar.ui.theme.MyCalendarTheme
import java.time.LocalDate
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyCalendarTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "calendar") {
                    composable("calendar") {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            MonthViewScreen(
                                onDaySelected = { date -> navController.navigate("day/$date") },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                    composable("day/{date}") { backStackEntry ->
                        val date = LocalDate.parse(
                            backStackEntry.arguments?.getString("date") ?: return@composable
                        )
                        DayViewScreen(
                            date = date,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
