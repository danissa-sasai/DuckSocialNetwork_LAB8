package org.example.validators;
import org.example.exceptions.UserException;

public interface Validator<T> {
    void validate(T entity) throws UserException;
}