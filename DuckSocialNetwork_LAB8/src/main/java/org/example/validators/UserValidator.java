package org.example.validators;


import org.example.domain.duck.Duck;
import org.example.domain.Person;
import org.example.domain.User;
import org.example.exceptions.UserException;

public class UserValidator implements Validator<User> {
    private final PersonValidator personValidator = new PersonValidator();
    private final DuckValidator duckValidator = new DuckValidator();

    @Override
    public void validate(User user) throws UserException {
        if (user instanceof Person)
            personValidator.validate((Person) user);
        else if (user instanceof Duck)
            duckValidator.validate((Duck) user);
        else
            throw new UserException("Unknown user type.");
    }
}