package org.example.service;

import org.example.domain.Friendship;
import org.example.domain.User;
import org.example.domain.duck.Duck;
import org.example.domain.duck.DuckType;
import org.example.domain.duck.SwimmingDuck;
import org.example.domain.event.Event;
import org.example.domain.flock.Flock;

import java.util.AbstractMap;
import java.util.List;

public interface Service {
    /**
     * adds a user to the existing users
     * @param user - a User object
     */
    void addUser(User user);

    /**
     * Deletes a user from the current users
     * @param user - a User object
     */
    void removeUser(User user);

    /**
     * Adds a friendship to the existing users
     * @param id1 - the id of the first user
     * @param id2 - the id of the second user
     */
    void addFriend(Long id1, Long id2);

    /**
     * Deletes a friendship from the existing friendships
     * @param id1 - the id of the first user
     * @param id2 - the id of the second user
     */
    void removeFriend(Long id1, Long id2);

    /**
     * Gets all the friendships that are currently in the app
     * @return - a list of Friendship objects
     */
    List<Friendship> getAllFriendships();

    /**
     * Gets all the users that are currently in the app
     * @return - a list of User objects
     */
    List<User> getAllUsers();

    /**
     * Gets the number of communities that currently exist
     * @return - an integer
     */
    int getNumberOfCommunities();

    /**
     * Finds the community with the most members
     * @return - a list containing the users of the most active community
     */
    List<User> getMostSociableCommunity();

    /**
     * Finds a user using an id
     * @param id - the id of the user
     * @return - a user
     */
    User getUserById(Long id);

    /**
     * adds a new flock in the app
     * @param name - the new flock's name
     * @param duckType - the type of ducks the flock contains
     * @param members - the id's of the ducks that will belong to the flock
     */
    void addFlock(String name, DuckType duckType, List<Long> members);

    /**
     *
     * @return - the list of all the flocks currently in the service
     */
    List<Flock<? extends Duck>> getAllFlocks();

    /**
     * adds a new event RaceEvent
     * @param name - the race event's name
     * @param participants - the id's of the swimming ducks that will belong to the race event
     */
    void addRaceEvent(String name, List<Long> participants);

    /**
     *
     * @return - the list of all the events currently in the service
     */
    List<Event> getAllEvents();

    /**
     * selects m participants from a race event
     * @param id - the id's of the race event
     * @param m - the number of participants that will be seleted from the event
     * @return - m participants from the race event with id 'id'
     */
    List<SwimmingDuck> selectParticipants(Long id, int m);

    /**
     * the average performance of a flock
     * @param id - the flock's id
     * @return - the average performance of a flock containing the average speed and the average stamina
     */
    AbstractMap.SimpleEntry<Double,Double> getAveragePerformance(Long id);
}
