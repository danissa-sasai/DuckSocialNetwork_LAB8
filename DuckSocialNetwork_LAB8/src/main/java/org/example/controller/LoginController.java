package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.HelloApplication;
import org.example.domain.User;
import org.example.service.SocialNetworkService;
import org.example.utils.Pair;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    SocialNetworkService service;

    @FXML
    private Button buttonLogin;
    @FXML
    public Label labelSignUp;

    @FXML
    private TextField textFieldEmail;
    @FXML
    private PasswordField passwordFieldPassword;

    public void setService(SocialNetworkService service) {
        this.service = service;
    }

    @FXML
    public void initialize() {
        textFieldEmail.requestFocus(); //cursorul e deja in field-ul de mail
        labelSignUp.setOnMouseClicked(event -> openSignupWindow());
    }


    @FXML
    public void handleButtonLogin() {
        String email = textFieldEmail.getText();
        String password = passwordFieldPassword.getText();

        if(email.isEmpty() || password.isEmpty()) {
            MessageAlert.showErrorMessage(null, "Please fill all the fields");
            return;
        }

        try {
            Pair<User, Boolean> data = service.getUserByEmailPassword(email, password);

            if (data.getFirst() == null) {
                MessageAlert.showErrorMessage(null, "User not found");
                return;
            }

            if (!data.getSecond()) {
                MessageAlert.showErrorMessage(null, "Incorrect password");
                return;
            }

            User user = data.getFirst();

            // LOGIN ADMIN
            if ("admin".equals(user.getUsername())) {

                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/ducks_view.fxml"));
                Scene scene = new Scene(loader.load());

                AdminController controller = loader.getController();
                controller.setService(service);

                Stage stage = new Stage();
                stage.setTitle("Admin Panel");
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();

                //closing login window
                Stage oldStage = (Stage) buttonLogin.getScene().getWindow();
                oldStage.close();
            }
            else{
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/user_view.fxml"));
                Scene scene = new Scene(loader.load());

                UserController controller = loader.getController();
                controller.init(service,user);

                Stage stage = new Stage();
                stage.setTitle("User Panel");
                stage.setScene(scene);
                stage.sizeToScene();
                stage.show();

                //closing login window
                Stage oldStage = (Stage) buttonLogin.getScene().getWindow();
                oldStage.close();
            }

        } catch (SQLException e) {
            MessageAlert.showErrorMessage(null, "Database error");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void openSignupWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/sign_up_view.fxml"));
            Scene scene = new Scene(loader.load());

            SignUpController controller = loader.getController();
            controller.setService(service);

            Stage stage = new Stage();
            stage.setTitle("Choose Account Type");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
