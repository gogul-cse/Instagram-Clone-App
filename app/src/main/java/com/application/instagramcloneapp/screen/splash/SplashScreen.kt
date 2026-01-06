package com.application.instagramcloneapp.screen.splash

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.application.instagramcloneapp.R
import com.application.instagramcloneapp.navigation.InstagramScreen
import com.application.instagramcloneapp.screen.login.LoginViewmodel
import kotlinx.coroutines.delay

@Preview
@Composable
fun SplashScreen(navController: NavController = NavController(LocalContext.current),
                 viewModel: LoginViewmodel= hiltViewModel()
){
    val scale = remember { Animatable(0f) }

    val isLoggedIn by viewModel.isLoggedIn.collectAsState(initial = false)

    LaunchedEffect(Unit) {
        // Animate Logo
        scale.animateTo(targetValue = 1f, animationSpec = tween(
                durationMillis = 1200,
                easing = {
                    OvershootInterpolator(4f)
                        .getInterpolation(it)
                }
            )
        )
        delay(1200L)
        if (isLoggedIn) {
            navController.navigate(InstagramScreen.MainPage.name) {
                popUpTo(InstagramScreen.SplashScreen.name) { inclusive = true }
            }
        } else {
            navController.navigate(InstagramScreen.LoginScreen.name) {
                popUpTo(InstagramScreen.SplashScreen.name) { inclusive = true }
            }
        }
    }

    Surface(
        modifier = Modifier.padding(10.dp)
            .size(330.dp),
        shape = CircleShape,
        border = BorderStroke(width = 2.dp, color = Color.Blue.copy(alpha = 0.2f))
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {

            // Logo Animation
            Image(painter = painterResource(R.drawable.einfochips_logo),
                contentDescription = "Logo", modifier = Modifier.size(140.dp)
                    .scale(scale.value))

            // Fade In Text
            AnimatedVisibility(
                visible = scale.value > 0.7f,
                enter = fadeIn(animationSpec = tween(800))
            ) {
                Text(
                    text = "Einfo App",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}