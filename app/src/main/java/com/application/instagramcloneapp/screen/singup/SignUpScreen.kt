package com.application.instagramcloneapp.screen.singup

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.application.instagramcloneapp.navigation.InstagramScreen
import com.application.instagramcloneapp.widgets.SignupTextField
import kotlinx.coroutines.delay


val PrimaryColor = Color(0xFF6200EE)
val SecondaryColor = Color(0xFF3700B3)
val BackgroundColor = Color(0xFFF5F5F5)

@Composable
fun SignUpScreen(navController: NavController,viewModel: SignUpViewModel){

    val state by viewModel.signupState

    val context = LocalContext.current

    var confirmPassword by remember { mutableStateOf("") }


    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    val isEmailAvailable = viewModel.isUserEmailAvailable


    val scrollState = rememberScrollState()

    LaunchedEffect(state.email) {
        if (state.email.isBlank() || !state.email.contains("@")) {
            viewModel.clearEmailState()
            return@LaunchedEffect
        }

        delay(500)
        viewModel.checkUserEmail(state.email)
    }


    LaunchedEffect(isEmailAvailable) {
        if (isEmailAvailable == false) {
            Toast.makeText(
                context,
                "Email already exists",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(30.dp))

        // 1. Header Section
        Text(
            text = "Create Account",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Start)
        )

        Text(
            text = "Chat with friends and share reels!",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 8.dp, bottom = 32.dp)
        )

        // 2. Name Field
        SignupTextField(
            value = state.userName,
            onValueChange = viewModel::updateName,
            label = "Full Name",
            icon = Icons.Default.Person,
            keyboardType = KeyboardType.Text
        )

        // 3. Phone Field
        SignupTextField(
            value = state.phoneNumber,
            onValueChange = viewModel::updatePhone,
            label = "Phone Number",
            phoneField = true,
            icon = Icons.Default.Phone,
            keyboardType = KeyboardType.Phone
        )

        // 4. Email Field
        SignupTextField(
            value = state.email,
            onValueChange = viewModel::updateEmail,
            label = "Email Address",
            icon = Icons.Default.Email,
            keyboardType = KeyboardType.Email
        )

        // 5. Password Field
        SignupTextField(
            value = state.password,
            onValueChange = viewModel::updatePassword,
            label = "Password",
            isVisible = isPasswordVisible,
            onVisibilityChange = { isPasswordVisible = !isPasswordVisible },
            icon = Icons.Default.Lock,
            keyboardType = KeyboardType.Password,
            passwordField = true
        )

        // 6. Confirm Password Field
        SignupTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password",
            isVisible = isConfirmPasswordVisible,
            onVisibilityChange = { isConfirmPasswordVisible = !isConfirmPasswordVisible },
            isLastItem = true,
            icon = Icons.Default.Lock,
            keyboardType = KeyboardType.Password,
            passwordField = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 7. Gradient Signup Button
        Button(
            onClick = {
                    if (state.password != confirmPassword){
                        Toast.makeText(context,"Please Check password!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (state.userName.isEmpty() or state.email.isEmpty() or state.phoneNumber.isEmpty() or state.password.isEmpty() or confirmPassword.isEmpty()){
                        Toast.makeText(context,"Please Fill everything!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (state.password.length<6){
                        Toast.makeText(context,"Password at least 6 character", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (isEmailAvailable == false) {
                        Toast.makeText(context, "Email already exists", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                viewModel.setBasicDetails(state.userName,state.phoneNumber,state.email,state.password)
                navController.navigate(InstagramScreen.AddProfileAndBioScreen.name)
                      },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(PrimaryColor, SecondaryColor)
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SIGN UP",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 8. Already have an account?
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(text = "Already have an account? ", color = Color.Gray)
            Text(
                text = "Login",
                color = PrimaryColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { navController.navigate(InstagramScreen.LoginScreen.name){
                    popUpTo(0){inclusive=true}
                } }
            )
        }
    }
}






