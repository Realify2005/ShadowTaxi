/**
 * Helper class for the ScoreEntry "tuple" for loading, comparing, and rendering top 5 scores in game end screen
 * Includes playerName and playerScore
 */
public class ScoreEntry implements Comparable<ScoreEntry> {
    private final String PLAYER_NAME;
    private final double PLAYER_SCORE;

    public ScoreEntry(String playerName, double playerScore) {
        this.PLAYER_NAME = playerName;
        this.PLAYER_SCORE = playerScore;
    }

    public String getPlayerName() {
        return PLAYER_NAME;
    }

    public double getPlayerScore() {
        return PLAYER_SCORE;
    }

    @Override
    public int compareTo(ScoreEntry other) {
        // Sort score in descending order
        return Double.compare(other.PLAYER_SCORE, this.PLAYER_SCORE);
    }

    @Override
    public String toString() {
        return PLAYER_NAME + " - " + String.format("%.2f", PLAYER_SCORE);
    }
}
