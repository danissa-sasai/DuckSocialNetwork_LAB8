package org.example.repo.DB;

import org.example.domain.Friendship;
import org.example.domain.Person;
import org.example.domain.User;
import org.example.dto.PersonFilterDTO;
import org.example.repo.PagingRepo;
import org.example.repo.Repo;
import org.example.utils.paging.Page;
import org.example.utils.paging.Pageable;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DBFriendshipRepo implements PagingRepo<Friendship> {
    private final String url;
    private final String username;
    private final String password;
    Connection connection;

    /**
     * user repository constructor
     * @param url - the link to the database
     * @param username - the username needed for logging into the database
     * @param password - the password for authentification
     */
    public DBFriendshipRepo(String url, String username, String password) throws SQLException {
        this.url = url;
        this.username = username;
        this.password = password;

        this.connection = DriverManager.getConnection(this.url, this.username, this.password);
    }

    @Override
    public void add(Friendship friendship) {
        String sql_command = "INSERT INTO friendships(user1, user2) VALUES(?,?) RETURNING id;";
        try(PreparedStatement statement = connection.prepareStatement(sql_command)){
            statement.setLong(1, friendship.getUser1());
            statement.setLong(2, friendship.getUser2());

            var result_id = statement.executeQuery();
            if(result_id.next()){
                Long id = result_id.getLong("id");
                friendship.setId(id);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public void delete(Long id) {
        String sql_command = "DELETE FROM friendships WHERE id = ?;";
        try(PreparedStatement statement = connection.prepareStatement(sql_command)){
            statement.setLong(1, id);
            statement.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Friendship> getAll() {
        List<Friendship> friendships = new ArrayList<>();

        String sql = "SELECT * FROM friendships;";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            var result = statement.executeQuery();
            while (result.next()){
                long id = result.getLong("id");
                long user1 = result.getLong("user1");
                long user2 = result.getLong("user2");
                Friendship friendship = new Friendship(user1,user2);
                friendship.setId(id);

                friendships.add(friendship);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return friendships;
    }

    @Override
    public Friendship getById(Long id) {
        String sql = "SELECT * FROM friendships WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                long idFriendship = result.getLong("id");
                long idUser1 = result.getLong("user1");
                long idUser2 = result.getLong("user2");

                Friendship friendship = new Friendship(idUser1,idUser2);
                friendship.setId(idFriendship);
                return friendship;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while searching for the friendship!", e);
        }
        return null;
    }


    //--------------------------------------FOR PAGING------------------------------------------------------------------

    private int friendshipsCount() throws SQLException {
        String sql_command = "SELECT count(*) AS count FROM friendships;";

        try (PreparedStatement statement = connection.prepareStatement(sql_command)) {
            int paramIndex = 0;
            try (ResultSet result = statement.executeQuery()) {
                int totalNumberOfFriendships = 0;
                if (result.next()) {
                    totalNumberOfFriendships = result.getInt("count");
                }
                return totalNumberOfFriendships;
            }
        }
    }

    private List<Friendship> loadFriendshipsOnPage(Pageable pageable) throws SQLException {
        List<Friendship> friendshipsOnPage = new ArrayList<>();

        String sql_command ="SELECT id, user1, user2 FROM friendships";

        sql_command += " limit ? offset ?";
        try (PreparedStatement statement = connection.prepareStatement(sql_command)) {
            int paramIndex = 0;

            statement.setInt(++paramIndex, pageable.getPageSize());
            statement.setInt(++paramIndex, pageable.getPageSize() * pageable.getPageNumber());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");

                    Long user1 = resultSet.getLong("user1");
                    Long user2 = resultSet.getLong("user2");


                    Friendship friendship = new Friendship(user1,user2);
                    friendship.setId(id);

                    friendshipsOnPage.add(friendship);
                }
            }


        }
        return friendshipsOnPage;
    }

    @Override
    public Page<Friendship> findAllOnPage(Pageable pageable) {
        try {
            int totalNumberOfFriendships = friendshipsCount();
            List<Friendship> friendshipsOnPage;
            if (totalNumberOfFriendships > 0) {
                friendshipsOnPage = loadFriendshipsOnPage(pageable);
            } else {
                friendshipsOnPage = new ArrayList<>();
            }
            return new Page<>(friendshipsOnPage, totalNumberOfFriendships);
        } catch (SQLException e) {
            System.out.println("SQLException caught: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


}
