package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.HelloApplication;
import org.example.domain.Friendship;
import org.example.domain.Person;
import org.example.domain.User;
import org.example.domain.duck.Duck;
import org.example.domain.duck.DuckType;
import org.example.dto.DuckFilterDTO;
import org.example.dto.PersonFilterDTO;
import org.example.service.SocialNetworkService;
import org.example.utils.paging.Page;
import org.example.utils.paging.Pageable;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AdminController {
    private SocialNetworkService service;
    ObservableList<User> ducksModel = FXCollections.observableArrayList();
    ObservableList<User> personsModel = FXCollections.observableArrayList();
    ObservableList<Friendship> friendshipsModel = FXCollections.observableArrayList();
    //ObservableList <- lista ce notifica automat GUI -ul atunci cand se schimba continutul ei
    //FXCollections.observableArrayList = metoda ce creeaza o implementare de ObservableList bazata pe un ArrayList,
    // dar cu notificari integrate.

    private final DuckFilterDTO duckFilter = new DuckFilterDTO();
    private final PersonFilterDTO personFilter = new PersonFilterDTO();

    private int pageSize = 3;

    private int currentPageDucks = 0;
    private int totalNumberOfDucks = 0;

    private int currentPagePersons = 0;
    private int totalNumberOfPersons = 0;

    private int currentPageFriendships = 0;
    private int totalNumberOfFriendships = 0;

    //--------------------------------------------------DUCKS-----------------------------------------------
    @FXML
    TableColumn<User, Long> tableColumnDuckId;

    @FXML
    TableView<User> tableViewDucks;

    @FXML
    TableColumn<User,String> tableColumnUsername;

    @FXML
    TableColumn<User, DuckType> tableColumnType;

    @FXML
    TableColumn<User,String> tableColumnSpeed;

    @FXML
    TableColumn<User,String> tableColumnStamina;

    @FXML
    ComboBox<DuckType> comboBoxType;

    @FXML
    Button buttonPrevious;

    @FXML
    Button buttonNext;

    @FXML
    Label labelPageNumber;

    //--------------------------------------------------PERSONS-----------------------------------------------
    @FXML
    Button buttonPreviousPersons;
    @FXML
    Button buttonNextPersons;
    @FXML
    Label labelPageNumberPersons;

    @FXML
    TableView<User> tableViewPersons;

    @FXML
    TableColumn<User, Long> tableColumnPersonId;
    @FXML
    TableColumn<User,String> tableColumnPersonUsername;
    @FXML
    TableColumn<User,String> tableColumnPersonEmail;
    @FXML
    TableColumn<User,String> tableColumnPersonPassword;
    @FXML
    TableColumn<User,String> tableColumnLastName;
    @FXML
    TableColumn<User,String> tableColumnFirstName;
    @FXML
    TableColumn<User, LocalDate> tableColumnBirthDate;
    @FXML
    TableColumn<User, String> tableColumnOccupation;
    @FXML
    TableColumn<User, Integer> tableColumnEmpathyLevel;

    //--------------------------------------------------FRIENDSHIPS-----------------------------------------------
    @FXML
    Button buttonPreviousFriendships;
    @FXML
    Button buttonNextFriendships;
    @FXML
    Label labelPageNumberFriendships;

    @FXML
    TableView<Friendship> tableViewFriendships;
    @FXML
    TableColumn<Friendship,Long> tableColumnFriendshipId;
    @FXML
    TableColumn<Friendship,Long> tableColumnFriendshipUser1;
    @FXML
    TableColumn<Friendship,Long> tableColumnFriendshipUser2;



    public void setService(SocialNetworkService service) {
        System.out.println("Setting service in DuckController");
        this.service = service;
        initModel();
    }

    private void initTypeComboBox() {
        var duckTypes = new ArrayList<DuckType>();
        duckTypes.add(null);
        for(var type : DuckType.values())
            duckTypes.add(type);

        comboBoxType.getItems().setAll(duckTypes);
    }

    @FXML //aici se fac toate chestile care au traba cu javafx
    public void initialize() {
        //--------------------------------------------DUCKS--------------------------------------------
        initTypeComboBox();
        tableColumnDuckId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        tableColumnType.setCellValueFactory(new PropertyValueFactory<>("type"));
        tableColumnSpeed.setCellValueFactory(new PropertyValueFactory<>("speed"));
        tableColumnStamina.setCellValueFactory(new PropertyValueFactory<>("stamina"));

        tableViewDucks.setItems(ducksModel);

        comboBoxType.getSelectionModel().selectedItemProperty().addListener(o ->{
            DuckType selectedType = comboBoxType.getSelectionModel().getSelectedItem();
            duckFilter.setType(selectedType);
            currentPageDucks = 0;
            initModel();
        });

        //-------------------------------------------PERSONS--------------------------------------------
        tableColumnPersonId.setCellValueFactory(new PropertyValueFactory<>("id")); //

        tableColumnPersonUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        tableColumnPersonEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableColumnPersonPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        tableColumnOccupation.setCellValueFactory(new PropertyValueFactory<>("occupation"));
        tableColumnEmpathyLevel.setCellValueFactory(new PropertyValueFactory<>("empathyLevel"));

        tableViewPersons.setItems(personsModel);

        //-------------------------------------------FRIENDSHIPS--------------------------------------------
        tableColumnFriendshipId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnFriendshipUser1.setCellValueFactory(new PropertyValueFactory<>("user1"));
        tableColumnFriendshipUser2.setCellValueFactory(new PropertyValueFactory<>("user2"));

        tableViewFriendships.setItems(friendshipsModel);
    }

    private void initModel() {
        //--------------------------------------------DUCKS--------------------------------------------
        Page<User> pageDucks = service.findAllDucksOnPage(new Pageable(currentPageDucks, pageSize),duckFilter);

        int maxPageDucks = (int) Math.ceil(1.0 * pageDucks.getTotalNumberOfElements() / pageSize) - 1;
        if(maxPageDucks < 0)
            maxPageDucks = 0;

        if(currentPageDucks > maxPageDucks){
            currentPageDucks = maxPageDucks;
            pageDucks =  service.findAllDucksOnPage(new Pageable(currentPageDucks, pageSize),duckFilter);
        }

        totalNumberOfDucks = pageDucks.getTotalNumberOfElements();
        buttonPrevious.setDisable(currentPageDucks == 0);
        buttonNext.setDisable((currentPageDucks + 1) * pageSize >= totalNumberOfDucks);

        var ducks = StreamSupport.stream(pageDucks.getElementsOnPage().spliterator(), false)
                .collect(Collectors.toList());
        ducksModel.setAll(ducks);

        labelPageNumber.setText("Page " + (currentPageDucks + 1) + " of " + (maxPageDucks + 1));

        //--------------------------------------------PERSONS--------------------------------------------
        Page<User> pagePersons = service.findAllPersonsOnPage(new Pageable(currentPagePersons, pageSize),personFilter);

        int maxPagePersons = (int) Math.ceil(1.0 * pagePersons.getTotalNumberOfElements() / pageSize) - 1;
        if(maxPagePersons < 0)
            maxPagePersons = 0;

        if(currentPagePersons > maxPagePersons){
            currentPagePersons = maxPagePersons;
            pagePersons =  service.findAllPersonsOnPage(new Pageable(currentPagePersons, pageSize),personFilter);
        }

        totalNumberOfPersons = pagePersons.getTotalNumberOfElements();
        buttonPreviousPersons.setDisable(currentPagePersons == 0);
        buttonNextPersons.setDisable((currentPagePersons + 1) * pageSize >= totalNumberOfPersons);

        var persons = StreamSupport.stream(pagePersons.getElementsOnPage().spliterator(), false)
                .collect(Collectors.toList());
        personsModel.setAll(persons);

        labelPageNumberPersons.setText("Page " + (currentPagePersons + 1) + " of " + (maxPagePersons + 1));

        //--------------------------------------------FRIENDSHIPS--------------------------------------------
        Page<Friendship> pageFriendships = service.findAllFriendshipsOnPage(new Pageable(currentPageFriendships, pageSize));

        int maxPageFriendships = (int) Math.ceil(1.0 * pageFriendships.getTotalNumberOfElements() / pageSize) - 1;
        if(maxPageFriendships < 0) maxPageFriendships = 0;

        if(currentPageFriendships > maxPageFriendships){
            currentPageFriendships = maxPageFriendships;
            pageFriendships = service.findAllFriendshipsOnPage(new Pageable(currentPageFriendships, pageSize));
        }

        totalNumberOfFriendships = pageFriendships.getTotalNumberOfElements();

        buttonPreviousFriendships.setDisable(currentPageFriendships == 0);
        buttonNextFriendships.setDisable((currentPageFriendships + 1) * pageSize >= totalNumberOfFriendships);

        var friendships = StreamSupport.stream(pageFriendships.getElementsOnPage().spliterator(), false)
                .collect(Collectors.toList());
        friendshipsModel.setAll(friendships);

        labelPageNumberFriendships.setText("Page " + (currentPageFriendships + 1) + " of " + (maxPageFriendships + 1));
    }

    public void onNextDucksPage(ActionEvent actionEvent) {
        currentPageDucks ++;
        initModel();
    }

    public void onPreviousDucksPage(ActionEvent actionEvent) {
        currentPageDucks --;
        initModel();
    }

    public void onPreviousPersonsPage(ActionEvent actionEvent) {
        currentPagePersons --;
        initModel();
    }

    public void onNextPersonsPage(ActionEvent actionEvent) {
        currentPagePersons ++;
        initModel();
    }

    @FXML
    public void onAddDuck(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/add_duck_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        var stage = new Stage();
        stage.setTitle("Add Duck");
        stage.setScene(scene);

        AddDuckController controller = fxmlLoader.getController();
        controller.init(this.service, stage);

        stage.setResizable(false);
        stage.show();
    }

    @FXML
    public void onDeleteDuck(ActionEvent actionEvent) {
        User selected = tableViewDucks.getSelectionModel().getSelectedItem();
        if (selected != null) {
            service.removeUser(selected);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Delete Duck");
            alert.setHeaderText("Success");
            alert.setContentText("You have successfully deleted this Duck");
            alert.showAndWait();
            initModel();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Delete Duck");
            alert.setHeaderText("Error");
            alert.setContentText("Please select a Duck first");
            alert.showAndWait();
        }
    }

    @FXML
    public void onAddPerson(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/add_person_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        var stage = new Stage();
        stage.setTitle("Add Person");
        stage.setScene(scene);

        AddPersonController controller = fxmlLoader.getController();
        controller.init(this.service, stage);

        stage.setResizable(false);
        stage.show();
    }

    @FXML
    public void onDeletePerson(ActionEvent actionEvent) {
        User selected = tableViewPersons.getSelectionModel().getSelectedItem();
        if (selected != null) {
            service.removeUser(selected);
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Success", "You have successfully deleted this Person");
            initModel();
        } else {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Please select a Person first");
        }
    }

    public void onPreviousFriendshipsPage(ActionEvent actionEvent) {
        currentPageFriendships --;
        initModel();
    }

    public void onNextFriendshipsPage(ActionEvent actionEvent) {
        currentPageFriendships ++;
        initModel();
    }

    @FXML
    public void onAddFriendship(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/add_friendship_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        var stage = new Stage();
        stage.setTitle("Add Friendship");
        stage.setScene(scene);

        AddFriendshipController controller = fxmlLoader.getController();
        controller.init(this.service, stage);

        stage.setResizable(false);
        //stage.setOnHidden(e -> initModel());
        stage.show();
    }

    @FXML
    public void onDeleteFriendship(ActionEvent actionEvent) {
        Friendship selected = tableViewFriendships.getSelectionModel().getSelectedItem();
        if (selected != null) {
            service.removeFriend(selected.getUser1(),selected.getUser2());
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Success", "You have successfully deleted this Friendship");
            initModel();
        } else {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Please select a Friendship first");
        }
    }

    @FXML
    public void showNumberOfCommunities(ActionEvent actionEvent){
        int numberOfCommunities = service.getNumberOfCommunities();
        MessageAlert.showMessage(null,Alert.AlertType.INFORMATION, "Number Of Communities",
                "The Current Number Of Communities in DuckSocialNetwork is " + numberOfCommunities);
    }

    @FXML
    public void showTheMostSociableCommunity(ActionEvent actionEvent){
        List<User> mostSociableCommunity = service.getMostSociableCommunity();

        String text = "The Most Sociable Community in DuckSocialNetwork:\n";
        for (User u : mostSociableCommunity) {
            text += u.getUsername() + "\n";
        }
        MessageAlert.showMessage(null,Alert.AlertType.INFORMATION, "Most Sociable Community", text);
    }
}
