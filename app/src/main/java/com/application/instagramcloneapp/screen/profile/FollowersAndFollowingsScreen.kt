package com.application.instagramcloneapp.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.instagramcloneapp.model.Followers
import com.application.instagramcloneapp.model.Following
import com.application.instagramcloneapp.screen.message.MessageInboxViewModel
import com.application.instagramcloneapp.utils.ImageUtils


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowersAndFollowingScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    initialTab: Int, // 0 = Followers, 1 = Following
    inboxViewModel: MessageInboxViewModel = hiltViewModel(),
) {
    var selectedTabIndex by remember { mutableIntStateOf(initialTab) }
    var searchText by remember { mutableStateOf("") }

    val followers by profileViewModel.followers.collectAsState()
    val following by profileViewModel.following.collectAsState()
    val user by profileViewModel.user.collectAsState()

    val userId = user?.id

    // Load data when tab changes
    LaunchedEffect(selectedTabIndex, userId) {
        if (userId == null) return@LaunchedEffect

        if (selectedTabIndex == 0) {
            profileViewModel.loadFollowers(userId)
        } else {
            profileViewModel.loadFollowing(userId)
        }
    }

    val filteredFollowers = followers.filter {
        it.followersInstaId.contains(searchText, ignoreCase = true)
    }

    val filteredFollowing = following.filter {
        it.followingInstaId.contains(searchText, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = user?.instaId ?: "",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // Tabs
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Followers") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Following") }
                )
            }

            FollowersSearchBar(searchText) { searchText = it }

            LazyColumn {
                if (selectedTabIndex == 0) {
                    items(
                        items = filteredFollowers,
                        key = { it.followersId }
                    ) { follower ->
                        FollowersRow(profileViewModel,follower)
                    }
                } else {
                    items(
                        items = filteredFollowing,
                        key = { it.followingId }
                    ) { followingUser ->
                        FollowingRow(
                             navController,followingUser,inboxViewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable fun FollowersSearchBar(text: String, onValueChange: (String) -> Unit) {

    TextField( value = text, onValueChange = onValueChange, placeholder = {
        Text("Search", color = Color.Gray, fontSize = 14.sp) },

        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null,
            tint = Color.Gray) },
        modifier = Modifier .fillMaxWidth() .padding(16.dp) .height(46.dp),
         shape = RoundedCornerShape(10.dp), colors = TextFieldDefaults.colors(
            focusedContainerColor = ButtonGray,
            unfocusedContainerColor = ButtonGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.Black ), singleLine = true )
}

@Composable
fun FollowersRow(profileViewModel: ProfileViewModel,followers: Followers) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            color = Color.LightGray
        ) {
            if (followers.followersProfile.isEmpty()) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
            } else {
                val bitmap = remember(followers.followersProfile) {
                    ImageUtils.base64ToBitmap(followers.followersProfile)
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

        Text(
            text = followers.followersInstaId,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        Button(onClick = { profileViewModel.removeFollower(followers.followersId) }) {
            Text("Remove")
        }
    }
}


@Composable
fun FollowingRow(navController: NavController,following: Following,
                 inboxViewModel: MessageInboxViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            color = Color.LightGray
        ) {
            if (following.followingProfile.isEmpty()) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
            } else {
                val bitmap = remember(following.followingProfile) {
                    ImageUtils.base64ToBitmap(following.followingProfile)
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

        Text(
            text = following.followingInstaId,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        Button(onClick = { inboxViewModel.openChat(following.followingId
            ,navController)}) {
            Text("Message")
        }
    }
}


