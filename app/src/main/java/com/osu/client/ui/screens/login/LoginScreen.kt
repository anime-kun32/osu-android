package com.osu.client.ui.screens.login

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.osu.client.ui.theme.OsuPink
import com.osu.client.ui.theme.OsuPurple

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    // Pulsing animation for the logo
    val infiniteTransition = rememberInfiniteTransition(label = "logo_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse_scale",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF2A0A1E),
                        Color(0xFF0D0D1A),
                        Color(0xFF080810),
                    ),
                    radius = 1200f,
                    center = Offset(400f, 300f),
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        // Decorative blurred blobs in background
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-80).dp, y = (-150).dp)
                .blur(80.dp)
                .background(OsuPink.copy(alpha = 0.25f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(250.dp)
                .offset(x = 120.dp, y = 200.dp)
                .blur(80.dp)
                .background(OsuPurple.copy(alpha = 0.2f), CircleShape)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier.padding(horizontal = 40.dp),
        ) {
            // osu! logo circle rings
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(scale)
                    .size(120.dp),
            ) {
                // outer ring
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            Brush.radialGradient(listOf(Color.Transparent, OsuPink.copy(0.35f))),
                            CircleShape,
                        )
                )
                // mid ring
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(
                            Brush.radialGradient(listOf(Color.Transparent, OsuPink.copy(0.6f))),
                            CircleShape,
                        )
                )
                // inner filled
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFFFF99CC), OsuPink)
                            ),
                            CircleShape,
                        )
                )
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = "osu!",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = (-1).sp,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "rhythm is just a click away",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(56.dp))

            Button(
                onClick = { viewModel.loginWithOsu(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OsuPink,
                    contentColor = Color.White,
                ),
                shape = MaterialTheme.shapes.large,
            ) {
                Text(
                    text = "Sign in with osu!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Your browser will open to securely sign you in.\nNo password is stored on this device.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
            )
        }
    }
}
