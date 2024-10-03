import bagel.Font;
import java.util.Properties;

/**
 * GameStats class tracks the overall statistics of the game.
 * This includes total earnings and total frames remaining.
 * This class is also responsible for rendering the texts that describe these statistics in the game
 */
public class GameStats {
    /**
     * The path to the font file used for rendering texts.
     */
    private final String FONT_PATH;

    /**
     * The font size used for rendering texts.
     */
    private final int FONT_SIZE;

    /**
     * The target score the player must reach during gameplay to win.
     */
    private final double TARGET_SCORE;

    /**
     * The maximum number of frames allowed in the game.
     * Game ends in a lost if target score is not reached within this timeframe.
     */
    private final int MAX_FRAMES;

    /**
     * The X-coordinate where the remaining frames text is rendered.
     */
    private final int MAX_FRAMES_X;

    /**
     * The Y-coordinate where the remaining frames text is rendered.
     */
    private final int MAX_FRAMES_Y;

    /**
     * The X-coordinate where the target score text is rendered.
     */
    private final int TARGET_X;

    /**
     * The Y-coordinate where the target score text is rendered.
     */
    private final int TARGET_Y;

    /**
     * The X-coordinate where the earnings text is rendered.
     */
    private final int EARNINGS_X;

    /**
     * The Y-coordinate where the earnings text is rendered.
     */
    private final int EARNINGS_Y;

    /**
     * The label text for displaying earnings.
     */
    private final String EARNINGS_TEXT;

    /**
     * The label text for displaying the remaining frames.
     */
    private final String REM_FRAMES_TEXT;

    /**
     * The label text for displaying the target score.
     */
    private final String TARGET_TEXT;

    /**
     * The amount of remaining frames that decrease per unit time.
     */
    private final int CHANGE_IN_FRAME;

    /**
     * The current gameplay total score.
     */
    private double totalScore;

    /**
     * The number of frames remaining in the game.
     */
    private int remainingFrames;

    /**
     * Constructor for GameStats class.
     * Initialises the initial total score to 0, and set remaining frames as the maximum number of frames.
     * Initialises the various text and (x, y) coordinates related to displaying game statistics too.
     * @param gameProps The properties object containing game configuration values.
     * @param messageProps The properties object containing text configuration values.
     */
    public GameStats(Properties gameProps, Properties messageProps) {
        TARGET_SCORE = Double.parseDouble(gameProps.getProperty("gamePlay.target"));
        MAX_FRAMES = Integer.parseInt(gameProps.getProperty("gamePlay.maxFrames"));

        FONT_PATH = gameProps.getProperty("font");
        FONT_SIZE = Integer.parseInt(gameProps.getProperty("gamePlay.info.fontSize"));

        EARNINGS_X = Integer.parseInt(gameProps.getProperty("gamePlay.earnings.x"));
        EARNINGS_Y = Integer.parseInt(gameProps.getProperty("gamePlay.earnings.y"));
        TARGET_X = Integer.parseInt(gameProps.getProperty("gamePlay.target.x"));
        TARGET_Y = Integer.parseInt(gameProps.getProperty("gamePlay.target.y"));
        MAX_FRAMES_X = Integer.parseInt(gameProps.getProperty("gamePlay.maxFrames.x"));
        MAX_FRAMES_Y = Integer.parseInt(gameProps.getProperty("gamePlay.maxFrames.y"));

        EARNINGS_TEXT = messageProps.getProperty("gamePlay.earnings");
        TARGET_TEXT = messageProps.getProperty("gamePlay.target");
        REM_FRAMES_TEXT = messageProps.getProperty("gamePlay.remFrames");

        CHANGE_IN_FRAME = 1;

        this.totalScore = 0;
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
     * @param tripEarnings The earnings of last trip to be added to the total earnings.
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

    /**
     * Gets the remaining maximum game frames.
     * @return The remaining game frames before the game must end in a lost.
     */
    public int getRemainingFrames() {
        return remainingFrames;
    }

    /**
     * Gets the current total score.
     * @return The current total score.
     */
    public double getTotalScore() {
        return totalScore;
    }
}
