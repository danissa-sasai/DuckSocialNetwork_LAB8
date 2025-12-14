package org.example.validators;


import org.example.domain.duck.Duck;
import org.example.exceptions.UserException;

public class DuckValidator implements Validator<Duck> {

    /**
     * Duck Validator
     * @param d - the Duck object that is getting validated
     * @throws UserException - throws exception if speed is less than 0 || stamina is higher than 0 || type is null
     */
    @Override
    public void validate(Duck d) throws UserException {
        if(d.getSpeed()<0)
            throw new UserException("Speed must be greater than 0");

        if(d.getStamina()<0)
            throw new UserException("Stamina must greater than 0");

        if(d.getType()==null){
            throw new UserException("Type must not be null");
        }
    }
}
