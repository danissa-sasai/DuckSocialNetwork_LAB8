package org.example.repo.inMemory;

import org.example.domain.flock.Flock;
import org.example.exceptions.EventException;
import org.example.repo.Repo;

import java.util.*;

public class FlockRepo<T extends Flock> implements Repo<T> {
    private Map<Long, T> flocks;

    /**
     * FlockRepo constructor
     */
    public FlockRepo(){
        this.flocks = new HashMap<>();
    }

    /**
     * Adds an entity in the repository
     * @param flock - a flock that has the type T
     */
    @Override
    public void add(T flock) {
        flocks.put(flock.getId(), flock);
    }

    /**
     * Deletes an entity in the repository
     * @param id - the id of the flock that is deleted
     */
    @Override
    public void delete(Long id) {
        flocks.remove(id);
    }

    /**
     * The list of all the entities that are currently in the repository
     * @return - a List of flocks that have the type T
     */
    @Override
    public List<T> getAll() {
        return new ArrayList<>(flocks.values());
    }

    /**
     * Finds an entity
     * @param id - the id of the flock
     * @return - the flock with the corresponding id from the repo
     */
    @Override
    public T getById(Long id) {
        return flocks.get(id);
    }

    /**
     * calculates the average speed and the average stamina of the flock's ducks
     * @return - the average speed and the average stamina
     * @throws - EventException if the flock is not found in the repo
     */
    public AbstractMap.SimpleEntry<Double, Double> getAveragePerformance(Long id) {
        T flock = getById(id);
        if (flock == null) {
            throw new EventException("Flock not found!");
        }

        return flock.getAveragePerformance();
    }

}