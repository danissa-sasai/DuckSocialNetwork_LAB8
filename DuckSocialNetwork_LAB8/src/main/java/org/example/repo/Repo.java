package org.example.repo;

import java.security.PublicKey;
import java.util.List;

public interface Repo<T> {
    /**
     * Adds an entity in the repository
     * @param entity - an entity that has the type T
     */
    public void add(T entity);

    /**
     * Deletes an entity in the repository
     * @param id - the id of the entity that is deleted
     */
    public void delete(Long id);

    /**
     * The list of all the entities that are currently in the repository
     * @return - a List of entities that have the type T
     */
    public List<T> getAll();

    /**
     * Finds an entity
     * @param id - the id of the entity
     * @return - the entity with the corresponding id from the repo
     */
    public T getById(Long id);

}
