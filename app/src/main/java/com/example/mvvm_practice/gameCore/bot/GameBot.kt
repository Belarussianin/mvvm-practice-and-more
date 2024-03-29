package com.example.mvvm_practice.gameCore.bot

import android.util.Log
import com.example.mvvm_practice.extras.TAG
import com.example.mvvm_practice.extras.contains
import com.example.mvvm_practice.extras.containsStartAt
import com.example.mvvm_practice.extras.print
import com.example.mvvm_practice.gameCore.Game
import com.example.mvvm_practice.gameCore.GameData.*
import com.example.mvvm_practice.gameCore.GameData.Companion.gameModeToInt
import com.example.mvvm_practice.gameCore.GameData.Companion.playerToCellState
import com.example.mvvm_practice.gameCore.GameData.Companion.positionIntoIndex
import com.example.mvvm_practice.gameCore.GameData.Companion.switchPlayer
import com.example.mvvm_practice.gameCore.bot.GameBotData.Mode

class GameBot(
    private val game: Game,
    private var mode: Mode,
    private var botPlayer: Player
) {
    fun makeMove(): Boolean {
        if (game.currentPlayer.value == botPlayer && game.state.value == GameState.GAME) {
            game.makeTurn(getIndexToMoveOrNull() ?: -1)
            //game.field.value?.print()
            return true
        }
        return false
    }

    fun getIndexToMoveOrNull(): Int? {
        val field = game.field.value ?: Companion.standard_game_field
        val state = game.state.value
        var indexToMove: Int? = 55
        //Checking all conditions only if game is play
        if (state == GameState.GAME) {
            when (mode) {
                Mode.EASY -> {
                    //Move to closest empty cell
                    indexToMove = closestEmptyMoveIndexOrNull()
                }
                Mode.MEDIUM -> {
                    //Move to closest empty cell
                    indexToMove = closestEmptyMoveIndexOrNull()
                    //Check player win
                    indexToMove = getIndexToWinOrNull(switchPlayer(botPlayer)) ?: indexToMove
                }
                Mode.HARD -> {
                    //Check empty cells
                    if (closestEmptyMoveIndexOrNull() != null) {
                        //Move to closest empty cell
                        indexToMove = closestEmptyMoveIndexOrNull() ?: indexToMove
                        //Move to closest empty corner if player have center
                        //Log.i(TAG, "getIndexToMoveOrNull: $indexToMove")
                        indexToMove = getClosestEmptyCornerOrNull() ?: indexToMove
                        //Check player move to center
                        Log.i(TAG, "getIndexToMoveOrNull: $indexToMove")
                        indexToMove = getIndexToPreventPlayerWinOrNull() ?: indexToMove
                        //Check player win
                        Log.i(TAG, "getIndexToMoveOrNull: $indexToMove")
                        indexToMove = if(checkIsEmpty(getIndexToWinOrNull(switchPlayer(botPlayer)))) getIndexToWinOrNull(switchPlayer(botPlayer)) else indexToMove
                        //TODO CHECK ISEMPTY
                        //Check center
                        Log.i(TAG, "getIndexToMoveOrNull: $indexToMove center: ${field[1][1].state}")

                        //if (game.getMode() == GameMode.THREE_TO_THREE) {
                        if (field[1][1].state == GameCellState.EMPTY) {
                            indexToMove = positionIntoIndex(1 to 1, field.size)
                        }
                        //}

                        //Check bot win
                        indexToMove = if(checkIsEmpty(getIndexToWinOrNull(botPlayer))) getIndexToWinOrNull(botPlayer) else indexToMove

                        Log.i(TAG, "getIndexToMoveOrNull: $indexToMove")
                    }
                }
            }
        }
        Log.i(TAG, "getIndexToMoveOrNull: $indexToMove")
        return indexToMove
    }

    fun checkIsEmpty(cellIndex: Int?): Boolean {
        if (cellIndex == null) return false
        val (row, column) = Companion.indexIntoPosition(cellIndex, 3)
        if (game.field.value?.get(row)?.get(column)?.state == GameCellState.EMPTY) return true
        return false
    }

    //TODO
    //TODO scalability
    //TODO

    private fun closestEmptyMoveIndexOrNull(): Int? {
        val field = game.field.value!!
        for (row in field.indices) {
            for (column in field[row].indices) {
                if (field[row][column].state == GameCellState.EMPTY) {
                    Log.e(
                        TAG,
                        "closestEmptyMoveIndexOrNull: ${
                            positionIntoIndex(
                                row to column,
                                field.size
                            )
                        }"
                    )
                    return positionIntoIndex(row to column, field.size)
                }
            }
        }
        return 55
        //return null //If no empty cells
    }

    private fun getClosestEmptyCornerOrNull(): Int? {
        val field = game.field.value!!
        if (field[0][0].state == GameCellState.EMPTY) return positionIntoIndex(0 to 0, field.size)
        if (field[0][2].state == GameCellState.EMPTY) return positionIntoIndex(0 to 2, field.size)
        if (field[2][0].state == GameCellState.EMPTY) return positionIntoIndex(2 to 0, field.size)
        if (field[2][2].state == GameCellState.EMPTY) return positionIntoIndex(2 to 2, field.size)
        return null
    }

    private fun getIndexToPreventPlayerWinOrNull(): Int? {
        val field = game.field.value!!
        val playerCellState: GameCellState = playerToCellState(switchPlayer(botPlayer))
        //Check 8 combinations of player moves after center
        if (field[1][1].state == playerToCellState(botPlayer)) {
            if (field[0][0].state == playerCellState && field[2][1].state == playerCellState) {
                if (field[1][0].state == GameCellState.EMPTY) {
                    return positionIntoIndex(1 to 0, field.size)
                }
            }
            if (field[0][0].state == playerCellState && field[1][2].state == playerCellState) {
                if (field[0][1].state == GameCellState.EMPTY) {
                    return positionIntoIndex(0 to 1, field.size)
                }
            }
            if (field[2][0].state == playerCellState && field[1][2].state == playerCellState) {
                if (field[2][1].state == GameCellState.EMPTY) {
                    return positionIntoIndex(2 to 1, field.size)
                }
            }
            if (field[0][1].state == playerCellState && field[2][2].state == playerCellState) {
                if (field[1][2].state == GameCellState.EMPTY) {
                    return positionIntoIndex(1 to 2, field.size)
                }
            }
        }
        return null
    }

//TODO
//TODO
//TODO

    private fun generateWinRows(
        checkSize: Int,
        cellStateToCheck: GameCellState
    ): Array<Array<GameCell>> {
        var winRows: Array<Array<GameCell>> = arrayOf()
        for (i in 0 until checkSize) {
            winRows = winRows.plusElement(
                Array(checkSize) { index ->
                    if (index == i) {
                        GameCell(GameCellState.EMPTY)
                    } else {
                        GameCell(cellStateToCheck)
                    }
                }
            )
        }
        Log.e(TAG, "generateWinRows: ${winRows.print()}")
        return winRows
    }

//TODO
//TODO scalability,
// MANY DIAGS CHECK, IF THERE IS MORE THAN 2 DIAGONALS

    private fun getIndexToWinOrNull(playerToCheck: Player): Int? {
        val field = game.field.value!!
        val cellStateToCheck: GameCellState = playerToCellState(playerToCheck)
        //Game have 2 modes to check 3x3 and 5x5
        //TODO REMOVE HARDCORED SHIT
        val winRows = generateWinRows(gameModeToInt(GameMode.THREE_TO_THREE), cellStateToCheck)
        //Checking rows and columns
        for (rowIndex in field.indices) {
            for (winRowIndex in winRows.indices) {
                //Check row
                if (field[rowIndex].contains(winRows[winRowIndex])) {
                    return positionIntoIndex(rowIndex to winRowIndex, field.size)
                }
                //Check column
                if (Array(field.size) { columnIndex ->
                        field[columnIndex][rowIndex]
                    }.contains(winRows[winRowIndex])) {
                    return positionIntoIndex(winRowIndex to rowIndex, field.size)
                }
            }
        }
        //Checking diagonals
        //Main diag From top to bottom
        val firstDiag: Array<GameCell> = Array(field.size) { index ->
            field[index][index]
        }
        //From bottom to top
        var secondDiag: Array<GameCell> = emptyArray()
        for (rowIndex in field.indices.reversed()) {
            secondDiag = secondDiag.plusElement(field[rowIndex][field.lastIndex - rowIndex])
        }
        for (winRowIndex in winRows.indices) {
            //first
            if (firstDiag.contains(winRows[winRowIndex])) {
                val winPosition = firstDiag.containsStartAt(winRows[winRowIndex])!! + winRowIndex
                return positionIntoIndex(winPosition to winPosition, field.size)
            }
            //second
            if (secondDiag.contains(winRows[winRowIndex])) {
                val winPosition = secondDiag.containsStartAt(winRows[winRowIndex])!! + winRowIndex
                return positionIntoIndex(2 - winPosition to winRowIndex, field.size)
            }
        }
        return null
    }
}