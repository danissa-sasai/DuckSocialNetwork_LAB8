package org.example;

import org.example.UI.UI;
import org.example.config.Config;
import org.example.domain.Friendship;
import org.example.domain.User;
import org.example.domain.duck.Duck;
import org.example.domain.event.Event;
import org.example.domain.flock.Flock;
import org.example.repo.*;
import org.example.repo.DB.DBEventRepo;
import org.example.repo.DB.DBFlockRepo;
import org.example.repo.DB.DBFriendshipRepo;
import org.example.repo.DB.DBUserRepo;
import org.example.service.Service;
import org.example.service.SocialNetworkService;
import org.example.validators.ValidationStrategy;
import org.example.validators.Validator;
import org.example.validators.ValidatorFactory;


import java.sql.SQLException;


public class Main {
    public static void main(String[] args) throws SQLException {
//        String url = Config.getProperties().getProperty("db.url");
//        String username = Config.getProperties().getProperty("db.username");
//        String password = Config.getProperties().getProperty("db.password");

        HelloApplication.main(args);
    }

}

