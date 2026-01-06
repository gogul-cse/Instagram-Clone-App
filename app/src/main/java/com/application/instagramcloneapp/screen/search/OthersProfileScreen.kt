package com.application.instagramcloneapp.screen.search

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.navigation.NavController
import com.application.instagramcloneapp.model.Post
import com.application.instagramcloneapp.model.User
import com.application.instagramcloneapp.navigation.InstagramScreen
import com.application.instagramcloneapp.screen.message.MessageInboxViewModel
import com.application.instagramcloneapp.screen.profile.ProfileViewModel
import com.application.instagramcloneapp.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val InstaBlue = Color(0xFF0095F6)
val ButtonGray = Color(0xFFEFEFEF)
val BorderGray = Color(0xFFDBDBDB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherUserProfileScreen(navController: NavController,
    userId: String,viewmodel: SearchViewmodel = hiltViewModel()
,profileViewModel: ProfileViewModel = hiltViewModel(),messageInboxViewModel: MessageInboxViewModel=hiltViewModel()
) {

    LaunchedEffect(userId) {
        viewmodel.loadUserById(userId)
        profileViewModel.getOthersPostById(userId)
        viewmodel.checkIsUserFollowing(userId)
    }

    val isFollowing = viewmodel.isFollowing
    val posts by profileViewModel.posts.collectAsState()

    when {
        viewmodel.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        viewmodel.user == null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("User not found")
            }
        }
        else->{
            val user = viewmodel.user!!
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user.instaId,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = {navController.popBackStack()}) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        },
                        actions = {
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Options")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                    )
                },
                containerColor = Color.White
            ) { paddingValues ->

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    // 1. Header Section (Image + Stats)
                    item(span = { GridItemSpan(3) }) {
                        OtherProfileHeader(user,navController,
                            postCount = user.posts.toString(),
                            followersCount = user.followers.toString(),
                            followingCount = user.following.toString()
                        )
                    }

                    // 2. Bio Section
                    item(span = { GridItemSpan(3) }) {
                        BioSection(
                            bio = user.bio.toString(),
                            followedBy = ""
                        )
                    }

                    // 3. Action Buttons (Follow/Message)
                    item(span = { GridItemSpan(3) }) {
                        ActionButtonsRow(navController,user,messageInboxViewModel,isFollowing,

                            onFollowClick = {
                                if (isFollowing){
                                    viewmodel.unfollowUser(userId)
                                }else{
                                    viewmodel.followUser(user.id,user.instaId,user.profileImage!!)
                                }

                            }
                        )
                    }

                    item(span = { GridItemSpan(3) }) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item(span = { GridItemSpan(3) }) {
                        MediaTabs(0) { }
                    }
                    items(posts, key = {it.postId}) {post->

                            OtherProfilePostGrid(post,modifier = Modifier
                                .aspectRatio(1f)
                                .padding(1.dp))
                        }
                    }
                }
            }
        }

}


@Composable
fun OtherProfileHeader(user: User,navController: NavController,
    postCount: String,
    followersCount: String,
    followingCount: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(88.dp)
                .border(2.dp, BorderGray, CircleShape)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
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
        }

        Spacer(modifier = Modifier.width(20.dp))

        Column(modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start) {
            Text(
                text = user.userName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileStatItem(postCount, "Posts")
                ProfileStatItem(followersCount, "Followers",onClick = {navController.navigate(
                    InstagramScreen.FollowerAndFollowingScreen.name+"?tab=0")

                })
                ProfileStatItem(followingCount, "Following",onClick = {navController.navigate(
                    InstagramScreen.FollowerAndFollowingScreen.name+"?tab=1")

                })
            }
        }
    }
}

@Composable
fun ProfileStatItem(count: String, label: String,onClick: ()-> Unit={}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable{onClick()}) {
        Text(text = count, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(text = label, fontSize = 14.sp)
    }
}

@Composable
fun BioSection(bio: String, followedBy: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(
            text = bio,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        if (followedBy.isNotEmpty()) {
            Text(
                text = followedBy,
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun ActionButtonsRow(navController: NavController,user: User,inboxViewmodel: MessageInboxViewModel,
    isFollowing: Boolean,
    onFollowClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Button(
            onClick = onFollowClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFollowing) ButtonGray else InstaBlue,
                contentColor = if (isFollowing) Color.Black else Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .height(36.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = if (isFollowing) "Following" else "Follow",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            if (isFollowing) {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        Button(
            onClick = {inboxViewmodel.openChat(user.id,navController) },
            colors = ButtonDefaults.buttonColors(containerColor = ButtonGray),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .height(36.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = "Message",
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }

        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = ButtonGray),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .size(36.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PersonAdd,
                contentDescription = "Suggestions",
                tint = Color.Black,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun MediaTabs(selectedIndex: Int, onTabSelected: (Int) -> Unit) {
    val icons = listOf(Icons.Default.GridOn, Icons.Outlined.AccountBox)

    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = Color.White,
        contentColor = Color.Black,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                height = 1.5.dp,
                color = Color.Black
            )
        }
    ) {
        icons.forEachIndexed { index, icon ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = if (selectedIndex == index) Color.Black else Color.Gray
                    )
                }
            )
        }
    }
}

@Composable
fun OtherProfilePostGrid(post: Post,modifier: Modifier) {

    var bitmap by remember(post.imageUrl) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(post.imageUrl) {
        bitmap = withContext(Dispatchers.IO) {
            ImageUtils.base64ToBitmap(post.imageUrl)
        }
    }

    Box(
        modifier = modifier
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

