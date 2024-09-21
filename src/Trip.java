import java.util.Properties;

/**
 * Class that handles the trip logic.
 */
public class Trip {
    // Constant that defines the penalty rate of when a taxi moves too far off the trip end flag Y coordinate;
    private final double PENALTY_RATE;

    // Constants (Entities)
    private final Taxi TAXI;
    private final Passenger PASSENGER;
    private final TripEndFlag TRIP_END_FLAG;
    private final CoinState COIN_STATE;
    private final GameStats GAME_STATS;

    // Variables
    private double earnings;
    private boolean isOngoing;
    private boolean isCompleted;
    private double penalty;

    public Trip(Taxi taxi, Passenger passenger, TripEndFlag tripEndFlag,
                CoinState coinState, GameStats gameStats, Properties properties) {
        this.TAXI = taxi;
        this.PASSENGER = passenger;
        this.TRIP_END_FLAG = tripEndFlag;
        this.isOngoing = false;
        this.isCompleted = false;
        this.earnings = getPassengerEarnings();
        this.penalty = 0;
        this.COIN_STATE = coinState;
        this.GAME_STATS = gameStats;

        PENALTY_RATE = Double.parseDouble(properties.getProperty("trip.penalty.perY"));
    }

    /**
     * Picks up the passenger, renders the trip end flag, and begins the trip.
     */
    public void beginTrip() {
        TRIP_END_FLAG.activate();
        this.isOngoing = true;

        // Decrease passenger priority for when passenger is picked up whilst a coin effect is already currently active
        if (PASSENGER.isPickedUp() && COIN_STATE.isCoinActivated()) {
            PASSENGER.decreasePriority();
        }
    }

    /**
     * Check if any penalty needs to be imposed to the trip earnings.
     */
    public void checkPenalty() {
        // Taxi has moved beyond trip end flag on screen
        if (TAXI.getY() < TRIP_END_FLAG.getY() && !PASSENGER.isPenaltyImposed()) {
            // Y distance between taxi and trip end flag
            double taxiFlagDistance = Math.sqrt(Math.pow(TAXI.getX() - TRIP_END_FLAG.getX(), 2) +
                    Math.pow(TAXI.getY() - TRIP_END_FLAG.getY(), 2));

            // Distance between taxi and flag is greater than flag's radius, so penalty needs to be imposed
            if (taxiFlagDistance > TRIP_END_FLAG.getRadius()) {
                this.penalty = PENALTY_RATE * Math.abs(TAXI.getY() - TRIP_END_FLAG.getY());

                // Need to get updated earnings here to account for coin effect
                earnings = getPassengerEarnings();
                earnings -= penalty;

                // Earnings cannot be negative
                if (earnings < 0) {
                    earnings = 0;
                }

                PASSENGER.setEarnings(earnings); // Update earnings if penalty have been imposed
                PASSENGER.penaltyWasImposed(); // Flag to stop doing it multiple times across multiple frames
            }
        }
    }

    /**
     * Set trip as completed.
     */
    public void setTripAsCompleted() {
        // At this stage, trip has been completed
        this.isOngoing = false;
        this.isCompleted = true;

        countTripEarnings();
    }

    /**
     * Count total earnings for trip and add them to the game statistics class to be rendered and tracked.
     */
    private void countTripEarnings() {
        if (!PASSENGER.isEarningsAdded() && PASSENGER.isMovingToFlag()) {
            // Ensure earnings is only added once to total score
            GAME_STATS.addTotalScore(PASSENGER.getEarnings());
            PASSENGER.addedEarnings();
        }
    }

    public boolean isOngoing() {
        return isOngoing;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public double getPassengerEarnings() {
        return PASSENGER.getEarnings();
    }

    public int getPassengerPriority() {
        return PASSENGER.getPriority();
    }

    public double getPenalty() {
        return penalty;
    }
}
