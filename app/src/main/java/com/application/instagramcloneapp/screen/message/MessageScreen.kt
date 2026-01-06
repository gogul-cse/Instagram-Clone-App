package com.application.instagramcloneapp.screen.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.application.instagramcloneapp.model.LastChat
import com.application.instagramcloneapp.model.User
import com.application.instagramcloneapp.screen.profile.ProfileViewModel
import com.application.instagramcloneapp.utils.ImageUtils
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(navController: NavController,profileViewModel: ProfileViewModel= hiltViewModel(),
                  inboxViewModel: MessageInboxViewModel = hiltViewModel()) {

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val user by profileViewModel.user.collectAsState()
    val chats by inboxViewModel.chats.collectAsStateWithLifecycle()

    LaunchedEffect(uid) {
        if (uid!=null && user == null){
            profileViewModel.loadUserProfile()
        }
    }

    if (user == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        MessageTopBar(user!!)
        // 1. Search Bar
        SearchBarSection()

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // 2. Active Now Horizontal List
            item {
                Text(
                    text = "Active Now",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 12.dp)
                )
                ActiveNowRow(user!!)
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 8.dp))
            }

            // 3. Message List Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Messages", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "Requests", color = Color(0xFF0095F6), fontWeight = FontWeight.SemiBold)
                }
            }

            // 4. Message Items
            items(items = chats,
                key = { it.chatId }) { chat ->
                MessageItem(chat,
                    navController,
                    inboxViewModel = inboxViewModel)
            }
        }
    }

}

// --- COMPONENTS ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageTopBar(user: User) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.instaId,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Video Call")
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.Edit, contentDescription = "New Message")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        windowInsets = WindowInsets(0,0,0,0)
    )
}

@Composable
fun SearchBarSection() {
    var searchText by remember { mutableStateOf("") }

    TextField(
        value = searchText,
        onValueChange = { searchText = it },
        placeholder = { Text("Search", color = Color.Gray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFEFEFEF),
            unfocusedContainerColor = Color(0xFFEFEFEF),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}

@Composable
fun ActiveNowRow(user: User) {
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // "Your Note" Item
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(70.dp)) {
                Box(contentAlignment = Alignment.TopEnd) {
                    Surface(
                        modifier = Modifier.size(70.dp),
                        shape = CircleShape,
                        color = Color.LightGray
                    ) {
                        Box(contentAlignment = Alignment.Center) {

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
                    }
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.White, CircleShape)
                            .padding(2.dp)
                    ) {
                        Text("💭", fontSize = 12.sp, modifier = Modifier.align(Alignment.Center))
                    }
                }
                Text(
                    "Your Note",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

    }
}

@Composable
fun MessageItem(
    chat: LastChat,
    navController: NavController,
    inboxViewModel: MessageInboxViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                inboxViewModel.openChat(chat.otherUserId, navController)
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Profile Image
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = Color.LightGray
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (chat.profileImage.isNullOrEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    val bitmap = remember(chat.profileImage) {
                        ImageUtils.base64ToBitmap(chat.profileImage)
                    }
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Text Section
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = chat.instaId,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = chat.lastMessage,
                color = Color.Gray,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(22.dp)
        )
    }
}

