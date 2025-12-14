package org.example.repo.inMemory;


import org.example.domain.User;
import org.example.repo.Repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepo<T extends User> implements Repo<T> {
    private Map<Long,T> users;

    /**
     * Constructor that initialises the users
     */
    public UserRepo() {
        this.users = new HashMap<>();
    }

    /**
     * Adds a user in the user repository
     * @param user - the user that is added in the user repository
     */
    @Override
    public void add(T user) {
        users.put(user.getId(), user);
    }

    /**
     * Deletes a user from the repo
     * @param id - the id of the entity that is deleted
     */
    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    /**
     * Gets all the users in the repository
     * @return - the list of all the users currently in the user repository
     */
    @Override
    public List<T> getAll() {
        return new ArrayList<>(users.values());
    }

    /**
     * Finds a user in the user repository
     * @param id - a user's id
     * @return - a user that has the id 'id'
     */
    @Override
    public T getById(Long id) {
        return users.get(id);
    }
}
