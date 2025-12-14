package org.example.domain.duck;


import org.example.domain.User;
import org.example.domain.flock.Flock;

public abstract class Duck extends User {
    protected DuckType type;
    protected Double speed;
    protected Double stamina;

    Flock<? extends Duck> flock;

    /**
     * Duck constructor
     * @param username - a user's username that is visible in the app
     * @param email - a user's email that they use to log in the app
     * @param password - the password that they use to log in the app
     * @param type - a duck user's type (swimming, flying, swimming_and_flying)
     * @param speed - a duck user's speed
     * @param stamina - a duck user's stamina
     */
    public Duck(String username, String email, String password,
                DuckType type, Double speed, Double stamina) {
        super(username, email, password);

        this.type = type;
        this.speed = speed;
        this.stamina = stamina;
    }

    //------------------------------------------Getters and Setters-------------------------------------------------------


    public DuckType getType() { return type; }

    public void setType(DuckType type) { this.type = type; }

    public Double getSpeed() { return speed; }

    public void setSpeed(Double speed) { this.speed = speed; }

    public Double getStamina() { return stamina; }

    public void setStamina(Double stamina) { this.stamina = stamina; }

    @Override
    public String toString() {
        return
                super.toString() +
                "type=" + type +
                ", speed=" + speed +
                ", stamina=" + stamina
                ;
    }

    public void setFlock(Flock<? extends Duck> flock) {
        this.flock = flock;
    }

    public Flock<? extends Duck> getFlock() {
        return flock;
    }
}