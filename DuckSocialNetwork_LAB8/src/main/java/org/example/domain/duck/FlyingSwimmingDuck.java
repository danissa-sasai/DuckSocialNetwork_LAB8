package org.example.domain.duck;

public class FlyingSwimmingDuck extends SwimmingDuck implements Flying{
    /**
     * Duck constructor
     *
     * @param username - a user's username that is visible in the app
     * @param email    - a user's email that they use to log in the app
     * @param password - the password that they use to log in the app
     * @param type     - a duck user's type (swimming, flying, swimming_and_flying)
     * @param speed    - a duck user's speed
     * @param stamina  - a duck user's stamina
     */
    public FlyingSwimmingDuck(String username, String email, String password, DuckType type,
                              Double speed, Double stamina) {
        super(username, email, password, type, speed, stamina);
    }

    @Override
    public void fly() {
        System.out.println("flying...");
    }

    @Override
    public String toString() {
        return "FlyingSwimmingDuck{" +
                super.toString() +
                "}";
    }
}
