package org.example.domain.flock;

import org.example.domain.duck.Duck;
import org.example.domain.duck.DuckType;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Flock<T extends Duck>{
    private Long id;
    private String name;
    private List<T> members; //hashmap!
    private DuckType type;

    /**
     * Flock Constructor
     * @param name - the flock's name
     * @param type - the type of ducks the flock has(swimming, flying)
     * @param members - the ducks that belong to the flock
     */
    public Flock(String name, DuckType type, List<T> members) {
        this.name = name;
        this.type = type;
        this.members = (members != null) ? members : new ArrayList<>();
        for(var m : members){
            m.setFlock(this);
        }
    }

    /**
     * adds a new duck to the existing ducks
     * @param duck - the duck that is added to the flock
     */
    public void addMember(T duck) {
        members.add(duck);
        duck.setFlock(this);
    }

    /**
     * removes an existing duck from the current list of ducks
     * @param duck - the duck that is getting removed
     */
    public void removeMember(T duck) {
        members.remove(duck);
        duck.setFlock(null);
    }

    /**
     * calculates the average speed and the average stamina of the flock's ducks
     * @return - the average speed and the average stamina
     */
    public AbstractMap.SimpleEntry<Double, Double> getAveragePerformance() {
        if (members.isEmpty()) {
            return new AbstractMap.SimpleEntry<>(0.0, 0.0);
        }

        double totalSpeed = 0.0;
        double totalStamina = 0.0;

        for (T duck : members) {
            totalSpeed += duck.getSpeed();
            totalStamina += duck.getStamina();
        }

        double avgSpeed = totalSpeed / members.size();
        double avgStamina = totalStamina / members.size();

        return new AbstractMap.SimpleEntry<>(avgSpeed, avgStamina);
    }

    //-------------------------------------------GETTERS AND SETTERS-----------------------------------------------------


    public List<T> getMembers() {
        return members;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMembers(List<T> members) {
        this.members = members;
        for(var m : members){
            m.setFlock(this);
        }

    }

    public DuckType getType() {
        return type;
    }

    public void setType(DuckType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Flock{id=").append(id)
                .append(", name='").append(name).append('\'')
                .append(", type=").append(type)
                .append(", members=[");

        for (T duck : members) {
            sb.append(duck.getUsername()) // sau duck.getId()
                    .append(", ");
        }

        if (!members.isEmpty()) {
            sb.setLength(sb.length() - 2); // elimina ultima virgula
        }

        sb.append("]}");
        return sb.toString();
    }
}
