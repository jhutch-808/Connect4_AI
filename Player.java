/**
 * The player in a minimax-style game.
 */
public enum Player {
    MAX(1), MIN(-1);

    private final int number;

    Player(int n) {
        number = n;
    }

    public Player otherPlayer() {
        if (this == MAX) return MIN;
        return MAX;
    }

    /** Returns +1 for MAX, -1 for MIN.
     */
    public int getNumber() {
        return number;
    }
}
