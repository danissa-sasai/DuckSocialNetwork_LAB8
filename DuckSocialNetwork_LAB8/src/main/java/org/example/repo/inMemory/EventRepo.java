package org.example.repo.inMemory;

import org.example.domain.duck.SwimmingDuck;
import org.example.domain.event.Event;
import org.example.domain.event.RaceEvent;
import org.example.repo.Repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventRepo<T extends Event> implements Repo<T> {
    private Map<Long, T> events;

    /**
     * EventRepo constructor
     */
    public EventRepo() {
        this.events = new HashMap<>();
    }

    /**
     * Adds an entity in the repository
     * @param event - an event that has the type T
     */
    @Override
    public void add(T event) {
        events.put(event.getId(), event);
    }

    /**
     * Deletes an entity in the repository
     * @param id - the id of the event that is deleted
     */
    @Override
    public void delete(Long id) {
        events.remove(id);
    }

    /**
     * The list of all the entities that are currently in the repository
     * @return - a List of events that have the type T
     */
    @Override
    public List<T> getAll() {
        return new ArrayList<>(events.values());
    }

    /**
     * Finds an entity
     * @param id - the id of the event
     * @return - the event with the corresponding id from the repo
     */
    @Override
    public T getById(Long id) {
        return events.get(id);
    }

    /**
     * selects m participants ordered by their stamina
     * @param id - the id of the event that is going to be used to select the participants
     * @param m - the number of participants that will be selected
     * @return - m participants
     */
    public List<SwimmingDuck> selectParticipants(Long id, int m){
        RaceEvent event = (RaceEvent) getById(id);
        return event.selectParticipants(m);
    }
}
