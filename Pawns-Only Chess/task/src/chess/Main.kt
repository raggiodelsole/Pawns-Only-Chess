package chess

var firstPlayerName = "";
var secondPlayerName = "";
var currentPlayerName = "";
val letters = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')
const val boardLine = "  +---+---+---+---+---+---+---+---+"
var board: MutableMap<String, String> = mutableMapOf()
val moveRegex = "[a-h][1-8][a-h][1-8]".toRegex()
var lastMove = ""
var isEnPassant = false

fun main() {
    initBoardAndPlayers()
    game()
}

fun game() {
    while (true) {
        println("$currentPlayerName's turn:")
        val playerMove = readln()
        if (playerMove != "exit") {
            if (!isCurrentPlayerPiece(playerMove)) {
                printNoPawsOnStartingPoint(playerMove)
            } else if (moveRegex.matches(playerMove) && isNotColumnHopper(playerMove) && isMoveAllowed(playerMove)) {
                performMove(playerMove)
                printCurrentBoard()
                if (isGameFinished(playerMove)) {
                    println("Bye!")
                    return
                }
                cleanUp(playerMove)

            } else {
                println("Invalid input")
            }
        } else {
            println("Bye!")
            return
        }
    }
}

fun isGameFinished(playerMove: String): Boolean {
    if (isPawnInFinalRow(playerMove) || isOpponentWithoutPawns(playerMove)) {
        printPlayerWonMessage(playerMove)
        return true
    }
    if (!isNextPlayerHavingMove(playerMove)) {
        println("Stalemate!")
        return true
    }
    return false
}

fun isNextPlayerHavingMove(playerMove: String): Boolean {
    var nextPlayerSymbol = "B"
    if (board[playerMove.substring(2)] == "B") {
        nextPlayerSymbol = "W"
    }
    return isPlayerHavingMove(nextPlayerSymbol)
}

fun isPlayerHavingMove(nextPlayerSymbol: String): Boolean {
    for ((key, value) in board) {
        if (nextPlayerSymbol == value) {
            if (ifMovePossibleForPosition(key, nextPlayerSymbol)) {
                return true
            }
        }
    }
    return false
}

fun ifMovePossibleForPosition(position: String, nextPlayerSymbol: String): Boolean {
    return isFreeFieldAhead(position, nextPlayerSymbol) || isTakingPossible(position)
}

fun isTakingPossible(position: String): Boolean {
    val currentPlayer = board[position]
    if (currentPlayer == "W") {
        return isTakingAsWhitePossible(position)
    } else {
        return isTakingAsBlackPossible(position)
    }
}

fun isTakingAsWhitePossible(position: String): Boolean {
    val row = position.substring(1).toInt() + 1
    val right = position.substring(0, 1).toCharArray()[0].plus(1).toString() + row
    val left = position.substring(0, 1).toCharArray()[0].minus(1).toString() + row
    if (board.contains(right) && board[right] == "B") {
        return true
    }
    if (board.contains(left) && board[left] == "B") {
        return true
    }
    return false
}

fun isTakingAsBlackPossible(position: String): Boolean {
    val row = position.substring(1).toInt() - 1
    val right = position.substring(0, 1).toCharArray()[0].plus(1).toString() + row
    val left = position.substring(0, 1).toCharArray()[0].minus(1).toString() + row
    if (board.contains(right) && board[right] == "W") {
        return true
    }
    if (board.contains(left) && board[left] == "W") {
        return true
    }
    return false
}

fun isFreeFieldAhead(position: String, nextPlayerSymbol: String): Boolean {
    if (nextPlayerSymbol == "W") {
        return board[position.substring(0, 1) + (position.substring(1).toInt() + 1)] == " "
    }
    return board[position.substring(0, 1) + (position.substring(1).toInt() - 1)] == " "


}

fun printPlayerWonMessage(playerMove: String) {
    val currentPlayerSymbol = board[playerMove.substring(2)]
    var winner = "White"
    if (currentPlayerSymbol == "B") {
        winner = "Black"
    }
    println("$winner Wins!")
}

fun isOpponentWithoutPawns(playerMove: String): Boolean {
    val currentPlayerChar = board[playerMove.substring(2)]
    var enemyChar = "B"
    if (currentPlayerChar == "B") {
        enemyChar = "W"
    }
    return !board.containsValue(enemyChar)
}

private fun isPawnInFinalRow(playerMove: String): Boolean {
    val currentPlayerChar = board[playerMove.substring(2)]
    return if (currentPlayerChar == "W") {
        isWhiteInFinalRow(playerMove)
    } else {
        isBlackInFinalRow(playerMove)
    }
}

fun isWhiteInFinalRow(playerMove: String): Boolean {
    return playerMove.substring(3) == "8"
}

fun isBlackInFinalRow(playerMove: String): Boolean {
    return playerMove.substring(3) == "1"
}

fun isNotColumnHopper(playerMove: String): Boolean {
    if (playerMove.substring(0, 1) == "a") {
        return listOf("a", "b").contains(playerMove.substring(2, 3))
    }
    if (playerMove.substring(0, 1) == "b") {
        return listOf("a", "b", "c").contains(playerMove.substring(2, 3))
    }
    if (playerMove.substring(0, 1) == "c") {
        return listOf("b", "c", "d").contains(playerMove.substring(2, 3))
    }
    if (playerMove.substring(0, 1) == "d") {
        return listOf("c", "d", "e").contains(playerMove.substring(2, 3))
    }
    if (playerMove.substring(0, 1) == "e") {
        return listOf("d", "e", "f").contains(playerMove.substring(2, 3))
    }
    if (playerMove.substring(0, 1) == "f") {
        return listOf("e", "f", "g").contains(playerMove.substring(2, 3))
    }
    return if (playerMove.substring(0, 1) == "g") {
        listOf("f", "g", "h").contains(playerMove.substring(2, 3))
    } else {
        listOf("g", "h").contains(playerMove.substring(2, 3))

    }
}

private fun cleanUp(playerMove: String) {
    changePlayer()
    lastMove = playerMove
    isEnPassant = false
}

fun printCurrentBoard() {
    printFullBoard(board)
}

fun performMove(playerMove: String) {
    val selectCurrentPlayerChar = selectCurrentPlayerChar()
    val startPosition = playerMove.substring(0, 2)
    val endPosition = playerMove.substring(2)
    board.replace(startPosition, " ")
    board.replace(endPosition, selectCurrentPlayerChar)
    if (isEnPassant) {
        board.replace(lastMove.substring(2), " ")
    }
}

fun changePlayer() {
    currentPlayerName = if (currentPlayerName == firstPlayerName) {
        secondPlayerName
    } else {
        firstPlayerName
    }
}

fun isMoveAllowed(playerMove: String): Boolean {
    val playerChar = selectCurrentPlayerChar()
    if (isDestinationFree(playerMove.substring(2)) && isInSameColumn(playerMove)) {
        return isCorrectlyMovingForward(playerMove, playerChar)
    }

    return isCorrectBeating(playerMove, playerChar)

}

fun isInSameColumn(playerMove: String): Boolean {
    return playerMove.substring(0, 1) == playerMove.substring(2, 3)
}

fun printNoPawsOnStartingPoint(playerMove: String) {
    var currentColor = "white"
    val startingPoint = playerMove.substring(0, 2)
    if (currentPlayerName == secondPlayerName) {
        currentColor = "black"
    }
    println("No $currentColor pawn at $startingPoint")
}

fun isCorrectBeating(playerMove: String, playerChar: String): Boolean {

    if (playerChar == "W") {
        if (isWhiteEnPassant(playerMove)) {
            return true
        }
        return board[playerMove.substring(2)] == "B" && isWhiteBeatingForward(playerMove)
    }
    if (playerChar == "B") {
        if (isBlackEnPassant(playerMove)) {
            return true
        }
        return board[playerMove.substring(2)] == "W" && isBlackBeatingForward(playerMove)
    }
    return false

}

fun isBlackEnPassant(playerMove: String): Boolean {
    if (lastMoveWasInitialMove() && isBlackDoingEnPassant(playerMove)) {
        isEnPassant = true
        return true
    }
    return false
}

fun isWhiteDoingEnPassant(playerMove: String): Boolean {
    return playerMove.substring(2) == (lastMove.substring(2, 3) + 6)
}


fun isBlackDoingEnPassant(playerMove: String): Boolean {
    return playerMove.substring(2) == (lastMove.substring(2, 3) + 3)
}

private fun lastMoveWasInitialMove() =
    lastMove.substring(1, 2).toInt() + 2 == lastMove.substring(3).toInt() || lastMove.substring(1, 2)
        .toInt() - 2 == lastMove.substring(3).toInt()


fun isWhiteEnPassant(playerMove: String): Boolean {
    if (lastMove.isNotEmpty() && lastMoveWasInitialMove() && isWhiteDoingEnPassant(playerMove)) {
        isEnPassant = true
        return true
    }
    return false

}

fun isWhiteBeatingForward(playerMove: String): Boolean {
    val initialField = playerMove.substring(0, 2)
    val possibleBeatingFields = getPossibleWhiteBeatingFields(initialField)
    return possibleBeatingFields.contains(playerMove.substring(2))
}

fun isBlackBeatingForward(playerMove: String): Boolean {
    val initialField = playerMove.substring(0, 2)
    val possibleBeatingFields = getPossibleBlackBeatingFields(initialField)
    return possibleBeatingFields.contains(playerMove.substring(2))
}

private fun getPossibleWhiteBeatingFields(initialField: String): List<String> {
    val possibleLetters = listOf(initialField[0].minus(1), initialField[0].plus(1))
    val possibleValue1: String = (possibleLetters[0] + "" + initialField[1].plus(1))
    val possibleValue2: String = (possibleLetters[1] + "" + initialField[1].plus(1))
    return listOf(possibleValue1, possibleValue2)
}

private fun getPossibleBlackBeatingFields(initialField: String): List<String> {
    val possibleLetters = listOf(initialField[0].minus(1), initialField[0].plus(1))
    val possibleValue1: String = (possibleLetters[0] + "" + (initialField[1].minus(1)))
    val possibleValue2: String = (possibleLetters[1] + "" + initialField[1].minus(1))
    return listOf(possibleValue1, possibleValue2)
}

fun isCorrectlyMovingForward(playerMove: String, s: String): Boolean {
    val startNumber = playerMove.substring(1, 2).toInt()
    val endNumber = playerMove.substring(3).toInt()
    val startLetter = playerMove.substring(0, 1)
    val endLetter = playerMove.substring(2, 3)
    if (s == "W" && (isNormalForwardMoveForWhite(startNumber, endNumber, startLetter, endLetter) || isWhiteInitialMove(
            startNumber,
            endNumber,
            startLetter,
            endLetter
        ))
    )
        return isDestinationFree(playerMove.substring(2))

    if (s == "B" && (isNormalForwardMoveForBlack(startNumber, endNumber, startLetter, endLetter) || isBlackInitialMove(
            startNumber,
            endNumber, startLetter, endLetter
        ))
    ) {
        return isDestinationFree(playerMove.substring(2))
    }
    return false
}

fun isDestinationFree(substring: String): Boolean {
    return board[substring] == " "
}

private fun isWhiteInitialMove(startNumber: Int, endNumber: Int, startLetter: String, endLetter: String) =
    (startNumber == 2 && endNumber == 4 && startLetter == endLetter)

private fun isBlackInitialMove(startNumber: Int, endNumber: Int, startLetter: String, endLetter: String) =
    (startNumber == 7 && endNumber == 5 && startLetter == endLetter)

private fun isNormalForwardMoveForWhite(startNumber: Int, endNumber: Int, startLetter: String, endLetter: String) =
    startNumber + 1 == endNumber && startLetter == endLetter

private fun isNormalForwardMoveForBlack(startNumber: Int, endNumber: Int, startLetter: String, endLetter: String) =
    startNumber - 1 == endNumber && startLetter == endLetter

fun selectCurrentPlayerChar(): String {
    return if (currentPlayerName == firstPlayerName) {
        "W"
    } else {
        "B"
    }
}

private fun isCurrentPlayerPiece(playerMove: String): Boolean {
    val playerChar = selectCurrentPlayerChar()
    val stringAtStartingField = board[playerMove.substring(0, 2)]
    return stringAtStartingField == playerChar
}


private fun initBoardAndPlayers() {
    println("Pawns-Only Chess")
    println("First Player's name:")
    firstPlayerName = readln()
    println("Second Player's name:")
    secondPlayerName = readln()
    currentPlayerName = firstPlayerName
    board = createBoard()
    printFullBoard(board)
}

fun createBoard(): MutableMap<String, String> {
    val board = mutableMapOf<String, String>()
    for (letter in letters) {
        for (number in 1..8) {
            board["" + letter + number] = " ";
        }
    }
    initializeBoard(board)
    return board;

}

fun initializeBoard(board: MutableMap<String, String>) {
    board.forEach { (k) ->
        run {
            if (k.contains("7")) {
                board[k] = "B"
            }
            if (k.contains("2")) {
                board[k] = "W"
            }
        }
    }
}


fun printFullBoard(gameBoard: MutableMap<String, String>) {
    println(boardLine)
    for (n in 8 downTo 1) {
        process5Rows(n, gameBoard)
    }
    printLastLine()
}

fun printLastLine() {
    println("    ${letters[0]}   ${letters[1]}   ${letters[2]}   ${letters[3]}   ${letters[4]}   ${letters[5]}   ${letters[6]}   ${letters[7]}")
}

fun process5Rows(number: Int, gameBoard: MutableMap<String, String>) {
    val lineWithValues =
        "$number | " + gameBoard["a$number"] + " | " + gameBoard["b$number"] + " | " + gameBoard["c$number"] + " | " + gameBoard["d$number"] + " | " + gameBoard["e$number"] + " | " + gameBoard["f$number"] + " | " + gameBoard["g$number"] + " | " + gameBoard["h$number"] + " | "

    println(lineWithValues)
    println(boardLine)
}
