package org.example.domain;

import java.time.LocalDate;
import java.util.List;

public class Person extends User {
    private String lastName; //nume
    private String firstName; //prenume
    private LocalDate birthDate;
    private String occupation;
    private Integer empathyLevel;

    /**
     * Person constructor
     * @param username - a person's username
     * @param email - a person's email address
     * @param password - a person's password for their account
     * @param firstName - a person's first name
     * @param lastName - a person's last name
     * @param birthDate - a person's birthday
     * @param occupation - a person's occupation
     * @param empathyLevel - the empathy that a person has towards others
     */
    public Person(String username, String email, String password,
                  String firstName, String lastName, LocalDate birthDate, String occupation, Integer empathyLevel) {
        super(username, email, password);

        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.occupation = occupation;
        this.empathyLevel = empathyLevel;
    }

    //------------------------------------------Getters and Setters-------------------------------------------------------

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName;}

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public LocalDate getBirthDate() { return birthDate; }

    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getOccupation() { return occupation; }

    public void setOccupation(String occupation) { this.occupation = occupation; }

    public Integer getEmpathyLevel() { return empathyLevel; }

    public void setEmpathyLevel(Integer empathyLevel) { this.empathyLevel = empathyLevel; }


    @Override
    public String toString() {
        return "Person{" +
                super.toString() +
                "lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", birthDate=" + birthDate +
                ", ocupation='" + occupation + '\'' +
                ", empathyLevel=" + empathyLevel +
                '}';
    }

}

