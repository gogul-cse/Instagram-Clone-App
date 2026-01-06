package com.application.instagramcloneapp.screen.search

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.application.instagramcloneapp.model.User
import com.application.instagramcloneapp.navigation.InstagramScreen
import com.application.instagramcloneapp.utils.ImageUtils

@Composable
fun SearchScreen(
    navController: NavController,viewmodel: SearchViewmodel = hiltViewModel()
) {
    val searchText = viewmodel.searchText
    val searchResults = viewmodel.searchResults
    val isLoading = viewmodel.isLoading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // Search Bar
        TextField(
            value = searchText,
            onValueChange = viewmodel::onSearchTextChange,
            placeholder = { Text("Search") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = { viewmodel.onSearchTextChange("") }) {
                        Icon(Icons.Default.Close, null)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Content
        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            searchResults.isEmpty() && searchText.isNotEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No users found")
                }
            }

            else -> {
                LazyColumn {
                    items(searchResults) { user ->
                        SearchUserItem(
                            user = user,
                            onClick = {navController.navigate("${InstagramScreen.OthersProfileScreen.name}/${user.id}")

                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SearchUserItem(user: User, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            color = Color.LightGray
        ) {

            if (user.profileImage.isNullOrEmpty()) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
            } else {
                val bitmap = remember(user.profileImage) {
                    ImageUtils.base64ToBitmap(user.profileImage)
                }

                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = user.instaId,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            if (user.instaId.isNotEmpty()) {
                Text(
                    text = user.userName,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}
