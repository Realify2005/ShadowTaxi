public interface Damageable {
    int COLLISION_TIMEOUT_FRAMES_TOTAL = 200;
    int COLLISION_TIMEOUT_FRAMES_INITIAL = 10;

    void receiveDamage(int damage);
    void updateCollisionTimeoutFramesRemaining();
    void separateFromObject(double otherX, double otherY);
}
