package com.application.instagramcloneapp.screen.singup

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.application.instagramcloneapp.navigation.InstagramScreen
import com.application.instagramcloneapp.utils.ImageUtils
import com.application.instagramcloneapp.utils.rememberImagePicker

val BorderGray = Color(0xFFDBDBDB)

@Composable
fun AddProfileAndBioScreen(navController: NavController,
    viewModel: SignUpViewModel
) {
    var bioText by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberImagePicker { uri ->
        imageUri = uri
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                imagePicker.launch("image/*")
            }
        }

    val context = LocalContext.current
    val isLoading = viewModel.isLoading.value

    if (isLoading){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { viewModel.success.value }
            .collect { success ->
                if (success) {
                    navController.navigate(InstagramScreen.MainPage.name) {
                        popUpTo(0) { inclusive = true }
                    }
                    viewModel.resetSuccess()
                }
            }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // 1. Header
        Text(
            text = "Add profile photo",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = "Add a profile photo so your friends know it's you.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp, bottom = 32.dp, start = 16.dp, end = 16.dp)
        )

        // 2. Profile Image Placeholder / Upload Area
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .border(2.dp, BorderGray, CircleShape)
                .clickable { val permission =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        Manifest.permission.READ_MEDIA_IMAGES
                    else
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    permissionLauncher.launch(permission) }
        ) {
            if (imageUri == null) {
                Icon(Icons.Default.Person, contentDescription = null)
            } else {
                val bitmap = remember(imageUri) {
                    ImageUtils.uriToBitmap(context, imageUri!!)
                }
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Selected profile image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop)
            }
        }

        Text(
            text = "Add profile",
            color = InstaBlue,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            modifier = Modifier
                .padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 3. Bio Input
        OutlinedTextField(
            value = bioText,
            onValueChange = { bioText = it },
            label = { Text("Bio (Optional)") },
            placeholder = { Text("Tell us about yourself...") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = InstaBlue,
                unfocusedBorderColor = BorderGray,
                focusedContainerColor = Color(0xFFFAFAFA),
                unfocusedContainerColor = Color(0xFFFAFAFA)
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        // 4. Action Buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Button(
                onClick = { val base64Image = imageUri?.let {
                    val bitmap = ImageUtils.uriToBitmap(context, it)
                    val bytes = ImageUtils.compressBitmap(bitmap)
                    ImageUtils.byteArrayToBase64(bytes)
                } ?: ""
                    viewModel.setProfileDetails(profileImageUrl = base64Image, bio = bioText)
                          viewModel.createAccount()
                          },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = InstaBlue)
            ) {
                Text(text = "Save", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    viewModel.setProfileDetails( bio = bioText,profileImageUrl = null)
                    viewModel.createAccount()
                },enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
            ) {
                Text(text = "Skip", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

