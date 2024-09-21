import bagel.Font;
import java.util.Properties;

/**
 * GameStats class tracks the overall statistics of the game.
 * This includes total earnings and total frames remaining.
 * This class is also responsible for rendering the texts that describe these statistics in the game
 */
public class GameStats {
    // Constants related to font for rendering texts.
    private final String FONT_PATH;
    private final int FONT_SIZE;

    // Constants related to integers used in gameplay.
    private final double TARGET_SCORE;
    private final int MAX_FRAMES;

    // Constants related to X and Y coordinates of where the text is rendered.
    private final int MAX_FRAMES_X;
    private final int MAX_FRAMES_Y;
    private final int TARGET_X;
    private final int TARGET_Y;
    private final int EARNINGS_X;
    private final int EARNINGS_Y;

    // Constants related to the texts referred from message_en.properties.
    private final String EARNINGS_TEXT;
    private final String REM_FRAMES_TEXT;
    private final String TARGET_TEXT;

    // Constant that defines the change in remaining frames per unit time.
    private final int CHANGE_IN_FRAME;

    // Variables
    private double totalScore;
    private int remainingFrames;

    public GameStats(Properties gameProperties, Properties messageProperties) {
        this.totalScore = 0;

        TARGET_SCORE = Double.parseDouble(gameProperties.getProperty("gamePlay.target"));
        MAX_FRAMES = Integer.parseInt(gameProperties.getProperty("gamePlay.maxFrames"));

        FONT_PATH = gameProperties.getProperty("font");
        FONT_SIZE = Integer.parseInt(gameProperties.getProperty("gameplay.info.fontSize"));

        EARNINGS_X = Integer.parseInt(gameProperties.getProperty("gameplay.earnings.x"));
        EARNINGS_Y = Integer.parseInt(gameProperties.getProperty("gameplay.earnings.y"));
        TARGET_X = Integer.parseInt(gameProperties.getProperty("gameplay.target.x"));
        TARGET_Y = Integer.parseInt(gameProperties.getProperty("gameplay.target.y"));
        MAX_FRAMES_X = Integer.parseInt(gameProperties.getProperty("gameplay.maxFrames.x"));
        MAX_FRAMES_Y = Integer.parseInt(gameProperties.getProperty("gameplay.maxFrames.y"));

        EARNINGS_TEXT = messageProperties.getProperty("gamePlay.earnings");
        TARGET_TEXT = messageProperties.getProperty("gamePlay.target");
        REM_FRAMES_TEXT = messageProperties.getProperty("gamePlay.remFrames");

        CHANGE_IN_FRAME = 1;

        this.remainingFrames = MAX_FRAMES;
    }

    /**
     * Constantly updates the game stats and its rendered text
     */
    public void update() {
        draw();
        updateRemainingFrames();
    }

    /**
     * Accumulate trip earnings.
     */
    public void addTotalScore(double tripEarnings) {
        this.totalScore += tripEarnings;
    }

    /**
     * Constantly decrease the remaining frames, every second.
     */
    private void updateRemainingFrames() {
        this.remainingFrames -= CHANGE_IN_FRAME;
    }

    /**
     * Draw the top left game statistics, which includes.
     * Total score, target score, and total remaining frames.
     */
    private void draw() {
        Font font = new Font(FONT_PATH, FONT_SIZE);

        // Total score text.
        String totalScoreText = String.format(EARNINGS_TEXT + "%.2f", totalScore);
        font.drawString(totalScoreText, EARNINGS_X, EARNINGS_Y);

        // Target score text.
        String targetScoreText = String.format(TARGET_TEXT + "%.2f", TARGET_SCORE);
        font.drawString(targetScoreText, TARGET_X, TARGET_Y);

        // Remaining frames text.
        String remainingFramesText = String.format(REM_FRAMES_TEXT + "%d", remainingFrames);
        font.drawString(remainingFramesText, MAX_FRAMES_X, MAX_FRAMES_Y);
    }

    public int getRemainingFrames() {
        return remainingFrames;
    }

    public double getTotalScore() {
        return totalScore;
    }
}
