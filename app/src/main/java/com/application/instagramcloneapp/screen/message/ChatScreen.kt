package com.application.instagramcloneapp.screen.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.instagramcloneapp.model.Messages
import com.application.instagramcloneapp.navigation.InstagramScreen
import com.application.instagramcloneapp.utils.ImageUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    chatId: String,
    viewModel: MessageViewModel = hiltViewModel()
) {

    val messages by viewModel.messages.collectAsState()
    val chatUser by viewModel.chatUser.collectAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(chatId) {
        viewModel.initChat(chatId)
    }

    LaunchedEffect(messages.lastOrNull()?.id) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }
    if (chatUser == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }


    Scaffold(contentWindowInsets = WindowInsets.safeDrawing.exclude(WindowInsets.ime),
        topBar = {
            ChatTopBar(
                username = chatUser?.instaId ?: "",
                userId = chatUser?.id ?: "",
                navController = navController,
                profile = chatUser?.profileImage
            )
        },
        containerColor = Color.White
    ) { paddingValues ->

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
            ) {

                items(
                    items = messages,
                ) { message ->
                    MessageBubble(
                        message = message,
                        isFromMe = viewModel.isFromMe(message),
                        otherProfile = chatUser?.profileImage
                    )
                }
            }
            ChatInputBar(
                onSend = { viewModel.sendMessage(it) },
                modifier = Modifier.fillMaxWidth()
                    .windowInsetsPadding(
                        WindowInsets.ime.union(WindowInsets.navigationBars)
                    )
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(username: String,userId:String, navController: NavController,profile: String?) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable{
                navController.navigate("${InstagramScreen.OthersProfileScreen.name}/${userId}")
            }) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = Color.LightGray
                ) {
                    Box(contentAlignment = Alignment.Center) {

                        if (profile.isNullOrEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color.White
                            )
                        } else {
                            val bitmap = remember(profile) {
                                ImageUtils.base64ToBitmap(profile)
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
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = username, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Active now", fontSize = 12.sp, color = Color.Gray)
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = {}) { Icon(Icons.Default.Call, contentDescription = "Call") }
            IconButton(onClick = {}) { Icon(Icons.Default.Videocam, contentDescription = "Video") }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

@Composable
fun MessageBubble(message: Messages,
                  isFromMe: Boolean,
                  otherProfile: String?) {
    val bubbleShape = if (isFromMe) {
        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp)
    }

    val alignment = if (isFromMe) Alignment.End else Alignment.Start


    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = if (isFromMe) Arrangement.End else Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (!isFromMe) {
            Surface(modifier = Modifier
                .size(28.dp)
                .padding(end = 8.dp), shape = CircleShape, color = Color.LightGray) {
                if (!otherProfile.isNullOrEmpty()) {
                    val bitmap = remember(otherProfile) {
                        ImageUtils.base64ToBitmap(otherProfile)
                    }
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Person, contentDescription = null)
                }
            }
        }

        Column(horizontalAlignment = alignment) {
            Box(
                modifier = Modifier
                    .clip(bubbleShape)
                    .background(
                        if (isFromMe) Brush.horizontalGradient(
                            listOf(
                                Color(0xFF3797F0),
                                Color(0xFF3797F0)
                            )
                        )
                        else Brush.horizontalGradient(
                            listOf(
                                Color(0xFFEFEFEF),
                                Color(0xFFEFEFEF)
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = message.message,
                    color = if (isFromMe) Color.White else Color.Black,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun     ChatInputBar(onSend: (String) -> Unit,modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        TextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text("Message...") },
            modifier = Modifier.weight(1f),
            trailingIcon = {
                Text(
                    text = "Send",
                    color = Color(0xFF3797F0),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            if (text.isNotBlank()) {
                                onSend(text)
                                text = ""
                            }
                        }
                        .padding(8.dp)
                )
            }
        )
    }
}



