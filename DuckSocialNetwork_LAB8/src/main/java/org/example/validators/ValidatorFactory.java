package org.example.validators;

public class ValidatorFactory implements Factory{
    private static ValidatorFactory INSTANCE=null;

    public ValidatorFactory() {
    }
    public static ValidatorFactory getInstance() {
        if (INSTANCE==null) {
            INSTANCE=new ValidatorFactory();

        }
        return INSTANCE;
    }

    @Override
    public Validator createValidator(ValidationStrategy strategy) {
        switch (strategy) {
            case USER:
                return new UserValidator();
            case FRIENDSHIP:
                return new FriendshipValidator();

        }

        return null;
    }
}

