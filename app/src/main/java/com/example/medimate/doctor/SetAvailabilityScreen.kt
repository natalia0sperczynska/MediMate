package com.example.medimate.doctor
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medimate.firebase.Term
import com.example.medimate.ui.theme.MediMateTheme
import java.util.Locale

data class TermUI(
    val term: Term,
    var isAvailable: Boolean = false
)

data class DayAvailabilityUI(
    val day: String,
    val slots: List<TermUI>
)

@Composable
fun SetAvailabilityScreen(
    navController: NavController,
    viewModel: SetAvailabilityModel = SetAvailabilityModel()) {
    Column(modifier = Modifier.padding(16.dp)) {
        viewModel.weekAvailability.forEach { dayAvailability ->
            Text(text = dayAvailability.day.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.ROOT
                ) else it.toString()
            }, style = MaterialTheme.typography.titleMedium)
            LazyRow {
                itemsIndexed(dayAvailability.slots) { index, termUI ->
                    val bgColor = if (termUI.isAvailable) Color.Green else Color.Gray
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable {
                                viewModel.toggleAvailability(dayAvailability.day, index)
                            }
                            .background(bgColor, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text("${termUI.term.startTime}-${termUI.term.endTime}", color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            viewModel.saveAvailability("GNGmDFTZZIhAYA2zRQmOljE9UoF3")
        }) {
            Text("Save Availability")
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun SetAvailabilityPreview() {
    MediMateTheme {
        SetAvailabilityScreen(navController = rememberNavController())
    }
}