package com.example.medimate.ui.theme

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun MediMateButton(
    text:String,
    onClick: ()-> Unit,
    modifier: Modifier = Modifier,
    enabled:Boolean=true,
    icon:ImageVector?=null,
    loading:Boolean = false
    ){
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled&&!loading,
        colors = ButtonDefaults.buttonColors(
            containerColor = PurpleMain,
            contentColor = White,
            disabledContainerColor = PurpleLight.copy(alpha = 0.6f),
            disabledContentColor = Grey2.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(50),
        elevation = ButtonDefaults.buttonElevation(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            when {
                loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Processing...")
                }
                icon != null -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = text)
                }
                else -> {
                    Text(text = text)
                }
            }
        }
    }
}