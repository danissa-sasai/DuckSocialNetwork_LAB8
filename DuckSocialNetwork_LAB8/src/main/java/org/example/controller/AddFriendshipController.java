package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.domain.Friendship;
import org.example.domain.User;
import org.example.service.SocialNetworkService;

import java.util.ArrayList;

public class AddFriendshipController {
    SocialNetworkService service;

    @FXML
    private TextField textFieldUser1;
    @FXML
    private TextField textFieldUser2;

    private Stage dialogStage;

    public void init(SocialNetworkService service, Stage stage) {
        this.service = service;
        this.dialogStage = stage;
    }

    @FXML
    public void onSave(){
        Long user1 = null;
        Long user2 = null;

        try
        {
            user1 = Long.parseLong(textFieldUser1.getText());
            user2 = Long.parseLong(textFieldUser2.getText());
        }
        catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("Please enter a valid number for speed and stamina");
            alert.showAndWait();
            return;
        }

        try {
            this.service.addFriend(user1,user2);
            MessageAlert.showMessage(this.dialogStage, Alert.AlertType.INFORMATION,"Success","The friendship has been added");
            dialogStage.close();
        }
        catch(Exception e){
            MessageAlert.showErrorMessage(this.dialogStage,
                    "Something went wrong:"+e.getMessage());
            return;
        }

    }

    @FXML
    public void onCancel(){
        dialogStage.close();
    }
}
