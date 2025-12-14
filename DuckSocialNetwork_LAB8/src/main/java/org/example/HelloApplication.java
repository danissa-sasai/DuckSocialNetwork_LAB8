package org.example;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

import org.example.config.Config;
import org.example.controller.AdminController;
import org.example.controller.LoginController;
import org.example.domain.Friendship;
import org.example.domain.duck.Duck;
import org.example.domain.event.Event;
import org.example.domain.flock.Flock;
import org.example.domain.message.Message;
import org.example.repo.DB.*;
import org.example.repo.PagingRepo;
import org.example.repo.Repo;
import org.example.service.SocialNetworkService;
import org.example.validators.ValidationStrategy;
import org.example.validators.Validator;
import org.example.validators.ValidatorFactory;


public class HelloApplication extends Application {
    DBUserRepo userRepo;
    PagingRepo<Friendship> repoFriendships;
    Repo<Flock<? extends Duck>> repoFlocks;
    Repo<Event> repoEvents;
    Repo<Message> repoMessage;

    SocialNetworkService service;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException, SQLException {
          String url = Config.getProperties().getProperty("db.url");
          String username = Config.getProperties().getProperty("db.username");
          String password = Config.getProperties().getProperty("db.password");

        this.userRepo = new DBUserRepo(url, username, password);
        this.repoFriendships = new DBFriendshipRepo(url, username, password);
        this.repoFlocks = new DBFlockRepo(url, username, password);
        this.repoEvents = new DBEventRepo(url, username, password);
        this.repoMessage =  new DBMessageRepo(url, username, password);

        ValidatorFactory factory = ValidatorFactory.getInstance();
        Validator userValidator = factory.createValidator(ValidationStrategy.USER);
        Validator friendValidator = factory.createValidator(ValidationStrategy.FRIENDSHIP);

        this.service = new SocialNetworkService(
                userRepo,
                repoFriendships,
                repoFlocks,
                repoEvents,
                repoMessage,
                userValidator,
                friendValidator
        );
        initView(primaryStage); //<-stiu de ce crapa (nu mai crapa yey)

        primaryStage.setWidth(615);
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {
        //FXMLLoader fxmlLoader = new FXMLLoader();
        //fxmlLoader.setLocation(getClass().getResource("com/example/guiex1/views/UtilizatorView.fxml"));

//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/ducks_view.fxml"));
//
//        AnchorPane userLayout = fxmlLoader.load();
//        primaryStage.setScene(new Scene(userLayout));
//
//        AdminController userController = fxmlLoader.getController();
//        userController.setService(service);

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/login_view.fxml"));
        BorderPane loginLayout = fxmlLoader.load();


        primaryStage.sizeToScene();
        primaryStage.setScene(new Scene(loginLayout));

        LoginController loginController = fxmlLoader.getController();
        loginController.setService(service);
    }

}

