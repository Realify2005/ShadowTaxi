public interface Damageable {
    int COLLISION_TIMEOUT_FRAMES_TOTAL = 200;
    int COLLISION_TIMEOUT_FRAMES_INITIAL = 10;

    void receiveDamage(double damage);
    boolean handleCollision(Car other);
    void updateCollisionTimeoutFramesRemaining();
    void separateFromObject(Damageable other);
    double getDistanceTo(double otherX, double otherY);
    int getX();
    int getY();
    double getDamage();
    double getRadius();
    double getCurrentHealth();
}
