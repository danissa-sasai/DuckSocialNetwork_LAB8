package org.example.domain.duck;

/**
 * Represents an entity capable of swimming.
 * Classes implementing this interface should define how the entity swims,
 * including possible style, speed, or environment.
 */
public interface Swimming {
    /**
     * Performs the swimming action.
     * The exact behavior depends on the implementing class.
     */
    void swim();
}
