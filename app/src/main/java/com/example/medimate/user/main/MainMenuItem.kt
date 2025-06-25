package com.example.medimate.user.main

import androidx.compose.ui.graphics.vector.ImageVector
/**
 * Represents a single item in the main menu with an icon and a title.
 *
 * @property icon The icon displayed for this menu item.
 * @property title The text label for this menu item.
 */
data class MainMenuItem(val icon: ImageVector, val title: String)