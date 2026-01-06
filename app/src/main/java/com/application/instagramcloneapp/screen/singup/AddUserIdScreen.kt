package com.application.instagramcloneapp.screen.singup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.instagramcloneapp.navigation.InstagramScreen
import kotlinx.coroutines.delay

// --- COLORS ---
val ValidGreen = Color(0xFF4CAF50)
val ErrorRed = Color(0xFFB00020)
val InstaBlue = Color(0xFF0095F6)

@Composable
fun AddUserIdScreen(navController: NavController,
                    viewModel: SignUpViewModel
) {
    val state by viewModel.signupState
    var localError by remember { mutableStateOf<String?>(null) }

    // States for validation
    var isLoading by viewModel.isLoading
    val isAvailable = viewModel.isUsernameAvailable

    // Simulate API Check Logic
    LaunchedEffect(state.instaId) {
        viewModel.clearUsernameAvailability()
        if (state.instaId.isBlank()) {
            return@LaunchedEffect
        }

        if (localError != null || state.instaId.length < 4) {
            return@LaunchedEffect
        }
        // Debounce: Wait for user to stop typing for 500ms
        delay(500)
        viewModel.checkUserInstaIdExist(state.instaId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // 1. Header Text
        Text(
            text = "Create username",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Use lowercase letters, numbers, dots and underscores only.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 2. Input Field with Status Icon
        OutlinedTextField(
            value = state.instaId,
            onValueChange = viewModel::onUsernameChanged,
            isError = localError!=null || (isAvailable == false && !isLoading),
            placeholder = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            trailingIcon = {
                when {
                    isLoading -> CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )

                    isAvailable == true -> Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = ValidGreen
                    )

                    isAvailable == false -> Icon(
                        Icons.Default.Clear,
                        contentDescription = null,
                        tint = ErrorRed
                    )
                }
            }
        )

        // 3. Validation Message
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth().height(20.dp), contentAlignment = Alignment.CenterStart) {
            when {
                localError != null -> {
                    Text(text = localError!!, color = ErrorRed, fontSize = 12.sp)
                }

                isAvailable == false -> {
                    Text(text = "Username already taken", color = ErrorRed, fontSize = 12.sp)
                }

                isAvailable == true -> {
                    Text(text = "Username available", color = ValidGreen, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Next Button
        Button(
            onClick = { viewModel.setUsername(state.instaId)
                      navController.navigate(InstagramScreen.SignUpScreen.name){
                          launchSingleTop = true
                      }},
            enabled = localError == null && isAvailable == true,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = InstaBlue,
                disabledContainerColor = InstaBlue.copy(alpha = 0.5f)
            )
        ) {
            Text(text = "Next", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun AddUserIdPreview() {
//    MaterialTheme {
//        AddUserIdScreen()
//    }
//}
