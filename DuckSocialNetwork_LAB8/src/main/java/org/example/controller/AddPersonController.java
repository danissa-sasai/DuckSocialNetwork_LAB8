package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.domain.Person;
import org.example.domain.User;
import org.example.service.SocialNetworkService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class AddPersonController {
    SocialNetworkService service;
    private Stage dialogStage;

    @FXML
    private TextField textFieldUsername;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldPassword;
    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;
    @FXML
    private TextField textFieldOccupation;

    @FXML
    private DatePicker birthDatePicker;

    @FXML
    private Slider sliderEmpathyLevel;

    @FXML
    private Label labelEmpathyLevel;



    public void init(SocialNetworkService service, Stage stage) {
        this.service = service;
        this.dialogStage = stage;

        this.sliderEmpathyLevel.setMin(1);
        this.sliderEmpathyLevel.setMax(10);
        this.sliderEmpathyLevel.setValue(1);
        this.sliderEmpathyLevel.setSnapToTicks(true);

        labelEmpathyLevel.setText(String.valueOf((int) sliderEmpathyLevel.getValue()));

        sliderEmpathyLevel.valueProperty().addListener((observable, oldValue, newValue) -> {
            labelEmpathyLevel.setText(String.valueOf(newValue.intValue()));
            });

    }

    @FXML
    public void onSave(){
        String username = textFieldUsername.getText();
        String email = textFieldEmail.getText();
        String password = textFieldPassword.getText();
        String firstName = textFieldFirstName.getText();
        String lastName = textFieldLastName.getText();

        String occupation = textFieldOccupation.getText();

        LocalDate birthDate = birthDatePicker.getValue();
        System.out.println(birthDate);

        Integer empathyLevel = (int)sliderEmpathyLevel.getValue();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Missing Fields");
            alert.setContentText("Username Email and Password cannot be empty");
            alert.showAndWait();
            return;
        }

        if(lastName.isEmpty() || firstName.isEmpty()){
            MessageAlert.showErrorMessage(this.dialogStage,
                    "First and Last Name cannot be empty");
            return;
        }

        if(occupation.isEmpty()){
            MessageAlert.showErrorMessage(this.dialogStage,
                    "Occupation cannot be empty");
            return;
        }

        if(birthDate == null){
            MessageAlert.showErrorMessage(this.dialogStage,
                    "Your Birth Date cannot be empty");
            return;
        }

        if(sliderEmpathyLevel.getValue()<=0){
            MessageAlert.showErrorMessage(this.dialogStage,
                    "Your Empathy Level cannot be less than 0");
            return;
        }

        Person person = new Person(username,email,password,firstName,lastName,birthDate,occupation,empathyLevel);

        try{
            service.addUser(person);
            this.dialogStage.close();
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
