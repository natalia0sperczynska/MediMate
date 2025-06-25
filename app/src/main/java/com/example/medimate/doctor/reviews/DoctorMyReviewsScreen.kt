package com.example.medimate.doctor.reviews

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medimate.firebase.review.Review
import com.example.medimate.firebase.review.ReviewDAO
import com.example.medimate.firebase.user.UserDAO
import com.example.medimate.ui.theme.*
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorMyReviewsScreen(navController: NavController) {
    val context = LocalContext.current
    val doctorId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val reviewDAO = remember { ReviewDAO() }
    val userDAO = remember { UserDAO() }

    var reviews by remember { mutableStateOf<List<Review>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var averageRating by remember { mutableStateOf(0.0) }
    var reviewsCount by remember { mutableStateOf(0) }

    LaunchedEffect(doctorId) {
        isLoading = true
        try {
            reviews = reviewDAO.getReviewsForDoctor(doctorId)
            averageRating = reviews.map { it.rate }.average()
            reviewsCount = reviews.size
        } catch (e: Exception) {
            Toast.makeText(context, "Error loading reviews: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Reviews") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PurpleMain,
                    titleContentColor = White,
                    navigationIconContentColor = White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            ReviewStatsCard(
                averageRating = averageRating,
                reviewsCount = reviewsCount,
                modifier = Modifier.padding(16.dp)
            )

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                reviews.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No reviews yet", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(reviews) { review ->
                            DoctorReviewItem(review = review, userDAO = userDAO)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewStatsCard(
    averageRating: Double,
    reviewsCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = PurpleLight2),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Your Rating Summary",
                style = MaterialTheme.typography.titleLarge,
                color = PurpleMain
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "%.1f".format(averageRating),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = PurpleMain
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    StarRating(rating = averageRating)
                    Text(
                        text = "$reviewsCount ${if (reviewsCount == 1) "review" else "reviews"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PurpleMain.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun DoctorReviewItem(
    review: Review,
    userDAO: UserDAO
) {
    var patientName by remember { mutableStateOf("") }

    LaunchedEffect(review.userId) {
        val user = userDAO.getUserById(review.userId)
        patientName = "${user?.name} ${user?.surname}".trim().ifEmpty { "Anonymous" }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = patientName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = review.formattedDate(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            StarRating(rating = review.rate)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = review.text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun StarRating(rating: Double, maxStars: Int = 5) {
    Row {
        val fullStars = rating.toInt()
        val hasHalfStar = rating - fullStars >= 0.5

        repeat(fullStars) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(20.dp)
            )
        }

        if (hasHalfStar) {
            Icon(
                imageVector = Icons.Default.StarHalf,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(20.dp)
            )
        }

        repeat(maxStars - fullStars - if (hasHalfStar) 1 else 0) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}