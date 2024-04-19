package com.devicetracker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.devicetracker.ui.theme.DeviceTrackerTheme
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.client.request.request
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val supabaseClient = createSupabaseClient(
    supabaseUrl = "",
    supabaseKey = ""
) {
    install(Postgrest)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeviceTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Greeting("Android")
                    //FetchDataButton()
                    //MainContent()
                    LaunchEffectComposable()
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
            .fillMaxWidth()
            .absolutePadding(16.dp, 16.dp, 16.dp, 16.dp), horizontalAlignment =  Alignment.CenterHorizontally) {
        Button(onClick = {
            Log.d("Alkesh","Button clicked.....")
            //LaunchEffectComposable()

        }) {
            Text("Small Button")

        }
    }
}

@Composable
fun LaunchEffectComposable() {
    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.IO) {
            val data = supabaseClient.postgrest["Users"].select {
                this.select(columns = Columns.ALL)
            }.decodeList<User>()
            data.forEach {

            }
            Log.d("Alkesh","Fetch Data: ${data}")
        }
    }
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

@Composable
fun GreetingPreview() {
    DeviceTrackerTheme {
        Greeting("Android")
    }
}

@Composable
fun FetchDataButton() {
    Button(
         modifier = Modifier
             .height(40.dp)
             .width(150.dp),
        onClick = {
        Log.d("Alkesh","Button clicked.....")
    }) {
        Text(text = "Get Device Data")
    }
}