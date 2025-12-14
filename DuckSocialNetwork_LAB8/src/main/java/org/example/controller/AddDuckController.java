package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.domain.User;
import org.example.domain.duck.*;
import org.example.service.SocialNetworkService;

import java.util.ArrayList;

public class AddDuckController {
    SocialNetworkService service;

    @FXML
    private TextField textFieldUsername;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldPassword;
    @FXML
    private TextField textFieldSpeed;
    @FXML
    private TextField textFieldStamina;
    @FXML
    private ComboBox<DuckType> comboBoxType;

    private Stage dialogStage;

    public void init(SocialNetworkService service, Stage stage) {
        this.service = service;
        var duckTypes = new ArrayList<DuckType>();
        duckTypes.add(null);
        for(var type : DuckType.values())
            duckTypes.add(type);

        comboBoxType.getItems().setAll(duckTypes);

        this.dialogStage = stage;
    }

    @FXML
    public void onSave(){
        String username = textFieldUsername.getText();
        String email = textFieldEmail.getText();
        String password = textFieldPassword.getText();
        Double speed = null;
        Double stamina = null;

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Missing Fields");
            alert.setContentText("Username Email and Password cannot be empty");
            alert.showAndWait();
            return;
        }

        try
        {
            speed = Double.parseDouble(textFieldSpeed.getText());
            stamina = Double.parseDouble(textFieldStamina.getText());
        }
        catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("Please enter a valid number for speed and stamina");
            alert.showAndWait();
            return;
        }

        DuckType selectedType = comboBoxType.getSelectionModel().getSelectedItem();
        if (selectedType == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("Please select a type of duck");
            alert.showAndWait();
            return;
        }

        if(selectedType == DuckType.valueOf("FLYING")){
            DuckType type = comboBoxType.getSelectionModel().getSelectedItem();
            User duck = new FlyingDuck(username,email,password,type,speed,stamina);

            try {
                this.service.addUser(duck);
            }
            catch(Exception e){
                MessageAlert.showErrorMessage(this.dialogStage,
                        "Something went wrong:"+e.getMessage());
                return;
            }
            dialogStage.close();
        }
        else if(selectedType == DuckType.valueOf("SWIMMING")){
            DuckType type = comboBoxType.getSelectionModel().getSelectedItem();
            User duck = new SwimmingDuck(username,email,password,type,speed,stamina);

            try {
                this.service.addUser(duck);
            }
            catch(Exception e){
                MessageAlert.showErrorMessage(this.dialogStage,
                        "Something went wrong:"+e.getMessage());
                return;
            }
            dialogStage.close();
        }
        else if(selectedType == DuckType.valueOf("FLYING_AND_SWIMMING")){
            DuckType type = comboBoxType.getSelectionModel().getSelectedItem();
            User duck = new FlyingSwimmingDuck(username,email,password,type,speed,stamina);

            try {
                this.service.addUser(duck);
            }
            catch(Exception e){
                MessageAlert.showErrorMessage(this.dialogStage,
                        "Something went wrong:"+e.getMessage());
                return;
            }
            dialogStage.close();
        }

    }

    @FXML
    public void onCancel(){
        dialogStage.close();
    }
}
