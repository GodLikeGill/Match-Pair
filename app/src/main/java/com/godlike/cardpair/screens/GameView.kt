package com.godlike.cardpair.screens

import android.graphics.Paint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godlike.cardpair.model.Card
import com.godlike.cardpair.model.Game
import com.godlike.cardpair.model.generateCards
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val TAG = "DEBUG"
var gameInfo = Game()


@Composable
fun GameView(
    key: Int,
    onGameOverMainMenu: () -> Unit,
    onRestart: () -> Unit,
) {

    var timeLeft by remember(key) { mutableIntStateOf(60) }
    var showDialog by remember(key) { mutableStateOf(false) }
    val cards = remember(key) { mutableStateListOf<Card>() }

    LaunchedEffect(key) {
        gameInfo = Game() // reset game
        cards.clear()
        cards.addAll(generateCards())

        while (timeLeft > 0 && !gameInfo.isGameOver) {
            delay(1000L)
            timeLeft--
            Log.d(TAG, "GameView: " + gameInfo.isGameOver + " : " + timeLeft.toString())
        }
        if (timeLeft == 0) {
            gameInfo.isGameOver = true
            showDialog = true
        }
        Log.d(TAG, "GameView: " + gameInfo.isGameOver)
    }

    if (showDialog) {
        AlertDialogExample(
            onDismissRequest = {
                onGameOverMainMenu()
                showDialog = false
            },
            onConfirmation = {
                onRestart()
                showDialog = false
            },
            dialogTitle = "Game Over",
            dialogText = "Your score is: " + gameInfo.score,
        )
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF2b2b2b)),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Match Pair",
                fontSize = 32.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Box(Modifier.fillMaxWidth()) {
                Text(
                    text = "Time left: $timeLeft seconds",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .padding(start = 8.dp)
                )
                Text(
                    text = "Score: ${gameInfo.score}",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier
                        .align(alignment = Alignment.TopEnd)
                        .padding(end = 8.dp)
                )
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
            ) {
                items(cards.size) { index ->
                    Box (Modifier.padding(10.dp)){ CardsView(index = index, cards = cards) }

                }
            }
        }
    }
}

@Composable
private fun CardsView(index: Int, cards: SnapshotStateList<Card>) {
    val scope = rememberCoroutineScope()
    val containerColor = cards[index].color
    val glowColor = cards[index].color
    val glowRadius = 8.dp
    val cornerRadius = 8.dp
    val xShifting = 0.dp
    val yShifting = 0.dp
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .aspectRatio(0.75f)
            .drawBehind {
                val canvasSize = size
                drawContext.canvas.nativeCanvas.apply {
                    drawRoundRect(
                        0f, // Left
                        0f, // Top
                        canvasSize.width, // Right
                        canvasSize.height, // Bottom
                        cornerRadius.toPx(), // Radius X
                        cornerRadius.toPx(), // Radius Y
                        Paint().apply {
                            color = containerColor.toArgb()
                            isAntiAlias = true
                            setShadowLayer(
                                glowRadius.toPx(),
                                xShifting.toPx(), yShifting.toPx(),
                                glowColor
                                    .copy(alpha = 1f)
                                    .toArgb()
                            )
                        }
                    )
                }
            }
            .clickable {
                if (!cards[index].isClicked && !cards[index].isPaired && !cards[index].isFailed && !gameInfo.isGamePaused && !gameInfo.isGameOver) {
                    //On Second Click
                    if (gameInfo.selectedCardIndex > -1) {
                        // If it is pair
                        if (cards[gameInfo.selectedCardIndex].image == cards[index].image) {
                            cards[index] = cards[index].copy(isClicked = false, isPaired = true)
                            cards[gameInfo.selectedCardIndex] =
                                cards[gameInfo.selectedCardIndex].copy(
                                    isClicked = false,
                                    isPaired = true
                                )
                            gameInfo.selectedCardIndex = -1
                        } else {
                            //If it is not pair
                            gameInfo.isGamePaused = true
                            cards[index] =
                                cards[index].copy(
                                    isClicked = false,
                                    isPaired = false,
                                    isFailed = true
                                )
                            cards[gameInfo.selectedCardIndex] =
                                cards[gameInfo.selectedCardIndex].copy(
                                    isClicked = false,
                                    isPaired = false,
                                    isFailed = true
                                )
                            scope.launch {
                                delay(1000L)
                                cards[index] = cards[index].copy(isFailed = false)
                                cards[gameInfo.selectedCardIndex] =
                                    cards[gameInfo.selectedCardIndex].copy(isFailed = false)
                                gameInfo.isGamePaused = false
                                gameInfo.selectedCardIndex = -1
                            }
                        }
                        gameInfo.score++
                    } else {
                        // On First Click
                        cards[index] = cards[index].copy(isClicked = true)
                        gameInfo.selectedCardIndex = index
                    }
                }
            },
    ) {
        AnimatedVisibility(
            visible = cards[index].isClicked || cards[index].isPaired || cards[index].isFailed,
            enter = fadeIn(initialAlpha = 0.25f),
            exit = fadeOut(targetAlpha = 0.25f)
        ) {
            Image(
                imageVector = cards[index].image,
                contentDescription = "",
                modifier = Modifier.aspectRatio(0.5f))
        }
    }
}

@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
) {
    AlertDialog(
        containerColor = Color(0xFF282828),
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = dialogTitle, color = Color.White)
            }
        },
        text = {
            Text(text = dialogText, color = Color.White)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Restart", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Quit", color = Color.White)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewCardView() {
    val dummyCards = remember {
        mutableStateListOf(
            Card(
                image = Icons.Default.Face, // or any valid drawable
                isClicked = false,
                isPaired = false,
                isFailed = false
            )
        )
    }
    CardsView(index = 0, cards = dummyCards)
}
