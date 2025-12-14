package org.example.validators;


import org.example.domain.Friendship;
import org.example.exceptions.FriendshipException;
import org.example.exceptions.UserException;

public class FriendshipValidator implements Validator<Friendship> {

    /**
     * Friendship Validator
     * @param friendship - the Friendship object that is getting validated
     * @throws FriendshipException - if u1=u2 || u1=null || u2=null
     */
    @Override
    public void validate(Friendship friendship) throws UserException {
        if (friendship.getUser1() == null || friendship.getUser2() == null)
            throw new FriendshipException("Users cannot be null in a friendship");

        if (friendship.getUser1().equals(friendship.getUser2()))
            throw new FriendshipException("A user cannot be friends with themselves");
    }
}
