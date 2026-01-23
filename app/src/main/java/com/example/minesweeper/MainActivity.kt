package com.example.minesweeper

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.minesweeper.ui.theme.MinesweeperTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = GameViewModel()

        setContent {
            MineSweeperScreen(viewModel)
        }
    }
}
enum class CellStatus{
    HIDDEN,REVEALED,FLAGGED
}

data class  Cell(
    val row: Int,
    val col : Int,
    val isMine: Boolean = false,
    val status: CellStatus = CellStatus.HIDDEN,
    val nearbyMines: Int = 0
)