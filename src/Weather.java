/**
 * Class representing the weather type.
 * Each weather type consists of information regarding the start frame, end frame, and the weather associated with it.
 */
public class Weather {

    /**
     * The type of weather (sunny/rainy).
     */
    private final String TYPE;

    /**
     * The frame at which the weather condition starts.
     */
    private final int START_FRAME;

    /**
     * The frame at which the weather condition ends.
     */
    private final int END_FRAME;

    /**
     * Constructor for Weather type class.
     * @param type The type of the weather (sunny/rainy).
     * @param startFrame The frame when the weather condition starts.
     * @param endFrame The frame when the weather condition ends.
     */
    public Weather(String type, int startFrame, int endFrame) {
        this.TYPE = type;
        this.START_FRAME = startFrame;
        this.END_FRAME = endFrame;
    }

    /**
     * Gets the type of the weather (sunny/rainy).
     * @return The type of the weather (sunny/rainy).
     */
    public String getType() {
        return TYPE;
    }

    /**
     * Gets the frame at which the weather condition starts.
     * @return The frame at which the weather condition starts.
     */
    public int getStartFrame() {
        return START_FRAME;
    }

    /**
     * Gets the frame at which the weather condition ends.
     * @return The frame at which the weather condition ends.
     */
    public int getEndFrame() {
        return END_FRAME;
    }
}
