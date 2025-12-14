package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.HelloApplication;
import org.example.domain.User;
import org.example.service.SocialNetworkService;

import java.io.IOException;

public class UserController {
    private User accountOwner;
    private SocialNetworkService service;
    ObservableList<User> usersModel = FXCollections.observableArrayList();

    @FXML
    private Label labelUsername;
    @FXML
    public TableView<User> tableViewUsers;
    @FXML
    public TableColumn<User,String> tableColumnUsername;
    @FXML
    public Button buttonChat;
    @FXML
    public Button buttonLogOut;

    public void init(SocialNetworkService service, User accountOwner) {
        this.service = service;
        this.accountOwner = accountOwner;
        tableColumnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));

        labelUsername.setText(accountOwner.getUsername());
        usersModel.setAll(service.getAllUsers()); //FARA ACCOUNT USER

        tableViewUsers.setItems(usersModel);

    }

    @FXML
    public void onOpenChat(ActionEvent actionEvent) throws IOException {
        User selectedUser = tableViewUsers.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/chat_view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            var stage = new Stage();
            stage.setTitle("Chat");
            stage.setScene(scene);

            ChatController controller = fxmlLoader.getController();
            controller.init(service,accountOwner,selectedUser);

            stage.sizeToScene();
            stage.show();
        }
        else{
            MessageAlert.showErrorMessage(null, "Select a user before chatting");
            return;
        }

    }

    public void onLogOut(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/login_view.fxml"));
        Scene scene = new Scene(loader.load());

        LoginController controller = loader.getController();
        controller.setService(service);

        Stage stage = new Stage();
        stage.setTitle("Login Panel");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();

        //closing login window
        Stage oldStage = (Stage) buttonChat.getScene().getWindow();
        oldStage.close();
    }
}
