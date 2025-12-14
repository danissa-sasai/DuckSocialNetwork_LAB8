package org.example.repo.inMemory;


import org.example.domain.Friendship;
import org.example.repo.Repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendshipRepo<T extends Friendship> implements Repo<T> {
    private final Map<Long, T> friendships;

    /**
     * Constructor
     */
    public FriendshipRepo() {
        this.friendships = new HashMap<>();
    }

    /**
     * Adds a friendship in the friendship repository
     * @param friendship - a friendship between two users
     */
    @Override
    public void add(T friendship) {
        friendships.put(friendship.getId(), friendship);
    }

    /**
     * Deletes a friendship from the friendship repository
     * @param id - the id of a friendship
     */
    @Override
    public void delete(Long id) {
        friendships.remove(id);
    }

    /**
     * Gets all the friendships in the friendship repository
     * @return a list of Friendship objects
     */
    @Override
    public List<T> getAll() {
        return new ArrayList<>(friendships.values());
    }

    /**
     * Finds a friendship in the friendship repository
     * @param id - the id of the friendship
     * @return - a Friendship object
     */
    @Override
    public T getById(Long id) {
        return friendships.get(id);
    }
}
