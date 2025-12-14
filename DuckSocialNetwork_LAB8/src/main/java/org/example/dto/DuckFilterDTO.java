package org.example.dto;

import org.example.domain.duck.DuckType;

import java.util.Optional;

public class DuckFilterDTO {
    private String username;
    private DuckType type;
    private Double speed;
    private Double stamina;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public DuckType getType() {
        return type;
    }

    public void setType(DuckType type) {
        this.type = type;
    }

    public Double getStamina() {
        return stamina;
    }

    public void setStamina(Double stamina) {
        this.stamina = stamina;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

}
