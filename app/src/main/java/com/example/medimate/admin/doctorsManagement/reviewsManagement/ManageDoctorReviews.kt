package com.example.medimate.admin.doctorsManagement.reviewsManagement

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medimate.firebase.review.Review
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageDoctorReviewsScreen(navController: NavController,doctorId:String?){
    val viewModel = viewModel<ManageDoctorReviewsViewModel>()
    val reviews by viewModel.reviews.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isDeleting by viewModel.isDeleting.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(doctorId) {
        doctorId?.let { viewModel.loadReviews(it) }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Manage Reviews") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                if (reviews.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No reviews available")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(reviews) { review ->
                            ReviewItem(
                                review = review,
                                onDelete = {
                                    scope.launch {
                                        try {
                                            viewModel.deleteReview(doctorId!!, review)
                                            snackbarHostState.showSnackbar("Review deleted")
                                        } catch (e: Exception) {
                                            snackbarHostState.showSnackbar("Error deleting review: ${e.message}")
                                        }
                                    }
                                },
                                isDeleting = isDeleting
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewItem(
    review: Review,
    onDelete: () -> Unit,
    isDeleting: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Rating: ${review.rate}/5",
                    style = MaterialTheme.typography.titleMedium
                )

                if (isDeleting) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete review"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = review.text)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "User ID: ${review.userId}",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

