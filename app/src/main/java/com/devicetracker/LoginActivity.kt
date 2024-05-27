package com.devicetracker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devicetracker.ui.theme.DeviceTrackerTheme
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeviceTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    MainContent()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainContent() {
    Column (
        Modifier
            .fillMaxSize()
            .absolutePadding(16.dp, 64.dp, 16.dp, 16.dp),
             horizontalAlignment =  Alignment.CenterHorizontally) {

        Text(text = stringResource(R.string.str_welcome_to_device_tracker),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold)

         Spacer(modifier = Modifier.height(160.dp))
         SimpleOutlinedTextFiled()
         Spacer(modifier = Modifier.height(32.dp))
         Button(onClick = {  }) {
            Text(text ="SignIn", fontSize = 18.sp, modifier = Modifier.absolutePadding(8.dp,2.dp,8.dp,2.dp))
         }
    }
}

@Composable
fun SimpleOutlinedTextFiled() {
    var text  = remember { mutableStateOf("") }
    OutlinedTextField(
        value = text.value,
        onValueChange = {
            text.value = it
        },
        label = {
            Text(text = stringResource(R.string.str_email_address))
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
    )

}

@Serializable
data class User (
    @SerialName("Id") val id: String,
    @SerialName("Name") val name: String,
    @SerialName("Emp_Code") val empCode: Int,
    @SerialName("Email") val email: String,
    @SerialName("Created_At")val createdAt: String,
    @SerialName("Deleted_At")val deletedAt: String?,
)


/*@Serializable
data class User(
    val Id: String,
    val Name: String,
    val Emp_Code: Int,
    val Emial: String,
    val Created_At: String,
)*/
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}