package com.example.medimate.user.reviews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import com.example.medimate.firebase.AuthManager
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import com.example.medimate.ui.theme.MediMateTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medimate.firebase.doctor.Review
import com.example.medimate.ui.theme.MediMateButton
import com.example.medimate.ui.theme.PurpleDark
import com.example.medimate.ui.theme.PurpleGrey2
import com.example.medimate.ui.theme.PurpleLight
import com.example.medimate.ui.theme.PurpleLight2
import com.example.medimate.ui.theme.PurpleMain
import com.example.medimate.user.ModelNavDrawerUser


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorReviewScreen(navController: NavController,selectedDoctorId: String){
    val viewModel = viewModel<ReviewModel>()
    val doctor by viewModel.selectedDoctor.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val reviewAdded by viewModel.reviewAdded.collectAsState()

    var rating by remember { mutableStateOf(0.0) }
    var text by remember { mutableStateOf("") }

    val currentUser = AuthManager.getCurrentUser()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    LaunchedEffect(selectedDoctorId) {
            viewModel.loadDoctorById(selectedDoctorId)

    }
    ModelNavDrawerUser(navController,drawerState) {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Doctor Reviews") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    doctor?.let {
                        Text(
                            text = "Dr. ${it.name} ${it.surname}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = PurpleDark
                        )
                        Text(
                            text = "Specialization: ${it.specialisation}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PurpleDark
                        )
                        Row {
                            Text(
                                text = "Rating: ${"%.1f".format(it.rating)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PurpleDark
                            )
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Rating",
                                tint = Color.Yellow,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (currentUser != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Leave a Review",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Text("Rating (0-5)")
                                Slider(
                                    value = rating.toFloat(),
                                    onValueChange = { rating = it.toDouble() },
                                    colors = SliderDefaults.colors( thumbColor = PurpleLight2,
                                        activeTrackColor = PurpleMain,
                                        inactiveTrackColor =  PurpleGrey2,
                                    ),
                                    valueRange = 0f..5f,
                                    steps = 4,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                Text(text = "%.1f".format(rating))

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Your Review")
                                OutlinedTextField(
                                    value = text,
                                    onValueChange = { text = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Write your review here...") },
                                    maxLines = 3
                                )

                                Spacer(modifier = Modifier.height(16.dp))
                                MediMateButton(
                                    "Submit Review",
                                    onClick = {
                                        if (selectedDoctorId.isNotEmpty() && currentUser.id.isNotEmpty()) {
                                            val review = Review(
                                                rate = rating,
                                                text = text,
                                                userId = currentUser.id,
                                                doctorId = selectedDoctorId
                                            )
                                            viewModel.addReview(review)
                                        }
                                    },
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }

                        if (reviewAdded) {
                            LaunchedEffect(Unit) {
                                viewModel.resetReviewAdded()
                            }
                            Text(
                                text = "Review submitted successfully!",
                                color = PurpleMain,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    } else {
                        Text("Please log in to leave a review")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Reviews",
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (reviews.isNullOrEmpty()) {
                        Text("No reviews yet")
                    } else {
                        LazyColumn {
                            items(reviews!!) { review ->
                                ReviewItem(review = review)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Rating: ${"%.1f".format(review.rate)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Rating",
                    tint = Color.Yellow,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = review.text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
@Preview(showSystemUi = true)
@Composable
fun DoctorsViewPreview() {
    MediMateTheme {
        DoctorReviewScreen(navController = rememberNavController(),"122wejuhfbwke")
    }
}
