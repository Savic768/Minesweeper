package com.example.minesweeper

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MineSweeperScreen(viewModel: GameViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Minesweeper", fontSize = 30.sp, modifier = Modifier.padding(32.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(viewModel.gridSize),
            modifier = Modifier
                .padding(8.dp)
                .aspectRatio(1f)
        ) {
            items(viewModel.board) { cell ->
                CellView(
                    cell = cell,
                    onClick = { viewModel.handleCellInteraction(cell) },
                    onLongClick = { viewModel.onToggleFlag(cell) }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp, bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ModeButton(
                label = "Otkrij 💣",
                isSelected = viewModel.currentMode.value == GameMode.REVEAL,
                onClick = { viewModel.toggleMode(GameMode.REVEAL) }
            )
            ModeButton(
                label = "Zastavica 🚩",
                isSelected = viewModel.currentMode.value == GameMode.FLAG,
                onClick = { viewModel.toggleMode(GameMode.FLAG) }
            )
        }
    }

    if (viewModel.isgameOver.value) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
            },
            title = {
                Text(text = "BOOM! 💥")
            },
            text = {
                Text("Nagazili ste na minu. Želite li novu igru?")
            },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = { viewModel.resetGame() }
                ) {
                    Text("Probaj ponovo")
                }
            }
        )
    }
}

@Composable
fun ModeButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    androidx.compose.material3.Button(
        onClick = onClick,
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF4CAF50) else Color.Gray // Zelena ako je selektovano
        ),
        modifier = Modifier.padding(8.dp)
    ) {
        Text(text = label, color = Color.White)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CellView(
    cell: Cell,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val backgroundColor = when (cell.status) {
        CellStatus.REVEALED -> if (cell.isMine) Color.Red else Color(0xFFDDDDDD)
        CellStatus.FLAGGED -> Color.Yellow
        CellStatus.HIDDEN -> Color.Gray
    }

    Box(
        modifier = Modifier
            .padding(1.dp)
            .aspectRatio(1f)
            .background(backgroundColor)
            .border(0.5.dp, Color.Black)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (cell.status == CellStatus.REVEALED) {
            if (cell.isMine) {
                Text(text = "💣", fontSize = 14.sp)
            } else if (cell.nearbyMines > 0) {
                Text(
                    text = "${cell.nearbyMines}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = getNumberColor(cell.nearbyMines)
                )
            }
        } else if (cell.status == CellStatus.FLAGGED) {
            Text(text = "🚩", fontSize = 14.sp)
        }
    }
}

fun getNumberColor(number: Int): Color {
    return when (number) {
        1 -> Color.Blue
        2 -> Color(0xFF388E3C)
        3 -> Color.Red
        4 -> Color(0xFF010082)
        else -> Color.Black
    }
}