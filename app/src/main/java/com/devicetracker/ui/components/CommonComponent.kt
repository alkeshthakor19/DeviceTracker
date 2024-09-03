package com.devicetracker.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun LabelText(labelText: String){
    Text(text = labelText, fontSize = 18.sp, fontStyle = FontStyle.Italic, fontWeight = FontWeight.SemiBold, color = Color.Gray)
}

@Composable
fun BodyText(bodyText: String){
    Text(text = bodyText,style = MaterialTheme.typography.bodyLarge, fontStyle = FontStyle.Italic, fontSize = 18.sp,  color = Color.Black)
}

@Composable
fun BlackLabelText(labelText: String){
    Text(text = labelText, fontSize = 18.sp, fontStyle = FontStyle.Normal, fontWeight = FontWeight.SemiBold, color = Color.Black)
}