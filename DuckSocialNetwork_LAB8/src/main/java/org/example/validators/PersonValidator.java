package org.example.validators;

import org.example.domain.Person;
import org.example.exceptions.UserException;

public class PersonValidator implements Validator<Person> {

    /**
     * Person validator
     * @param p - the person that is getting validated
     * @throws UserException - if
     * first name = null || = ''
     * last name = null || = ''
     * occupation = null || = ''
     * empathy = null || empathy is higher than 10 || empathy is less then 1
     *
     */
    @Override
    public void validate(Person p) throws UserException {
        if (p.getFirstName() == null || p.getFirstName().isEmpty()) {
            throw new UserException("First name is empty");
        }

        if (p.getLastName() == null || p.getLastName().isEmpty()) {
            throw new UserException("Last name is empty");
        }

        if(p.getOccupation() == null || p.getOccupation().isEmpty()) {
            throw new UserException("Occupation is empty");
        }

        if(p.getEmpathyLevel()==null){
            throw new UserException("Empathy level is empty");
        }

        if(p.getEmpathyLevel()<1 || p.getEmpathyLevel()>10){
            throw new UserException("Empathy level must be between 1 and 10");
        }
    }
}
