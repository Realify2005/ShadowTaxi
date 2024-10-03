/**
 * Helper class for the ScoreEntry "tuple" for loading, comparing, and rendering top 5 scores in game end screen
 * Includes playerName and playerScore
 */
public class ScoreEntry implements Comparable<ScoreEntry> {

    /**
     * The name of the player in this score entry.
     */
    private final String PLAYER_NAME;

    /**
     * The score of the player in this score entry.
     */
    private final double PLAYER_SCORE;

    /**
     * Constructor for ScoreEntry type.
     * Initialises the type with given player name and player score.
     * @param playerName The name of the player.
     * @param playerScore The score of the player.
     */
    public ScoreEntry(String playerName, double playerScore) {
        this.PLAYER_NAME = playerName;
        this.PLAYER_SCORE = playerScore;
    }

    /**
     * Compares this ScoreEntry with another ScoreEntry to determine the sorting order.
     * The comparison is done in descending order of the player score.
     * @param other The other ScoreEntry to be compared to.
     * @return negative integer if this ScoreEntry's score is greater than the other ScoreEntry's score.
     *         zero if this ScoreEntry's score is equal to the other ScoreEntry's score.
     *         positive integer if this ScoreEntry's score is less than the other ScoreEntry's score.
     */
    @Override
    public int compareTo(ScoreEntry other) {
        // Sort score in descending order
        return Double.compare(other.PLAYER_SCORE, this.PLAYER_SCORE);
    }

    /**
     * Returns a string representation of this ScoreEntry to be displayed in Game End Screen for top 5 scores.
     * The format is: "playerName - score".
     * @return A string representation of this ScoreEntry.
     */
    @Override
    public String toString() {
        return PLAYER_NAME + " - " + String.format("%.2f", PLAYER_SCORE);
    }
}
