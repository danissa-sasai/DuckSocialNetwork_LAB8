package org.example.domain;

import java.util.concurrent.atomic.AtomicLong;

public class Friendship {
    private Long id;
    //private final User user1;
    //private final User user2;
    private final Long user1;
    private final Long user2;
    /**
     * Friendship constructor
     * @param user1 - the first user that belongs to the friendship
     * @param user2 - the second user that belong to the friendship
     */
    public Friendship(Long user1, Long user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    //------------------------------------------Getters and Setters-------------------------------------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {this.id = id;}

    public Long getUser1() {
        return user1;
    }

    public Long getUser2() {
        return user2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Friendship)) return false;
        Friendship f = (Friendship) o;
        return (user1.equals(f.user1) && user2.equals(f.user2)) ||
                (user1.equals(f.user2) && user2.equals(f.user1));
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "id=" + id +
                ", user1=" + user1 +
                ", user2=" + user2 +
                '}';
    }
}

