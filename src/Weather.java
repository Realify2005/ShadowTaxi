public class Weather {
    private final String TYPE;
    private final int START_FRAME;
    private final int END_FRAME;

    public Weather(String type, int startFrame, int endFrame) {
        this.TYPE = type;
        this.START_FRAME = startFrame;
        this.END_FRAME = endFrame;
    }

    public String getType() {
        return TYPE;
    }

    public int getStartFrame() {
        return START_FRAME;
    }

    public int getEndFrame() {
        return END_FRAME;
    }
}
