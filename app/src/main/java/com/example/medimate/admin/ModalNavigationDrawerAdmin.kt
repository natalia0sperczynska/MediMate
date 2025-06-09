package com.example.medimate.admin

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
import com.example.medimate.user.DrawerHeader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ModelNavDrawerAdmin(navController: NavController,
                       drawerState: DrawerState,
                       scope: CoroutineScope,
                       content: @Composable () -> Unit){
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
                modifier = Modifier.background(PurpleGrey2).offset(x = (1 - drawerAnimatable.value) * (-300.dp))
            ) {
                DrawerHeader()
                NavigationDrawerItem(
                    label = {Text(text="Menu", modifier = Modifier.padding(16.dp), color = Black)},
                    selected = false,
                    onClick = { navController.navigate(Screen.MainAdmin.route) }
                )
                HorizontalDivider(color = LightGrey)
                NavigationDrawerItem(
                    label = { Text(text = "Add Doctors", color = Black) },
                    selected = false,
                    onClick = { navController.navigate(Screen.AddDoctor.route) }
                )

                NavigationDrawerItem(
                    label = { Text(text = "Manage Users", color = Black) },
                    selected = false,
                    onClick = { navController.navigate(Screen.ManageUsers.route) }
                )

                NavigationDrawerItem(
                    label = { Text(text = "Manage Doctors", color = Black) },
                    selected = false,
                    onClick = { navController.navigate(Screen.DoctorsAdmin.route) }
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