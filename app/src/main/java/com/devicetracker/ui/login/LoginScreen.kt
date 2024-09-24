package com.devicetracker.ui.login

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devicetracker.R
import com.devicetracker.ui.components.EmailField
import com.devicetracker.ui.components.EmailState
import com.devicetracker.ui.components.PasswordState
import com.devicetracker.ui.components.PasswordTextField

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    onSignInButtonCLick: (email: String, password: String) -> Unit
) {
    Scaffold (modifier = Modifier.fillMaxSize()){
        SignInContent( onSignInButtonCLick = onSignInButtonCLick)
    }
}

//@Preview(showBackground = true)
@Composable
fun SignInContent(onSignInButtonCLick: (email: String, password: String) -> Unit) {

    val emailState = remember { EmailState() }
    val passwordState = remember { PasswordState() }

    Column (
        Modifier
            .fillMaxSize()
            .absolutePadding(36.dp, 84.dp, 36.dp, 16.dp),
        horizontalAlignment =  Alignment.CenterHorizontally) {

        Text(text = stringResource(R.string.str_welcome_to_asset_tracker),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(80.dp))
        EmailField(emailState)
        Spacer(modifier = Modifier.height(16.dp))
        PasswordTextField(passwordState)
        Spacer(modifier = Modifier.height(36.dp))
        Button(
            modifier = Modifier.width(200.dp),
            shape = RoundedCornerShape(5.dp),
            onClick = {
            // Enable error display on button click
            emailState.enableShowError()
            passwordState.enableShowError()

            // Check if the input is valid before proceeding
            if (emailState.isValid && passwordState.isValid) {
                onSignInButtonCLick(emailState.text, passwordState.text)
            }
        }) {
            Text(text = "Login", fontSize = 18.sp, modifier = Modifier.padding(8.dp, 2.dp, 8.dp, 2.dp))
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(onSignInButtonCLick = {_,_->})
}