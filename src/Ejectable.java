/**
 * Ejectable interface represent entities that can be ejected upon taxi being broken.
 * For Project 2B, this only applies to driver and passenger, the only 2 entities that has entering taxi functionality.
 */
public interface Ejectable {
    /**
     * Ejects the entity from the taxi upon taxi being broken.
     */
    void eject();
}
