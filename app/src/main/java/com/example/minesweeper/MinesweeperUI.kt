package com.example.minesweeper

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val BackgroundColor = Color(0xFF1E1E1E)
val CardColor = Color(0xFF2D2D2D)
val CellHiddenColor = Color(0xFF3E3E42)
val CellRevealedColor = Color(0xFF252526)
val AccentColor = Color(0xFFBB86FC)
val FlagColor = Color(0xFFFFA726)
val MineColor = Color(0xFFEF5350)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MineSweeperScreen(viewModel: GameViewModel) {
    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundColor
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CardHeader(mineCount = viewModel.mineCount)

            androidx.compose.material3.Card(
                colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = CardColor),
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().horizontalScroll(rememberScrollState())) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(viewModel.gridSize),
                        modifier = Modifier
                            .padding(8.dp)
                            .width(700.dp)
                    ) {
                        items(viewModel.board) { cell ->
                            CellView(
                                cell = cell,
                                onClick = { viewModel.handleCellInteraction(cell) },
                                onLongClick = { viewModel.onToggleFlag(cell) }
                            )
                        }
                    }
                }
            }

            ControlPanel(viewModel)
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
                        Text(text = "Probaj ponovo")
                    }
                }
            )
        }
    }
}

@Composable
fun CardHeader(mineCount: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 40.dp, bottom = 10.dp)
    ) {
        Text(
            text = "MINESWEEPER",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            letterSpacing = 2.sp
        )
        Text(
            text = "Expert Mode",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun ControlPanel(viewModel: GameViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(60.dp)
            .background(CardColor, androidx.compose.foundation.shape.RoundedCornerShape(50)) // Kapsula oblik
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ModernModeButton(
            text = "Otkrij",
            icon = "💣",
            isSelected = viewModel.currentMode.value == GameMode.REVEAL,
            onClick = { viewModel.toggleMode(GameMode.REVEAL) }
        )
        ModernModeButton(
            text = "Zastavica",
            icon = "🚩",
            isSelected = viewModel.currentMode.value == GameMode.FLAG,
            onClick = { viewModel.toggleMode(GameMode.FLAG) }
        )
    }
}

@Composable
fun ModernModeButton(text: String, icon: String, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) AccentColor else Color.Transparent
    val textColor = if (isSelected) Color.Black else Color.White

    Box(
        modifier = Modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(50))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, color = textColor, fontWeight = FontWeight.Bold)
        }
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
        CellStatus.REVEALED -> if (cell.isMine) MineColor.copy(alpha = 0.5f) else CellRevealedColor
        CellStatus.FLAGGED -> CellHiddenColor
        CellStatus.HIDDEN ->CellHiddenColor
    }

    val borderStroke = if(cell.status == CellStatus.HIDDEN || cell.status == CellStatus.FLAGGED) {
        null
    } else {
        null
    }

    Box(
        modifier = Modifier
            .padding(1.dp)
            .aspectRatio(1f)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
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
                Text(text = "💣", fontSize = 20.sp)
            } else if (cell.nearbyMines > 0) {
                Text(
                    text = "${cell.nearbyMines}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = getNumberColor(cell.nearbyMines)
                )
            }
        } else if (cell.status == CellStatus.FLAGGED) {
            Text(text = "🚩", fontSize = 20.sp)
        }
    }
}

fun getNumberColor(number: Int): Color {
    return when (number) {
        1 -> Color(0xFF42A5F5)
        2 -> Color(0xFF66BB6A)
        3 -> Color(0xFFEF5350)
        4 -> Color(0xFFAB47BC)
        5 -> Color(0xFFFFA726)
        else -> Color.Gray
    }
}