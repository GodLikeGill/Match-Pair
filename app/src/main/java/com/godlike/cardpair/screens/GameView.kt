package com.godlike.cardpair.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godlike.cardpair.Card
import com.godlike.cardpair.Game
import com.godlike.cardpair.generateCards
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



    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Match Pair",
            fontSize = 32.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Box(Modifier.fillMaxWidth()) {
            Text(
                text = "Time left: $timeLeft seconds",
                fontSize = 16.sp,
                modifier = Modifier
                    .align(alignment = Alignment.TopStart)
                    .padding(start = 8.dp)
            )
            Text(
                text = "Score: ${gameInfo.score}",
                modifier = Modifier.align(alignment = Alignment.TopEnd)
            )
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(cards.size) { index ->
                CardsView(index = index, cards = cards)
            }
        }
    }
}

@Composable
private fun CardsView(index: Int, cards: SnapshotStateList<Card>) {
    val scope = rememberCoroutineScope()
    Button(
        modifier = Modifier.aspectRatio(0.75f),
        colors = ButtonDefaults.buttonColors(
            containerColor = (if (cards[index].isClicked) Color(0xFFFFFF00)
            else if (cards[index].isPaired) Color(0xFF00FF00)
            else if (cards[index].isFailed) Color(0xFFFF0000)
            else Color(0xFF0070FF)),
            contentColor = Color.White,
        ),
        shape = RoundedCornerShape(CornerSize(10.dp)),
        onClick = {
            if (!cards[index].isClicked && !cards[index].isPaired && !cards[index].isFailed && !gameInfo.isGamePaused && !gameInfo.isGameOver) {
                //On Second Click
                if (gameInfo.selectedCardIndex > -1) {
                    // If it is pair
                    if (cards[gameInfo.selectedCardIndex].image == cards[index].image) {
                        cards[index] = cards[index].copy(isClicked = false, isPaired = true)
                        cards[gameInfo.selectedCardIndex] = cards[gameInfo.selectedCardIndex].copy(
                            isClicked = false,
                            isPaired = true
                        )
                        gameInfo.selectedCardIndex = -1
                    } else {
                        //If it is not pair
                        gameInfo.isGamePaused = true
                        cards[index] =
                            cards[index].copy(isClicked = false, isPaired = false, isFailed = true)
                        cards[gameInfo.selectedCardIndex] = cards[gameInfo.selectedCardIndex].copy(
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
            Image(imageVector = cards[index].image, "", Modifier.fillMaxSize())
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
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = dialogTitle)
            }
        },
        text = {
            Text(text = dialogText)
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
                Text("Restart")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Quit")
            }
        }
    )
}

//@Preview
//@Composable
//private fun PreviewGameView() {
//    GameView(onGameOverMainMenu = {}, onRestart = {},
//    )
//}