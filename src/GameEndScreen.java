import bagel.*;
import bagel.Font;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Collections;

/**
 * GameEndScreen class handles all the logic of the end of the game.
 * Renders the required screens and texts.
 */
public class GameEndScreen extends Screen {

    /**
     * The font size for displaying the scores.
     */
    private final int SCORES_FONT_SIZE;

    /**
     * The font size for displaying the win/loss status.
     */
    private final int STATUS_FONT_SIZE;

    /**
     * The file path for storing and retrieving scores.
     */
    private final String SCORES_FILE;

    /**
     * The text displayed for the highest scores section.
     */
    private final String SCORES_TEXT;

    /**
     * The text displayed when the player wins the game.
     */
    private final String WON_TEXT;

    /**
     * The text displayed when the player loses the game.
     */
    private final String LOST_TEXT;

    /**
     * The Y-coordinate for displaying the scores text.
     */
    private final int SCORES_Y;

    /**
     * The Y-coordinate for displaying the win/loss status text.
     */
    private final int WON_LOST_Y;

    /**
     * The target score the player needs to achieve to win the game.
     */
    private final double TARGET_SCORE;

    /**
     * The maximum number of top scores to display.
     */
    private final int MAX_NUM_SCORES;

    /**
     * The distance between lines of text drawn in the game end screen.
     */
    private final int DISTANCE_BETWEEN_LINES;

    /**
     * The list of top scores to be displayed on the game end screen.
     */
    private final ArrayList<ScoreEntry> TOP_SCORES;

    /**
     * The current player's name.
     */
    private final String PLAYER_NAME;

    /**
     * The current player's final score.
     */
    private final double PLAYER_SCORE;

    /**
     * Constructor for GameEndScreen class.
     * Initialises the current player name, current player score, as well as both game and message properties.
     * @param playerName The current player's name.
     * @param playerScore The current player's score.
     * @param gameProperties The properties object containing game configuration values.
     * @param messageProperties The properties object containing rendered text configuration values.
     */
    public GameEndScreen(String playerName, double playerScore,
                         Properties gameProperties, Properties messageProperties) {
        super(gameProperties, messageProperties, new Image(gameProperties.getProperty("backgroundImage.gameEnd")));

        this.PLAYER_NAME = playerName;
        this.PLAYER_SCORE = playerScore;

        SCORES_FONT_SIZE = Integer.parseInt(GAME_PROPS.getProperty("gameEnd.scores.fontSize"));
        STATUS_FONT_SIZE = Integer.parseInt(GAME_PROPS.getProperty("gameEnd.status.fontSize"));

        SCORES_TEXT = MESSAGE_PROPS.getProperty("gameEnd.highestScores");
        WON_TEXT = MESSAGE_PROPS.getProperty("gameEnd.won");
        LOST_TEXT = MESSAGE_PROPS.getProperty("gameEnd.lost");

        SCORES_Y = Integer.parseInt(GAME_PROPS.getProperty("gameEnd.scores.y"));
        WON_LOST_Y = Integer.parseInt(GAME_PROPS.getProperty("gameEnd.status.y"));

        TARGET_SCORE = Double.parseDouble(GAME_PROPS.getProperty("gamePlay.target"));

        SCORES_FILE = GAME_PROPS.getProperty("gameEnd.scoresFile");

        MAX_NUM_SCORES = 5;
        DISTANCE_BETWEEN_LINES = 40;

        // Load top scores after SCORES_FILE is initialized.
        this.TOP_SCORES = loadTopScores();

        // Write current player's score to the score file.
        IOUtils.writeScoreToFile(SCORES_FILE, this.PLAYER_NAME + "," + this.PLAYER_SCORE);
    }

    /**
     * Renders the game end screen, displaying the top scores and the win/loss status.
     */
    @Override
    public void draw() {
        super.draw();

        Font scoresFont = new Font(FONT_PATH, SCORES_FONT_SIZE);
        scoresFont.drawString(SCORES_TEXT, (Window.getWidth() -
                scoresFont.getWidth(SCORES_TEXT)) / 2.0, SCORES_Y);

        // Renders the top 5 scores
        int scorePositionY = SCORES_Y + DISTANCE_BETWEEN_LINES;
        for (ScoreEntry scoreEntry : TOP_SCORES) {
            String scoreText = scoreEntry.toString();
            scoresFont.drawString(scoreText, (Window.getWidth() -
                    scoresFont.getWidth(scoreText)) / 2.0, scorePositionY);
            scorePositionY += DISTANCE_BETWEEN_LINES;
        }

        Font winLoseFont = new Font(FONT_PATH, STATUS_FONT_SIZE);

        // Render the won/lost status text.
        String winLoseText = PLAYER_SCORE >= TARGET_SCORE ? WON_TEXT : LOST_TEXT;
        winLoseFont.drawString(winLoseText, (Window.getWidth() -
                winLoseFont.getWidth(winLoseText)) / 2.0, WON_LOST_Y);
    }

    /**
     * Loads the scores file.
     * Sort all entries in descending order according to the score.
     * Takes the top 5 score to be shown on screen.
     * @return A list of the top 5 scores to be displayed on the screen.
     */
    private ArrayList<ScoreEntry> loadTopScores() {
        ArrayList<ScoreEntry> scores = new ArrayList<>();
        String[][] scoreEntries = IOUtils.readCommaSeparatedFile(SCORES_FILE);

        for (String[] scoreEntry : scoreEntries) {
            String name = scoreEntry[0];
            double score = Double.parseDouble(scoreEntry[1]);
            scores.add(new ScoreEntry(name, score));
        }

        // Add the current player's name and score to the list.
        scores.add(new ScoreEntry(PLAYER_NAME, PLAYER_SCORE));

        // Sort scores in descending order.
        Collections.sort(scores);

        int numOfRecords = scores.size();

        // Return the top 5 scores (or fewer if there are less than 5).
        return new ArrayList<>(scores.subList(0, Math.min(numOfRecords, MAX_NUM_SCORES)));
    }
}
