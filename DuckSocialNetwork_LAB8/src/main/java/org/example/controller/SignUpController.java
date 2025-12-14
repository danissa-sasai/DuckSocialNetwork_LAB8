package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.HelloApplication;
import org.example.service.SocialNetworkService;

public class SignUpController {

    private SocialNetworkService service;

    @FXML
    private javafx.scene.control.Button buttonUser;

    @FXML
    private javafx.scene.control.Button buttonDuck;

    public void setService(SocialNetworkService service) {
        this.service = service;
    }

    @FXML
    public void initialize() {
        buttonUser.setOnAction(e -> openUserSignup());
        buttonDuck.setOnAction(e -> openDuckSignup());
    }

    private void openUserSignup() {
        openWindow("/add_person_view.fxml", "Create User Account");
    }

    private void openDuckSignup() {
        openWindow("/add_duck_view.fxml", "Create Duck Account");
    }

    private void openWindow(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(fxml));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setResizable(false);

            Object controller = loader.getController();
            try {
                controller.getClass()
                        .getMethod("init", SocialNetworkService.class, Stage.class)
                        .invoke(controller, service, stage);
            } catch (Exception ignored) {}

            stage.show();

            Stage oldStage = (Stage) buttonUser.getScene().getWindow();
            oldStage.close();

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
