package com.example.medimate.mainViews

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.example.medimate.ui.theme.White
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.healme.R
import com.example.medimate.navigation.Screen
import com.example.medimate.ui.theme.Grey2
import com.example.medimate.ui.theme.MediMateTheme
import com.example.medimate.ui.theme.PurpleGrey2
import com.example.medimate.ui.theme.PurpleMain


@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(false) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasPermission = isGranted }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    Surface(color=White) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.medimate_logo),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = { navController.navigate(Screen.Login.route) },
            modifier = Modifier.width(180.dp),
            colors=ButtonDefaults.buttonColors(
                containerColor = PurpleMain,
                contentColor = White,
                disabledContainerColor = PurpleGrey2,
                disabledContentColor = White)
        ) {
            Text(stringResource(R.string.Login), color = MaterialTheme.colorScheme.onPrimary)
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = { navController.navigate(Screen.Register.route) },
            modifier = Modifier.width(180.dp),
            colors=ButtonDefaults.buttonColors(
                containerColor = PurpleMain,
                contentColor = White,
                disabledContainerColor = PurpleGrey2,
                disabledContentColor = White
        ) ){
            Text(stringResource(R.string.Register), color = MaterialTheme.colorScheme.onSecondary)
        }
    }

//        if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            Toast.makeText(context, "Notification permission required!", Toast.LENGTH_SHORT).show()
        }
}

@Preview(showSystemUi = true)
@Composable
fun MainScreenPreview() {
    MediMateTheme {
        MainScreen(navController = rememberNavController())
    }
}

