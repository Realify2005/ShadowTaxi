/**
 * Damageable interface represent entities that has health points.
 * These entities can give and receive damage through collision with other entities.
 * This interface provides constants and methods for collision, damage, and health logic.
 */
public interface Damageable {

    /**
     * The total number of frames a damageable entity is "timed out" after a collision.
     * When an entity is "timed out", they are temporarily unable to be involved in collisions.
     */
    int COLLISION_TIMEOUT_FRAMES_TOTAL = 200;

    /**
     * The total number of frames an entity needs to be separated from the other entity post-collision.
     * For these amount of frames post-collision, the 2 entities involved in the collision will continue to move away
     * from each other.
     */
    int COLLISION_TIMEOUT_FRAMES_INITIAL = 10;

    /**
     * Updates the current health of damageable entity after some damage is inflicted on it.
     * @param damage The damage inflicted onto the damageable entity.
     */
    void receiveDamage(double damage);

    /**
     * Checks if a collision has occurred that involves this damageable object.
     * A collision has occurred if:
     * 1. The other car's health is greater than or equal to 0.
     * 2. This damageable object has no more collision timeout frames remaining.
     * 3. The distance between this object and other car is less than the combined collision radius of both objects.
     * @param other The other car that the damageable entity has potentially collided with
     * @return True if a legal collision has occurred, false otherwise.
     */
    boolean handleCollision(Car other);

    /**
     * Reduces the collision timeout frames remaining if it is still active.
     * This function is called once every frame.
     */
    void updateCollisionTimeoutFramesRemaining();

    /**
     * Separates the damageable object from another object (post-collision during initial timeout frames).
     * For 10 frames, moves the damageable object vertically upwards if it is above the other object, or vice versa.
     * @param other The other object that this damageable object has collided with.
     */
    void separateFromObject(Damageable other);

    /**
     * Calculates the Euclidean distance of this damageable entity and another object.
     * @param otherX The x-coordinate of the other object.
     * @param otherY The y-coordinate of the other object.
     * @return The Euclidean distance between this damageable entity and the other object.
     */
    double getDistanceTo(double otherX, double otherY);

    /**
     * Gets the current x-coordinate of the damageable entity.
     * @return Current x-coordinate of the damageable entity.
     */
    int getX();

    /**
     * Gets the current y-coordinate of the damageable entity.
     * @return Current y-coordinate of the damageable entity.
     */
    int getY();

    /**
     * Gets the amount of damage that the damageable entity inflicts towards other object upon collision.
     * @return The amount of damage the damageable entity inflicts towards other object.
     */
    double getDamage();

    /**
     * Gets the radius of the damageable entity.
     * @return Radius of the damageable entity.
     */
    double getRadius();

    /**
     * Gets the current health of the damageable entity.
     * @return The current health of the damageable entity.
     */
    double getCurrentHealth();
}
