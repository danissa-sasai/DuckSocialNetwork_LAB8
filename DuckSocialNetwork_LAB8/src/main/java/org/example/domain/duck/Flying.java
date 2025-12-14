package org.example.domain.duck;

/**
 * Represents an entity capable of flying
 * Classes implementing this interface should define how the entity flyes,
 * including possible style, speed, or environment.
 */
public interface Flying {
    /**
     * Performs the flying action.
     * The exact behavior depends on the implementing class.
     */
    void fly();
}

