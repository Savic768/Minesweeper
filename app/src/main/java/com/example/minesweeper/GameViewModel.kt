package com.example.minesweeper

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

enum class GameMode {
    REVEAL, FLAG
}

class GameViewModel : ViewModel() {
    val gridSize = 16
    val mineCount = 45
    var isgameOver = mutableStateOf(false)
    var board = mutableStateListOf<Cell>()
    var isFirstClick = true

    init {
        generateEmptyboard()
    }

    private fun generateEmptyboard()
    {
        board.clear()
        for(r in 0 until gridSize){
            for(c in 0 until gridSize){
                board.add(Cell(row = r, col = c))
            }
        }
    }

    fun resetGame() {
        isgameOver.value = false
        isFirstClick = true
        generateEmptyboard()
    }

    fun onCellClick(clickedCell : Cell){
        if(isgameOver.value) return

        if(isFirstClick){
            setupMines(clickedCell)
            isFirstClick = false
        }
        if(clickedCell.isMine) {
            onGameOver()
        } else {
            revealCell(clickedCell)
        }
    }

    private fun onGameOver() {
        isgameOver.value = true

        for(i in board.indices) {
            if(board[i].isMine) {
                board[i] = board[i].copy(status = CellStatus.REVEALED)
            }
        }
    }

    private fun setupMines(startcell: Cell){
        val allPositions = board.filter { it != startcell}.shuffled()
        for(i in 0 until mineCount){
            val cell = allPositions[i]
            val index = board.indexOf(cell)
            board[index] = board[index].copy(isMine = true)
        }

        for(i in board.indices) {
            if(!board[i].isMine) {
                board[i] = board[i].copy(nearbyMines = countNearbymines(board[i]))
            }
        }
    }

    private fun countNearbymines(cell: Cell): Int {
        var count = 0
        for(dr in -1..1) {
            for(dc in -1..1) {
                val r = cell.row + dr
                val c = cell.col + dc
                if(r in 0 until gridSize && c in 0 until gridSize) {
                    if(board.find { it.row == r && it.col == c }?.isMine == true) {
                        count++
                    }
                }
            }
        }
        return count
    }

    private fun revealCell(cell : Cell) {
        val index = board.indexOf(cell)
        if(index == -1 || board[index].status == CellStatus.REVEALED) return

        board[index] = board[index].copy(status = CellStatus.REVEALED)

        if(board[index].nearbyMines == 0 && !board[index].isMine) {
            for(dr in -1..1) {
                for(dc in -1..1) {
                    val neighbor = board.find { it.row == cell.row + dr && it.col == cell.col + dc }
                    if(neighbor != null) revealCell(neighbor)
                }
            }
        }
    }

    fun onToggleFlag(cell: Cell) {
        if(isgameOver.value) return
        val index = board.indexOf(cell)
        if (index != -1 && board[index].status != CellStatus.REVEALED) {
            val currentStatus = board[index].status
            val newStatus = if (currentStatus == CellStatus.FLAGGED) CellStatus.HIDDEN else CellStatus.FLAGGED
            board[index] = board[index].copy(status = newStatus)
        }
    }

    var currentMode = mutableStateOf(GameMode.REVEAL)

    fun toggleMode(mode: GameMode) {
        currentMode.value = mode
    }

    fun handleCellInteraction(cell: Cell) {
        if(currentMode.value == GameMode.REVEAL) {
            onCellClick(cell)
        } else {
            onToggleFlag(cell)
        }
    }
}