import bagel.*;
import bagel.Font;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Collections;

/**
 * GameEndScreen class handles all the logic of the end of the game.
 * Renders the required screens and texts.
 */

public class GameEndScreen {
    // Constants related to properties that other variables refer to.
    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    // Constants related to rendering.
    private final Image BACKGROUND_IMAGE;
    private final String FONT_PATH;
    private final int SCORES_FONT_SIZE;
    private final int STATUS_FONT_SIZE;
    private final String SCORES_FILE;

    // Constants related to the texts from message_en.properties.
    private final String SCORES_TEXT;
    private final String WON_TEXT;
    private final String LOST_TEXT;

    // Constants related to Y coordinates for rendered texts.
    private final int SCORES_Y;
    private final int WON_LOST_Y;

    // Constants related to integers used in gameplay.
    private final double TARGET_SCORE;
    private final int MAX_NUM_SCORES;

    // Constant that defines the Y distance between rendered lines in the game end screen.
    private final int DISTANCE_BETWEEN_LINES;

    // Variables
    private final ArrayList<ScoreEntry> TOP_SCORES;
    private final String PLAYER_NAME;
    private final double PLAYER_SCORE;

    public GameEndScreen(String playerName, double playerScore,
                         Properties gameProperties, Properties messageProperties) {
        this.PLAYER_NAME = playerName;
        this.PLAYER_SCORE = playerScore;

        this.GAME_PROPS = gameProperties;
        this.MESSAGE_PROPS = messageProperties;

        BACKGROUND_IMAGE = new Image(GAME_PROPS.getProperty("backgroundImage.gameEnd"));
        FONT_PATH = GAME_PROPS.getProperty("font");
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

        // Need to load scores at the end since SCORES_FILE need to be assigned first.
        this.TOP_SCORES = loadTopScores();

        // Add current player score to score file (i.e. scores.csv)
        IOUtils.writeScoreToFile(SCORES_FILE, this.PLAYER_NAME + "," + this.PLAYER_SCORE);
    }

    /**
     * Renders the game end screen.
     */
    public void render() {
        BACKGROUND_IMAGE.draw(Window.getWidth() / 2.0, Window.getHeight() / 2.0);

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

        // Renders the won/lost text
        String winLoseText = PLAYER_SCORE >= TARGET_SCORE ? WON_TEXT : LOST_TEXT;
        winLoseFont.drawString(winLoseText, (Window.getWidth() -
                winLoseFont.getWidth(winLoseText)) / 2.0, WON_LOST_Y);
    }

    /**
     * Loads the scores file.
     * Sort all entries in descending order according to the score.
     * Takes the top 5 score to be shown on screen.
     */
    private ArrayList<ScoreEntry> loadTopScores() {
        ArrayList<ScoreEntry> scores = new ArrayList<>();
        String[][] scoreEntries = IOUtils.readCommaSeparatedFile(SCORES_FILE);

        for (String[] scoreEntry : scoreEntries) {
            String name = scoreEntry[0];
            double score = Double.parseDouble(scoreEntry[1]);
            scores.add(new ScoreEntry(name, score));
        }

        // Add current player name and score.
        scores.add(new ScoreEntry(PLAYER_NAME, PLAYER_SCORE));

        // Sort scores in descending order.
        Collections.sort(scores);

        int numOfRecords = scores.size();

        // Returns the 5 best scores. If there is less than 5 scores, all scores will be returned.
        return new ArrayList<>(scores.subList(0, Math.min(numOfRecords, MAX_NUM_SCORES)));
    }
}
