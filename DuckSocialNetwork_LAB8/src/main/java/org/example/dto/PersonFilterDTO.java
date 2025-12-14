package org.example.dto;

import java.time.LocalDate;

public class PersonFilterDTO {
    private String username;
    private String email;
    private String password;

    private String lastName; //nume
    private String firstName; //prenume
    private LocalDate birthDate;
    private String occupation;
    private Integer empathyLevel;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public Integer getEmpathyLevel() {
        return empathyLevel;
    }

    public void setEmpathyLevel(Integer empathyLevel) {
        this.empathyLevel = empathyLevel;
    }
}
