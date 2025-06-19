package com.godlike.cardpair.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainMenu(onPlayClicked: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF2b2b2b))
            .padding(vertical = 16.dp, horizontal = 8.dp)
    ) {
        Text(
            text = "Score: ${gameInfo.score}",
            color = Color.White,
            modifier = Modifier.align(alignment = Alignment.TopEnd)
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .align(alignment = Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Card Pair",
                fontSize = 32.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "by GodLikeGaming",
                color = Color.White,
            )
        }
        Column(
            modifier = Modifier
                .align(alignment = Alignment.BottomCenter)
                .aspectRatio(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onPlayClicked,
                modifier = Modifier.fillMaxWidth(0.25f)
            ) {
                Text(text = "Play")
            }
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(0.25f)
            ) {
                Text(text = "Quit")

            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainMenuPreview() {
    MainMenu(onPlayClicked = { /* No-op for preview */ })
}
