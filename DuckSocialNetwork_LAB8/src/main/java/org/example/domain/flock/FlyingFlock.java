package org.example.domain.flock;

import org.example.domain.duck.Duck;
import org.example.domain.duck.DuckType;
import org.example.domain.duck.FlyingDuck;

import java.util.List;

public class FlyingFlock extends Flock<FlyingDuck> {

    /**
     * FlyingFlock Constructor
     * @param name - the flock's name
     * @param type - the type of ducks the flock has(swimming, flying)
     * @param members - the ducks that belong to the flock
     */
    public FlyingFlock(String name, DuckType type, List<FlyingDuck> members) {
        super(name, type, members);
    }
}