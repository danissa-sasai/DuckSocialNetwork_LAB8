package org.example.utils.observer;

/**
 * Observer pattern
 */
public interface Observable {
    void subscribe(Observer observer); //add
    void unsubscribe(Observer observer); //remove
    void notifySubscribers(String message);
}
