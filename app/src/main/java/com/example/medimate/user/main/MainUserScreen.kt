package com.example.medimate.user.main
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medimate.firebase.user.UserDAO
import com.example.medimate.navigation.Screen
import com.example.medimate.ui.theme.Black
import com.example.medimate.ui.theme.MediMateTheme
import com.example.medimate.user.ModelNavDrawerUser
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MainUserScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val userId = auth.currentUser?.uid
    val firestoreClass = UserDAO()
    var userName by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        if (userId != null) {
            coroutineScope.launch {
                try {
                    val data = firestoreClass.loadUserData(userId)
                    userName = (data?.getValue("name") ?: "User").toString()
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to load user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    ModelNavDrawerUser(navController,drawerState) {
        ScreenModel(navController, userId.toString(), userName, drawerState)
    }
}

@Composable
fun ScreenModel(navController: NavController, userId: String, userName: String, drawerState: DrawerState) {

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Good to see you again, $userName!", style = MaterialTheme.typography.headlineMedium, color = Black)
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navController.navigate(Screen.UpdateData.route) }) {
                Text("Update Data")
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Main.route) { inclusive = true }
                }
            }) {
                Text("Logout")
            }
        }

}

@Preview(showSystemUi = true)
@Composable
fun MainUserScreenPreview() {
    MediMateTheme {
       ScreenModel(navController = rememberNavController(), userId = "123", userName = "John", drawerState = rememberDrawerState(DrawerValue.Closed))
    }
}

