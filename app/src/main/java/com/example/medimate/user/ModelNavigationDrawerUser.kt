package com.example.medimate.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medimate.navigation.Screen
import com.example.medimate.ui.theme.Black
import com.example.medimate.ui.theme.LightGrey
import com.example.medimate.ui.theme.PurpleGrey
import com.example.medimate.ui.theme.PurpleGrey2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ModelNavDrawerUser(navController: NavController,
                       drawerState: DrawerState,
                       scope: CoroutineScope,
                       content: @Composable () -> Unit){
    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = PurpleGrey.copy(alpha = 0.3f),
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.background(PurpleGrey2)
            ) {
                Text("Menu", modifier = Modifier.padding(16.dp), color = Black)
                HorizontalDivider(color = LightGrey)
                NavigationDrawerItem(
                    label = { Text(text = "Appointments", color = Black) },
                    selected = false,
                    onClick = { navController.navigate(Screen.Appointments.route) }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Doctors", color = Black) },
                    selected = false,
                    onClick = { navController.navigate(Screen.Doctors.route) }
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