package com.devicetracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.devicetracker.ui.dashbord.assets.Project
import com.devicetracker.ui.getFontSizeByPercent

@Composable
fun LabelText(labelText: String){
    Text(text = labelText, fontSize = 18.sp, fontStyle = FontStyle.Italic, fontWeight = FontWeight.SemiBold, color = Color.Gray)
}

@Composable
fun BodyText(bodyText: String){
    Text(text = bodyText,style = MaterialTheme.typography.bodyLarge, fontStyle = FontStyle.Italic, fontSize = 18.sp)
}

@Composable
fun BlackLabelText(labelText: String){
    Text(text = labelText, fontSize = 18.sp, fontStyle = FontStyle.Normal, fontWeight = FontWeight.SemiBold)
}

@Composable
fun TextWithLabel(labelText: String, normalText: String) {
    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("$labelText: ")
            }
            append(normalText)
        },
        fontSize = getFontSizeByPercent(fontSizeInPercent = 4f)
    )
}

@Composable
fun LabelAndTextWithColor(labelText: String, normalText: String, color: Color, fontSize: TextUnit = getFontSizeByPercent(3.5f)){
    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(color = color)) {
                append(labelText)
            }
            append(": $normalText")
        },
        fontSize = fontSize,
        lineHeight = 1.4.em
    )
}

@Composable
fun NoDataMessage(){
    Column(
        modifier = Modifier
            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = "No Data!!", style = MaterialTheme.typography.titleLarge)
    }
}