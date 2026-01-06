package com.application.instagramcloneapp.screen.profile


import android.Manifest
import android.graphics.Bitmap
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.application.instagramcloneapp.model.Post
import com.application.instagramcloneapp.model.User
import com.application.instagramcloneapp.navigation.InstagramScreen
import com.application.instagramcloneapp.utils.ImageUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.livedata.observeAsState

val ButtonGray = Color(0xFFEFEFEF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController,viewModel: ProfileViewModel) {

    val user by viewModel.user.collectAsState()

    val postUploaded =
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Boolean>("post_uploaded")
            ?.observeAsState()
    LaunchedEffect(postUploaded?.value) {
        if (postUploaded?.value == true) {
            viewModel.getPostByTime()
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.remove<Boolean>("post_uploaded")
        }
    }

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    LaunchedEffect(uid) {
        if (uid!=null){
            viewModel.loadUserProfile()
            viewModel.getPostByTime()
        }
    }
    when {
        viewModel.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        viewModel.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = viewModel.error ?: "Failed to load profile",
                    color = Color.Red
                )
            }
        }

        user != null -> {
            ProfileContent(
                navController = navController,
                user = user!!,viewModel
            )
        }
    }

}

@Composable
fun ProfileContent(
    navController: NavController,
    user: User,
    viewModel: ProfileViewModel
) {
    val posts by viewModel.posts.collectAsState()
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {


        item(span = { GridItemSpan(3) }) {
            ProfileTopBar(navController, user, viewModel)
        }

        item(span = { GridItemSpan(3) }) {
            ProfileHeaderSection(user,navController)
        }

        item(span = { GridItemSpan(3) }) {
            ActionButtonsSection(navController)
        }

        item(span = { GridItemSpan(3) }) {
            HighlightsSection()
        }

        item(span = { GridItemSpan(3) }) {
            ProfileTabs()
        }

        items(posts, key = {it.postId}) { post ->
            PostGridItem(
                post = post,
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(1.dp),
                navController
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(navController: NavController,user: User,viewModel: ProfileViewModel) {
    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let {
                viewModel.setImage(it)
                navController.navigate(InstagramScreen.NewPostScreen.name)
            }
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                imagePickerLauncher.launch("image/*")
            }
        }

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = user.instaId,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
            }
        },
        actions = {
            IconButton(onClick = {val permission =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    Manifest.permission.READ_MEDIA_IMAGES
                else
                    Manifest.permission.READ_EXTERNAL_STORAGE
                permissionLauncher.launch(permission)
            }) { Icon(Icons.Default.AddBox, contentDescription = "Add post") }
            IconButton(onClick = {
                navController.navigate(InstagramScreen.SettingsScreen.name)
            }) { Icon(Icons.Default.Menu, contentDescription = "Menu") }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        windowInsets = WindowInsets(0,0,0,0)
    )
}

@Composable
fun ProfileHeaderSection(user: User,navController: NavController) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Profile Image
            Surface(
                modifier = Modifier
                    .size(86.dp)
                    .border(1.dp, Color.LightGray, CircleShape)
                    .padding(4.dp),
                shape = CircleShape,
                color = Color.Gray
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

            Spacer(modifier = Modifier.width(16.dp))

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
                    ProfileStat(number = "${user.posts}", label = "Posts")
                    ProfileStat(number = "${user.followers}", label = "Followers",onClick = {navController.navigate(
                        InstagramScreen.FollowerAndFollowingScreen.name+"?tab=0")

                    })
                    ProfileStat(number = "${user.following}", label = "Following",onClick = {
                        navController.navigate(
                            InstagramScreen.FollowerAndFollowingScreen.name+"?tab=1")
                    })
                }
            }
        }

        // Bio Section
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "${user.bio}", fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ProfileStat(number: String, label: String,onClick: ()-> Unit={}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable{onClick()}) {
        Text(text = number, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(text = label, fontSize = 13.sp)
    }
}

@Composable
fun ActionButtonsSection(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Button(
            onClick = {navController.navigate(InstagramScreen.EditProfileScreen.name)},
            colors = ButtonDefaults.buttonColors(containerColor = ButtonGray),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .height(36.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("Edit profile", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }

        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = ButtonGray),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .height(36.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("Share profile", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }

        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = ButtonGray),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(36.dp)
                .height(36.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(Icons.Outlined.Person, contentDescription = "Suggestions", tint = Color.Black, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun HighlightsSection() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .border(1.dp, Color.LightGray, CircleShape)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AddBox, contentDescription = null, tint = Color.Black, modifier = Modifier.size(30.dp))
                }
                Text("New", fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
fun ProfileTabs() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(Icons.Default.GridOn, Icons.Outlined.AccountBox) // Grid & Tagged

    TabRow(
        selectedTabIndex = selectedTabIndex,
        contentColor = Color.Black,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = Color.Black
            )
        }
    ) {
        tabs.forEachIndexed { index, icon ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { selectedTabIndex = index },
                icon = { Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp)) }
            )
        }
    }
}


@Composable
fun PostGridItem(post: Post,modifier: Modifier,navController: NavController) {

    var bitmap by remember(post.imageUrl) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(post.imageUrl) {
        bitmap = withContext(Dispatchers.IO) {
            ImageUtils.base64ToBitmap(post.imageUrl)
        }
    }

    Box(
        modifier = modifier
            .background(Color.LightGray)
            .clickable{
                navController.navigate(InstagramScreen.PostViewScreen.name)
            },
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

