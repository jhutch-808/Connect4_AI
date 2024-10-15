import java.util.Arrays;
import java.util.Objects;

/**
 * Class to represent a Connect-4 game board (or Connect-2 or -3, or a different number).
 * Because we are creating a lot of boards, using bytes instead of ints will save memory.
 */
public class Board {
    // Dimensions of the board.
    private final byte numRows, numCols;

    // How many in a row it takes to win.
    private final byte consecToWin;

    // 2D array to hold the positions of the tokens on the board.
    // 1 used for MAX player, -1 for MIN player, 0 for empty square.
    // 0 is the "bottom" row of the board, boardRows-1 is the top row.
    public final byte[][] board;

    // The lowest-numbered free row in each column.  This is nice to keep track of
    // because it lets us know when we drop a token in a column where it will end up without searching.
    // If an entry in this array is == rows, that column is full.
    private final byte[] lowestFreeRow;

    // Player who moves next.
    private final Player playerToMove;

    // Total number of moves made so far in the game.  Should be equal to
    // number of tokens on the board.
    private final int movesMadeSoFar;

    // Is the board full?  Not technically necessary, but nice to have.
    private final boolean isBoardFull;

    // State of the game: in progress, or win/loss/tie.
    private final GameState gameState;

    /**
     * Constructs an empty starting board; MAX moves first.
     */
    public Board(int numRows, int numCols, int consecToWin) {
        this.numRows = (byte) numRows;
        this.numCols = (byte) numCols;
        this.consecToWin = (byte) consecToWin;

        board = new byte[numRows][numCols];
        lowestFreeRow = new byte[numCols];
        playerToMove = Player.MAX;
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++)
                board[r][c] = 0;
        }
        for (int c = 0; c < numCols; c++)
            lowestFreeRow[c] = 0;

        isBoardFull = false;
        movesMadeSoFar = 0;
        gameState = GameState.IN_PROGRESS;
    }

    /**
     * Construct a board from a previous state and a specified column to
     * drop a token in.  Private because users will access this through the
     * "makeMove" method.
     */
    private Board(Board prevBoard, int columnToDrop) {
        this.numRows = prevBoard.numRows;
        this.numCols = prevBoard.numCols;
        this.consecToWin = prevBoard.consecToWin;

        if (prevBoard.isColumnFull(columnToDrop))
            throw new IllegalArgumentException("Board is full in column " + columnToDrop);

        // copy the board
        board = new byte[prevBoard.numRows][prevBoard.numCols];
        for (int r = 0; r < numRows; r++) {
            System.arraycopy(prevBoard.board[r], 0, board[r], 0, numCols);
        }

        // copy the free rows array
        lowestFreeRow = new byte[numCols];
        System.arraycopy(prevBoard.lowestFreeRow, 0, lowestFreeRow, 0, numCols);

        playerToMove = prevBoard.getPlayerToMoveNext().otherPlayer();

        // make the move
        int rowOfNewToken = lowestFreeRow[columnToDrop];
        board[rowOfNewToken][columnToDrop] = (byte) (prevBoard.getPlayerToMoveNext() == Player.MAX ? 1 : -1);
        lowestFreeRow[columnToDrop]++;
        movesMadeSoFar = prevBoard.movesMadeSoFar + 1;

        isBoardFull = (movesMadeSoFar == numRows * numCols);
        gameState = calcStateOfGame();
    }

    /**
     * Called only by constructor to determine the winner if there is one.
     * Assumes lowestFreeRow array and isBoardFull are set correctly.
     */
    private GameState calcStateOfGame() {
        // check for wins
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                if (board[r][c] == 0) {
                    continue;
                }

                if ((c <= numCols - consecToWin && allMatchInARow(r, c))
                        || (r <= numRows - consecToWin && allMatchInAColumn(r, c))
                        || (r <= numRows - consecToWin && c <= numCols - consecToWin && allMatchInANorthEastDiagonal(r, c))
                        || (r <= numRows - consecToWin && c - consecToWin >= -1 && allMatchInANorthWestDiagonal(r, c))) {
                    if (board[r][c] == 1) {
                        return GameState.MAX_WIN;
                    } else if (board[r][c] == -1) {
                        return GameState.MIN_WIN;
                    }
                }
            }
        }

        // if we get here, there was no win, so either it's a tie or in progress.
        if (isBoardFull)
            return GameState.TIE;
        else
            return GameState.IN_PROGRESS;
    }

    /** Creates a new board based on this board, but with one additional move. */
    public Board makeMove(int col) {
        return new Board(this, col);
    }

    /**
     * Returns true if a desired column is full (and can't have any more tokens in it).
     */
    public boolean isColumnFull(int col) {
        return lowestFreeRow[col] == numRows;
    }

    /**
     * Return the number of moves made in the game so far.
     */
    public int getNumberOfMoves() {
        return movesMadeSoFar;
    }

    /** Return the number of rows in the board. */
    public int getRows() {
        return numRows;
    }

    /** Return the number of columns in the board. */
    public int getCols() {
        return numCols;
    }

    /** Return the player who will move next.  */
    public Player getPlayerToMoveNext() {
        return playerToMove;
    }

    /**
     * Return the state of the game (in progress, win for max, win for min, or tie).
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Return the winner if there is one.
     */
    public Player getWinner() {
        if (gameState == GameState.IN_PROGRESS)
            throw new IllegalStateException("Can't get winner for game in progress.");
        else if (gameState == GameState.TIE)
            throw new IllegalStateException("Can't get winner for tie game.");

        if (gameState == GameState.MAX_WIN)
            return Player.MAX;
        else
            return Player.MIN;
    }

    /**
     * Return if the board has a winner.
     */
    public boolean hasWinner() {
        return gameState == GameState.MAX_WIN || gameState == GameState.MIN_WIN;
    }

    /**
     * Return the board as a nicely-formatted, 2D string.
     * @return
     */
    public String to2DString() {
        StringBuilder sb = new StringBuilder();
        for (int r = numRows - 1; r >= 0; r--) {
            for (int c = 0; c < numCols; c++) {
                if (board[r][c] == 1)  // max is 1
                    sb.append("X ");
                else if (board[r][c] == -1) // min is -1
                    sb.append("O ");
                else
                    sb.append(". ");
            }
            sb.append("\n");
        }
        sb.append("0 1 2 3 4 5 6 7 8 9", 0, numCols * 2);
        return sb.toString();//  + "full=" + boardIsFull + " winner=" + winner;
    }

    /**
     * Returns a single-line representation of the board, useful for debugging.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int r = numRows - 1; r >= 0; r--) {
            for (int c = 0; c < numCols; c++) {
                if (board[r][c] == 1)  // max is 1
                    sb.append("X");
                else if (board[r][c] == -1) // min is -1
                    sb.append("O");
                else
                    sb.append(".");
            }
            sb.append("|");
        }
        return sb.toString();//  + "full=" + boardIsFull + " winner=" + winner;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Board other = (Board) obj;
        if (this.playerToMove != other.playerToMove) {
            return false;
        }
        return Arrays.deepEquals(this.board, other.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(board), playerToMove);
    }

    ///////////////// internal functions below here for detecting wins
    private boolean allMatchInARow(int row, int startcol) {
        for (int x = 0; x < consecToWin - 1; x++) {
            if (board[row][startcol + x] != board[row][startcol + x + 1])
                return false;
        }
        return true;
    }

    private boolean allMatchInAColumn(int startrow, int col) {
        for (int x = 0; x < consecToWin - 1; x++) {
            if (board[startrow + x][col] != board[startrow + x + 1][col])
                return false;
        }
        return true;
    }

    private boolean allMatchInANorthEastDiagonal(int startrow, int startcol) {
        for (int x = 0; x < consecToWin - 1; x++) {
            if (board[startrow + x][startcol + x] != board[startrow + x + 1][startcol + x + 1])
                return false;
        }
        return true;
    }

    private boolean allMatchInANorthWestDiagonal(int startrow, int startcol) {
        for (int x = 0; x < consecToWin - 1; x++) {
            if (board[startrow + x][startcol - x] != board[startrow + x + 1][startcol - x - 1])
                return false;
        }
        return true;
    }
}
