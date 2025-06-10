package com.godlike.cardpair

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.ui.graphics.vector.ImageVector

data class Card(
    val image: ImageVector,
    var isClicked: Boolean = false,
    var isPaired: Boolean = false,
    var isFailed: Boolean = false
)

data class Game(
    var selectedCardIndex: Int = -1,
    var isGameOver: Boolean = false,
    var isGamePaused: Boolean = false,
    var score: Int = 0
)

fun generateCards(): List<Card> {
    return listOf(
        Card(image = Icons.Default.Face),
        Card(image = Icons.Default.Favorite),
        Card(image = Icons.Default.Star),
        Card(image = Icons.Default.ShoppingCart),
        Card(image = Icons.Default.Home),
        Card(image = Icons.Default.ThumbUp),
        Card(image = Icons.Default.Face),
        Card(image = Icons.Default.Favorite),
        Card(image = Icons.Default.Star),
        Card(image = Icons.Default.ShoppingCart),
        Card(image = Icons.Default.Home),
        Card(image = Icons.Default.ThumbUp),
    ).shuffled()
}