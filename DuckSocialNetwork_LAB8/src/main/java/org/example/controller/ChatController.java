package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.domain.User;
import org.example.domain.message.Message;
import org.example.exceptions.UserException;
import org.example.service.SocialNetworkService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class ChatController {
    @FXML
    public ListView<Message> listViewMessages;
    @FXML
    public Label labelSelectedMessage;
    @FXML
    public TextField textFieldMessage;
    @FXML
    public Button buttonReply;
    @FXML
    public Button buttonSend;


    private SocialNetworkService service;
    private User accountOwner;
    private User chatUser;
    private Message selectedMessage;

    public void init(SocialNetworkService service, User accountOwner, User chatUser) {
        this.service = service;
        this.accountOwner = accountOwner;
        this.chatUser = chatUser;
        loadMessages();
    }

    private void loadMessages() {
        listViewMessages.setItems(service.getMessagesBetweenUsers(accountOwner.getId(), chatUser.getId()));

        listViewMessages.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldMsg, newMsg) -> {
                selectedMessage = newMsg;
                if (newMsg != null)
                    labelSelectedMessage.setText("Replying to: " + newMsg.getContent());
        });


        listViewMessages.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Message m, boolean empty) {
                super.updateItem(m, empty);

                if (empty || m == null) {
                    setText(null);
                    return;
                }

                String who = m.getSender().equals(accountOwner.getId()) ?
                        "You" : chatUser.getUsername();

                setText(who + ": " + m.getContent());
            }

        });
    }

    @FXML
    private void onSend() {
        String text = textFieldMessage.getText();
        if (text.isEmpty())
            return;
        Message m = new Message(
                accountOwner.getId(),
                Set.of(chatUser.getId()),
                text,
                LocalDateTime.now(),
                null //not a reply
        );

        service.addMessage(m);
        textFieldMessage.clear();
        loadMessages();
    }

    @FXML
    private void onReply() {
        String text = textFieldMessage.getText();
        if (text.isEmpty())
            return;
        if (selectedMessage == null) {
            MessageAlert.showErrorMessage(null, "You need to select a message before replying");
            return;
        }
        Message m = new Message(
                accountOwner.getId(),
                Set.of(chatUser.getId()),
                text,
                LocalDateTime.now(),
                selectedMessage.getId()
        );
        try {
            service.addMessage(m);
        }
        catch (UserException e){
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
        textFieldMessage.clear();
        loadMessages();
    }
}
