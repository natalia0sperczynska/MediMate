package com.example.medimate.user
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import com.example.healme.R
import com.example.medimate.navigation.Screen
import com.example.medimate.ui.theme.Black
import com.example.medimate.ui.theme.LightGrey
import com.example.medimate.ui.theme.PurpleGrey
import com.example.medimate.ui.theme.PurpleGrey2
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
@Composable
fun DrawerHeader(){
    val auth = FirebaseAuth.getInstance()
    val name= auth.currentUser?.displayName
    val email = auth.currentUser?.email
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(PurpleGrey.copy(alpha = 0.1f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(LightGrey, CircleShape)
        ) {
            Icon(
                painter = painterResource(R.drawable.profile_pic),
                contentDescription = "Profile",
                modifier = Modifier.size(32.dp).align(Alignment.Center)
            )
        }
        name?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                color = Black
            )
        }

        email?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = Black.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = LightGrey)
    }
    }

@Composable
fun ModelNavDrawerUser(navController: NavController,
                       drawerState: DrawerState,
                       content: @Composable () -> Unit){
    val scope = rememberCoroutineScope()
    val drawerAnimatable = remember { Animatable(0f)}

    LaunchedEffect(drawerState.isOpen) {
        if (drawerState.isOpen) {
            drawerAnimatable.animateTo(1f, animationSpec = tween(300))
        } else {
            drawerAnimatable.animateTo(0f, animationSpec = tween(300))
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = PurpleGrey.copy(alpha = 0.3f),
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.background(PurpleGrey).offset(x = (1 - drawerAnimatable.value) * (-300.dp))
            ) {
                DrawerHeader()
                NavigationDrawerItem(
                    label = {Text(text="Menu", modifier = Modifier.padding(16.dp), color = Black)},
                    selected = false,
                    onClick = { navController.navigate(Screen.MainUser.route) }
                )
                HorizontalDivider(color = LightGrey)
                NavigationDrawerItem(
                    label = { Text(text = "Set New Appointment", color = Black) },
                    selected = false,
                    onClick = { navController.navigate(Screen.Appointments.route) }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Doctors", color = Black) },
                    selected = false,
                    onClick = { navController.navigate(Screen.Doctors.route) }
                )
                NavigationDrawerItem(
                    label = { Text(text = "My Appointments", color = Black) },
                    selected = false,
                    onClick = { navController.navigate(Screen.FutureAppointments.route) }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Chat", color = Black) },
                    selected = false,
                    onClick = { navController.navigate(Screen.ChatSelection.createRoute(false)) }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Appointments History", color = Black) },
                    selected = false,
                    onClick = { navController.navigate(Screen.AppointmentsHistory.route) }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Update Data", color = Black) },
                    selected = false,
                    onClick = { navController.navigate(Screen.UpdateData.route) }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Logout", color = Black) },
                    selected = false,
                    onClick = { navController.navigate(Screen.Login.route) }
                )

            }
        }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = { scope.launch { drawerState.open() } },
                    modifier = Modifier.background(Color.White, shape = MaterialTheme.shapes.small)

                ) {
                    Icon(Icons.Default.Menu, contentDescription = "Open Menu", tint = Black)
                }
            }
            content()
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(20.dp)
                    .background(LightGrey.copy(alpha = 0.5f))
                    .clickable { scope.launch { drawerState.open() } }
            )
        }
    }
}