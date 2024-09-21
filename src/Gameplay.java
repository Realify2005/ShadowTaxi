import bagel.Font;
import bagel.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Class that handles the gameplay logic
 */
public class Gameplay {
    // Constants related to properties.
    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    // Variables
    private Taxi taxi;
    private Trip trip;
    private TripEndFlag tripEndFlag;
    private final CoinState COIN_STATE;
    private final GameStats GAME_STATS;

    // Full list of passengers.
    private ArrayList<Passenger> passengers;

    // Constants for rendering trip info
    private final String FONT_PATH;
    private final int FONT_SIZE;
    private final double TRIP_INFO_X;
    private final double TRIP_INFO_Y;

    // Constants for message texts from message_en.properties
    private final String ONGOING_TRIP_TEXT;
    private final String COMPLETED_TRIP_TEXT;
    private final String EXPECTED_EARNINGS_TEXT;
    private final String PRIORITY_TEXT;
    private final String PENALTY_TEXT;

    public Gameplay(TripEndFlag tripEndFlag, CoinState coinState,
                    GameStats gameStats, Properties gameProps, Properties messageProps) {
        this.trip = null;
        this.tripEndFlag = tripEndFlag;
        this.COIN_STATE = coinState;
        this.GAME_STATS = gameStats;

        this.GAME_PROPS = gameProps;
        this.MESSAGE_PROPS = messageProps;

        // Initialize constants for rendering trip info
        this.FONT_PATH = GAME_PROPS.getProperty("font");
        this.FONT_SIZE = Integer.parseInt(GAME_PROPS.getProperty("gameplay.info.fontSize"));
        this.TRIP_INFO_X = Double.parseDouble(GAME_PROPS.getProperty("gameplay.tripInfo.x"));
        this.TRIP_INFO_Y = Double.parseDouble(GAME_PROPS.getProperty("gameplay.tripInfo.y"));

        // Initialize constants for messages
        this.ONGOING_TRIP_TEXT = MESSAGE_PROPS.getProperty("gamePlay.onGoingTrip.title");
        this.COMPLETED_TRIP_TEXT = MESSAGE_PROPS.getProperty("gamePlay.completedTrip.title");
        this.EXPECTED_EARNINGS_TEXT = MESSAGE_PROPS.getProperty("gamePlay.trip.expectedEarning");
        this.PRIORITY_TEXT = MESSAGE_PROPS.getProperty("gamePlay.trip.priority");
        this.PENALTY_TEXT = MESSAGE_PROPS.getProperty("gamePlay.trip.penalty");
    }

    /**
     * Checks if taxi is adjacent to either passenger or flag
     * If so, either pick up the passenger towards the taxi or drop the passenger towards the trip end flag
     */
    public void checkIfTaxiIsAdjacentToPassengerOrFlag() {
        for (Passenger passenger : passengers) {
            // Check if taxi is adjacent to a passenger that has not been picked up before.
            if (taxi.isEmpty() && taxi.isAdjacentToPassenger(passenger) && !passenger.isPickedUp()) {
                taxi.pickUpPassenger(passenger);
            }

            if (!taxi.isEmpty()) {
                // Check if taxi is adjacent to flag. (Logic is in taxi.dropOffPassenger function).
                taxi.dropOffPassenger(tripEndFlag);
                trip.checkPenalty();
            }
        }
    }

    /**
     * Constantly updates the gameplay class.
     * This function performs all the needed checks at any given time to make the gameplay smooth.
     */
    public void update(Input input) {
        renderTripInfo();

        if (tripEndFlag != null) {
            tripEndFlag.update(input);
        }

        for (Passenger passenger : passengers) {
            // If passenger left the taxi and is moving to flag,
            // make sure that the passenger arrives to its final destination (i.e. flag).
            if (passenger.isMovingToFlag()) {
                passenger.dropOff(tripEndFlag);
            }
        }

        // Set trip as completed as soon as passenger leaves the taxi.
        if (trip != null && taxi.isPassengerMovingToFlag()) {
            trip.setTripAsCompleted();
        }
    }

    /**
     * Renders the bottom left "Current Trip" or "Last Trip" information text on screen during gameplay.
     */
    private void renderTripInfo() {
        Font font = new Font(FONT_PATH, FONT_SIZE);

        if (trip != null && trip.isOngoing()) {
            // Render "Current Trip" information.
            font.drawString(ONGOING_TRIP_TEXT, TRIP_INFO_X, TRIP_INFO_Y);

            double expectedEarnings = trip.getPassengerEarnings();
            String expectedEarningsText = String.format(EXPECTED_EARNINGS_TEXT + "%.1f", expectedEarnings);
            font.drawString(expectedEarningsText, TRIP_INFO_X, TRIP_INFO_Y + 30);

            int priority = trip.getPassengerPriority();
            String priorityText = String.format(PRIORITY_TEXT + "%d", priority);
            font.drawString(priorityText, TRIP_INFO_X, TRIP_INFO_Y + 60);
        } else if (trip != null && trip.isCompleted()) {
            // Render "Last Trip" information.
            font.drawString(COMPLETED_TRIP_TEXT, TRIP_INFO_X, TRIP_INFO_Y);

            double totalEarnings = trip.getPassengerEarnings();
            double penalty = trip.getPenalty();
            double earningsWithPenalty = totalEarnings + penalty;
            String totalEarningsText = String.format(EXPECTED_EARNINGS_TEXT + "%.1f", earningsWithPenalty);
            font.drawString(totalEarningsText, TRIP_INFO_X, TRIP_INFO_Y + 30);

            int priority = trip.getPassengerPriority();
            String priorityText = String.format(PRIORITY_TEXT + "%d", priority);
            font.drawString(priorityText, TRIP_INFO_X, TRIP_INFO_Y + 60);

            String penaltyText = String.format(PENALTY_TEXT + "%.2f", penalty);
            font.drawString(penaltyText, TRIP_INFO_X, TRIP_INFO_Y + 90);
        }
    }

    /**
     * Initialises the taxi object.
     */
    public void initialiseTaxi(Taxi taxi) {
        this.taxi = taxi;
    }

    /**
     * Initialises the list of passengers.
     */
    public void initialisePassengers(ArrayList<Passenger> passengers) {
        this.passengers = passengers;
    }

    /**
     * Logic to start trip if a passenger has successfully entered the taxi
     */
    public void startTrip(Passenger passenger) {
        tripEndFlag = new TripEndFlag(passenger.getEndX(), passenger.getY(),
                passenger.getDistanceY(), GAME_PROPS);
        trip = new Trip(taxi, passenger, tripEndFlag, COIN_STATE, GAME_STATS, GAME_PROPS);
        trip.beginTrip();
    }
}
