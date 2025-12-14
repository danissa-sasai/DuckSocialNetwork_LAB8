package org.example.domain.flock;

import org.example.domain.duck.DuckType;
import org.example.domain.duck.SwimmingDuck;

import java.util.List;

public class SwimmingFlock extends Flock<SwimmingDuck>{

    /**
     * SwimmingFlock Constructor
     * @param name - the flock's name
     * @param type - the type of ducks the flock has(swimming, flying)
     * @param members - the ducks that belong to the flock
     */
    public SwimmingFlock(String name, DuckType type, List<SwimmingDuck> members) {
        super(name, type, members);
    }
}