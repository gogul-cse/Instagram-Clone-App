package com.application.instagramcloneapp.screen.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.instagramcloneapp.model.Post
import com.application.instagramcloneapp.utils.ImageUtils
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.application.instagramcloneapp.model.User
import com.application.instagramcloneapp.screen.profile.ProfileViewModel


val InstaGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFBAA47),
        Color(0xFFD91A46),
        Color(0xFFA60F93)
    )
)

val BackgroundColor = Color(0xFFFAFAFA)
val BottomNavColor = Color.White
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.loadFeed()
        viewModel.loadSuggestions()
        profileViewModel.loadUserProfile()
    }

    val user by profileViewModel.user.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {

        InstaTopBar()
        when {
            viewModel.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        user?.let {
                            StoriesSection(it)
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                        }
                    }

                    items(
                        items = viewModel.posts,
                        key = { it.postId }
                    ) { post ->
                        InstaPostItem(post)
                    }
                    item {
                        EmptyFeedSection(
                            suggestions = viewModel.suggestions,
                            onFollowClick = { user ->
                                viewModel.followUserOptimistic(user)
                            }
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstaTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Instagram",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Cursive
            )
        },
        actions = {
            IconButton(onClick = {  }) {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "Notifications",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        windowInsets = WindowInsets(0,0,0,0)
    )
}


//  Stories Section
@Composable
fun StoriesSection(user: User) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Surface(
                        modifier = Modifier.size(65.dp),
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
                    Icon(
                        imageVector = Icons.Filled.AddCircle,
                        contentDescription = "Add Story",
                        tint = Color(0xFF0095F6),
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color.White, CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                    )
                }
                Text("Your Story", fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
fun StoryItem() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .background(InstaGradient, CircleShape)
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .size(65.dp)
                    .border(2.dp, Color.White, CircleShape),
                shape = CircleShape,
                color = Color.LightGray
            ) {
                Icon(Icons.Filled.Face, contentDescription = null, tint = Color.White, modifier = Modifier.padding(10.dp))
            }
        }
        Text(text = "user_name", fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
    }
}

//  Feed Post Item
@Composable
fun InstaPostItem(post: Post) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(modifier = Modifier.size(36.dp), shape = CircleShape, color = Color.LightGray) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    color = Color.LightGray
                ) {
                    val bitmap =  remember(post.userProfileImage) {
                        ImageUtils.base64ToBitmap(post.userProfileImage)
                    }
                    if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )


                    } else {Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.White
                        )
                    }
                }
            }
            Text(
                text = post.userInstId,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .weight(1f)
            )
            Icon(Icons.Default.MoreVert, contentDescription = "Options")
        }

        val bitmap = remember(post.imageUrl) {
            post.imageUrl.takeIf { it.isNotBlank() }
                ?.let { ImageUtils.base64ToBitmap(it) }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .background(Color(0xFFEEEEEE)),
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text("Image not available", color = Color.Gray)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Like", modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text(text = "${post.likes}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Icon(Icons.AutoMirrored.Default.Comment, contentDescription = "Comment", modifier = Modifier.size(26.dp)) // Using generic icon
            Spacer(modifier = Modifier.width(16.dp))
            Icon(Icons.AutoMirrored.Default.Send, contentDescription = "Share", modifier = Modifier.size(26.dp))
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Save", modifier = Modifier.size(28.dp))
        }


        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
            Row(modifier = Modifier.padding(top = 4.dp)) {
                Text(text = "${post.userInstId} ", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = post.caption, fontSize = 14.sp)
            }

        }
    }
}

@Composable
fun EmptyFeedSection(
    suggestions: List<User>,
    onFollowClick: (User) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Suggested for you",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(12.dp))

        suggestions.take(5).forEach { user ->
            FollowSuggestionItem(user, onFollowClick)
        }
    }
}

@Composable
fun FollowSuggestionItem(
    user: User,
    onFollowClick: (User) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = Color.LightGray
        ) {
            if (!user.profileImage.isNullOrEmpty()) {
                val bitmap = remember(user.profileImage) {
                    ImageUtils.base64ToBitmap(user.profileImage)
                }
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }else{
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(user.instaId, fontWeight = FontWeight.Bold)
            Text("Suggested for you", fontSize = 12.sp, color = Color.Gray)
        }

        Button(
            onClick = {
                onFollowClick(user)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor =  Color(0xFF0095F6)
            )
        ) {
            Text(text =  "Follow", color = Color.White, fontSize = 13.sp)
        }
    }
}
