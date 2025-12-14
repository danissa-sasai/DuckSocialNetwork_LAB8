package org.example.domain.event;

import org.example.domain.duck.SwimmingDuck;
import org.example.exceptions.UserException;

import java.util.*;

public class RaceEvent extends Event {
    private Set<SwimmingDuck> participants;

    /**
     * Constructor for Event that implements Observable
     * @param name - the event's name
     * @param participants - the participating swimming duck that will participate in the event
     */
    public RaceEvent(String name, List<SwimmingDuck> participants) {
        super(name);
        this.participants = new HashSet<>(participants);
    }

    public Set<SwimmingDuck> getParticipants() { //!din start are M rate
        return participants;
    }

    public void setParticipants(Set<SwimmingDuck> participants) {
        this.participants = participants;
    }

    /**
     * selects m participants ordered by their stamina
     * @param m - the number of participants that will be selected from the current participants in the event
     * @return - m participants if m is less or equal to the length of the list holding the participants
     */
    public List<SwimmingDuck> selectParticipants(int m) {
        if (getParticipants().size() < m) {
            throw new UserException("Race event must have at least " + m + " participants");
        }
        List<SwimmingDuck> sorted = new ArrayList<>(participants);

        sorted.sort(Comparator.comparingDouble(SwimmingDuck::getStamina));

        return new ArrayList<>(sorted.subList(0, m));
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RaceEvent{id=").append(getId())
                .append(", name='").append(getName()).append("', participants=[");

        if (participants != null && !participants.isEmpty()) {
            for (SwimmingDuck duck : participants) {
                sb.append(duck.getUsername()).append(", ");
            }
            // eliminăm ultima virgulă și spațiu
            sb.setLength(sb.length() - 2);
        } else {
            sb.append("no participants");
        }

        sb.append("]}");
        return sb.toString();
    }

}