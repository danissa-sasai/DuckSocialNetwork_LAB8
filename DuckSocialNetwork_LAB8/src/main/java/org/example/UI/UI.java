package org.example.UI;

import org.example.domain.duck.*;
import org.example.domain.event.Event;
import org.example.domain.flock.Flock;
import org.example.service.Service;
import org.example.domain.*;
import org.example.exceptions.*;

import java.sql.SQLOutput;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UI {
    Service service;

    public UI(Service service){
        this.service = service;
    }

    private void printMenu(){
        System.out.println("0. EXIT");
        System.out.println("1. Add User");
        System.out.println("2. Delete User");
        System.out.println("3. Add Friend");
        System.out.println("4. Delete Friend");
        System.out.println("5. View number of communities");
        System.out.println("6. View the most social community");
        System.out.println("7. View all the users");
        System.out.println("8. View all friendships");
        System.out.println("9. Add Flock");
        System.out.println("10. View all the flocks");
        System.out.println("11. Add a RaceEvent");
        System.out.println("12. View all events");
        System.out.println("13. Select m ducks from an event");
        System.out.println("14. View average performance of a Flock");
    }

    private void addUserUI() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Insert username:");
        String username = scanner.next();

        System.out.println("Insert email:");
        String email = scanner.next();

        System.out.println("Insert password:");
        String password = scanner.next();

        System.out.println("Insert the type of user (duck/person):");
        String type = scanner.next();

        try {
            if (type.equalsIgnoreCase("person")) {
                System.out.println("Insert first name:");
                String firstName = scanner.next();

                System.out.println("Insert last name:");
                String lastName = scanner.next();

                System.out.println("Insert birthday (yyyy-mm-dd):");
                String birthday = scanner.next();

                System.out.println("Insert occupation:");
                String occupation = scanner.next();

                System.out.println("Insert empathy level (1-10):");
                int level = scanner.nextInt();

                Person p = new Person(
                        username,
                        email,
                        password,
                        firstName,
                        lastName,
                        LocalDate.parse(birthday),
                        occupation,
                        level
                );

                service.addUser(p);
                System.out.println("Person added successfully!");
            }
            else if (type.equalsIgnoreCase("duck")) {
                System.out.println("Insert type of duck (FLYING / SWIMMING / FLYING_AND_SWIMMING):");
                String duckTypeInput = scanner.next();

                DuckType duckType;
                try {
                    duckType = DuckType.valueOf(duckTypeInput.toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid duck type!");
                    return;
                }

                System.out.println("Insert speed:");
                int speed = scanner.nextInt();

                System.out.println("Insert stamina:");
                int stamina = scanner.nextInt();

                if (duckType == DuckType.FLYING) {
                    Duck d = new FlyingDuck(
                            username,
                            email,
                            password,
                            duckType,
                            (double) speed,
                            (double) stamina
                    );
                    service.addUser(d);
                }
                else if (duckType == DuckType.SWIMMING) {
                    Duck d = new SwimmingDuck(
                            username,
                            email,
                            password,
                            duckType,
                            (double) speed,
                            (double) stamina
                    );
                    service.addUser(d);
                }
                else{
                    Duck d = new FlyingSwimmingDuck(
                            username,
                            email,
                            password,
                            duckType,
                            (double) speed,
                            (double) stamina
                    );
                    service.addUser(d);
                }
                System.out.println("Duck added successfully!");
            }
            else {
                System.out.println("Invalid user type!");
            }
        } catch (UserException e) {
            System.out.println("Validation error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }


    }

    private void deleteUserUI() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter user ID to delete:");

        Long id = Long.parseLong(scanner.nextLine());
        try {
            User user = service.getUserById(id);
            if (user == null) {
                System.out.println("User not found!");
                return;
            }
            service.removeUser(user);
            System.out.println("User deleted successfully.");
        } catch (Exception e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }

    private void addFriendshipUI(){
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter first user ID:");
        Long id1 = Long.parseLong(scanner.nextLine());

        System.out.println("Enter second user ID:");
        Long id2 = Long.parseLong(scanner.nextLine());

        try {
            service.addFriend(id1, id2);
            System.out.println("Friendship added successfully!");
        } catch (FriendshipException e) {
            System.out.println("Validation error: " + e.getMessage());
        }
    }

    private void removeFriendshipUI() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the users ID's to delete the friendship");

        System.out.println("Enter first user ID:");
        Long id1 = Long.parseLong(scanner.nextLine());

        System.out.println("Enter second user ID:");
        Long id2 = Long.parseLong(scanner.nextLine());

        try {
            service.removeFriend(id1, id2);
            System.out.println("Friendship deleted successfully!");
        } catch (FriendshipException e) {
            System.out.println("Validation error: " + e.getMessage());
        }
    }

    private void numberOfCommunitiesUI(){
        var result = service.getNumberOfCommunities();
        System.out.println("Number of communities: " + result);
    }

    private void mostSociableCommunityUI(){
        var result = service.getMostSociableCommunity();

        if (result.isEmpty()) {
            System.out.println("No communities found.");
            return;
        }

        result.forEach(System.out::println);
    }

    private void printUsersUI(){
        List<User> users = service.getAllUsers();
        users.forEach(System.out::println);
    }

    private void printFriendshipsUI(){
        List<Friendship> friendships = service.getAllFriendships();
        friendships.forEach(System.out::println);
    }
    ///9.
    private void addFlockUI(){ /// DE PUS SI FLYING_AND_SWIMMING_DUCK!
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the flock ducks type(SWIMMING / FLYING):");

        String duckTypeInput = scanner.next();
        try {
            DuckType duckType = DuckType.valueOf(duckTypeInput.toUpperCase());
            if (duckType!=DuckType.FLYING && duckType!=DuckType.SWIMMING) {
                System.out.println("Invalid duck type!");
                return;
            }
            System.out.println("Enter the flock's name:");
            String name = scanner.next();

            List<Long> membersIds = new ArrayList<>();
            System.out.println("Enter the duck's id's(they must belong specified type):");
            System.out.println("To stop insert 0!");

            Long id = scanner.nextLong();
            while(id!=0){
                membersIds.add(id);
                id = scanner.nextLong();
            }

            if(membersIds.isEmpty()){
                System.out.println("No members were inserted");
                return;
            }

            try {
                service.addFlock(name, duckType, membersIds);
            }
            catch (UserException e) {
                System.out.println(e.getMessage());
            }

        }catch (IllegalArgumentException e) {
            System.out.println("Invalid duck type!");
        }

    }

    private void printFlocksUI()
    {
        List<Flock<? extends Duck>> flocks = service.getAllFlocks();

        flocks.forEach(System.out::println);
    }

    private void addRaceEventUI(){
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the race event's name");
        String eventName =  scanner.next();

        List<Long> participantsIds = new ArrayList<>();
        System.out.println("Enter the duck's id's(they must be SWIMMING ducks):");
        System.out.println("To stop insert 0!");

        Long id = scanner.nextLong();
        while(id!=0){
            participantsIds.add(id);
            id = scanner.nextLong();
        }

        if(participantsIds.isEmpty()){
            System.out.println("No members were inserted");
            return;
        }

        try {
            service.addRaceEvent(eventName, participantsIds);
        }
        catch (UserException e) {
            System.out.println(e.getMessage());
        }
    }

    private void printRaceEventsUI(){
        List<Event> events = service.getAllEvents();
        events.forEach(System.out::println);
    }

    private void selectMDucksFromRaceEventUI() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the race event's id:");
        Long id = Long.parseLong(scanner.nextLine());

        System.out.println("Enter the amount of ducks you wish to select:");
        int m = Integer.parseInt(scanner.nextLine());

        try {
            List<SwimmingDuck> rez = service.selectParticipants(id, m);

            if (rez.isEmpty()) {
                System.out.println("No ducks were selected!");
            } else {
                System.out.println("Selected ducks (ordered by stamina):");
                for (SwimmingDuck duck : rez) {
                    System.out.println("- " + duck.getUsername() +
                            " (stamina: " + duck.getStamina() + ")");
                }
            }
        } catch (UserException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    private void getAveragePerformanceUI(){
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the flock's id:");
        Long id = Long.parseLong(scanner.nextLine());

        var rezultat = service.getAveragePerformance(id);
        System.out.println("The average performance of the flock is "+ rezultat);
    }


    public void run(){
        String option;
        Scanner scanner = new Scanner(System.in);
        label:
        while(true){
            printMenu();
            System.out.println("Please choose an option:");
            option = scanner.next();

            switch (option) {
                case "0":
                    break label;
                case "1":
                    addUserUI();
                    break;
                case "2":
                    deleteUserUI();
                    break;
                case "3":
                    addFriendshipUI();
                    break;
                case "4":
                    removeFriendshipUI();
                    break;
                case "5":
                    numberOfCommunitiesUI();
                    break;
                case "6":
                    mostSociableCommunityUI();
                    break;
                case "7":
                    printUsersUI();
                    break;
                case "8":
                    printFriendshipsUI();
                    break;
                case "9":
                    addFlockUI();
                    break;
                case "10":
                    printFlocksUI();
                    break;
                case "11":
                    addRaceEventUI();
                    break;
                case "12":
                    printRaceEventsUI();
                    break;
                case "13":
                    selectMDucksFromRaceEventUI();
                    break;
                case "14":
                    getAveragePerformanceUI();
                    break;
                default:
                    System.out.println("Invalid option!");
                    break;
            }
        }
    }


}
