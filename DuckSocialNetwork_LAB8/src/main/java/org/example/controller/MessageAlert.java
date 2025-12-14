package org.example.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;

public class MessageAlert {
    public static void showMessage(Stage owner, Alert.AlertType type, String header, String text){
        Alert message=new Alert(type);
        message.setHeaderText(header);
        message.setContentText(text);
        message.initOwner(owner);
        // --- STILIZARE ---
        styleAlert(message);
        // -----------------
        message.showAndWait();
    }

    public static void showErrorMessage(Stage owner, String text){
        Alert message=new Alert(Alert.AlertType.ERROR);
        message.initOwner(owner);
        message.setTitle("Error");
        message.setContentText(text);

        // --- STILIZARE ---
        styleAlert(message);
        // -----------------

        message.showAndWait();
    }

    private static void styleAlert(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();

        // 1. Adaugam foaia de stil (asigura-te ca style.css e in resources)
        try {
            dialogPane.getStylesheets().add(
                    MessageAlert.class.getResource("/style/error_alert_style.css").toExternalForm()
            );
        } catch (Exception e) {
            System.out.println("CSS File could not be loaded: " + e.getMessage());
        }

        // 2. Adaugam clasa CSS definita de noi (.my-alert)
        dialogPane.getStyleClass().add("my-alert");
    }
}

