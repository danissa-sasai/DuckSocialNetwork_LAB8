package org.example.domain.event;

import java.util.ArrayList;
import java.util.List;

import org.example.utils.observer.Observable;
import org.example.utils.observer.Observer;

/**
 * an event that can be organised by person users and can be observed by all types of users
 */
public abstract class Event implements Observable {
    private Long id;
    private String name;
    private List<Observer> subscribers;

    /**
     * Constructor for Event that implements Observable
     * @param name - the event's name
     */
    public Event(String name) {
        this.name = name;
        subscribers = new ArrayList<>();
    }

    /**
     * adds a new subscriber to the existing subscribers
     * @param observer - the subscriber that is added in order to observe the event
     */
    @Override
    public void subscribe(Observer observer) {
        subscribers.add(observer);
    }

    /**
     * removes a subscriber from the existing subscribers
     * @param observer - the subscriber that is removed and can no longer observe the event
     */
    @Override
    public void unsubscribe(Observer observer) {
        subscribers.remove(observer);
    }

    /**
     * notifies all the current subscribers of the event
     * @param message - the message that is transmitted to all the subscribers
     */
    @Override
    public void notifySubscribers(String message) {
        for (Observer observer : subscribers) {
            observer.update(message);
        }
    }

    //-------------------------------------------GETTERS AND SETTERS-----------------------------------------------------

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name +
                '}';
    }
}
