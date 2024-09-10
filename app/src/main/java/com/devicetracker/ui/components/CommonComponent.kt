package com.devicetracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

@Composable
fun NoDataMessage(){
    Column(
        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = "No Data!!", style = MaterialTheme.typography.titleLarge)
    }
}