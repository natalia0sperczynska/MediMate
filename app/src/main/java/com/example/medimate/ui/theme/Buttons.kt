package com.example.medimate.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

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
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = PurpleMain,
            contentColor = White,
            disabledContainerColor = PurpleLight.copy(alpha = 0.6f),
            disabledContentColor = Grey2.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(50),
        elevation = ButtonDefaults.buttonElevation()
    ) {
        Text(text = text)
    }


}