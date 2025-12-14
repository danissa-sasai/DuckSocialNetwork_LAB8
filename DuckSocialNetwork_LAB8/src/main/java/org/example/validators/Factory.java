package org.example.validators;

public interface Factory {
    Validator createValidator(ValidationStrategy strategy);
}

