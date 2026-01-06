package com.application.instagramcloneapp.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.instagramcloneapp.screen.singup.SignUpViewModel
import com.application.instagramcloneapp.utils.ImageUtils

@Composable
fun NewPostScreen(navController: NavController,viewModel: ProfileViewModel,
                  userViewmodel: SignUpViewModel = hiltViewModel()
){

    val context = LocalContext.current
    var caption by remember { mutableStateOf("") }

    val imageUri = viewModel.imageUri.value

    Column(modifier = Modifier.fillMaxSize()) {

        if (imageUri == null){
            Text("No image selected")
        }else{
            val imageAsBitmap = ImageUtils.uriToBitmap(context,imageUri)
            Image(
                bitmap = imageAsBitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )
        }


        OutlinedTextField(
            value = caption,
            onValueChange = { caption = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Write a caption...") }
        )

        Button(
            onClick = {
                imageUri?.let {
                    val imageAsString = ImageUtils.uriToBase64(context,it)
                    viewModel.uploadPost(
                        imageUrl = imageAsString,
                        caption = caption
                    )
                    userViewmodel.updatePostNumber()
                        viewModel.clear()
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("post_uploaded", true)

                    navController.popBackStack()

                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Share")
        }
    }
}