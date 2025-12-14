package org.example.repo.DB;

import org.example.domain.message.Message;
import org.example.repo.Repo;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DBMessageRepo implements Repo<Message> {
    private final String url;
    private final String username;
    private final String password;
    Connection connection;

    public DBMessageRepo(String url, String username, String password) throws SQLException {
        this.url = url;
        this.username = username;
        this.password = password;

        this.connection = DriverManager.getConnection(this.url, this.username, this.password);
    }

    private void addRecievers(Message message) {
        String sql = "INSERT INTO message_receivers(id, receiver) VALUES (?, ?)";
        Long id = message.getId();
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            for (Long receiver : message.getReceivers()) {
                st.setLong(1, id);
                st.setLong(2, receiver); //validat in service ca toti receiverii exista
                st.addBatch();
            }
            st.executeBatch();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void add(Message message) {
        String sql = "INSERT INTO messages(sender, content, timestamp, reply) VALUES(?, ?, ?, ?) RETURNING id";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, message.getSender());
            st.setString(2, message.getContent());
            st.setTimestamp(3, Timestamp.valueOf(message.getTimestamp()));

            if (message.getReply() == null)
                st.setNull(4, Types.BIGINT);
            else
                st.setLong(4, message.getReply());


            var result = st.executeQuery();
            if (result.next()) {
                long id = result.getLong("id");
                message.setId(id);

                addRecievers(message); //de adaugat in tabelul message_receivers
            }

        } catch (SQLException e) {
            throw new RuntimeException("Couldn't insert event", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql_command = "DELETE FROM messages WHERE id = ?;";
        try(var statement = connection.prepareStatement(sql_command)){
            statement.setLong(1, id);
            statement.executeUpdate();
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Set<Long> loadReceivers(Long messageId) throws SQLException {
        Set<Long> receivers = new HashSet<>();

        String sql = "SELECT receiver FROM message_receivers WHERE id = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, messageId);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                receivers.add(rs.getLong("receiver"));
            }
        }
        return receivers;
    }


    @Override
    public List<Message> getAll() {
        List<Message> messages = new ArrayList<>();

        String sql = "SELECT * FROM messages ORDER BY timestamp;";

        try (PreparedStatement st = connection.prepareStatement(sql);
             var rs = st.executeQuery()) {

            while (rs.next()) {
                Long id = rs.getLong("id");
                Long sender =  rs.getLong("sender");
                String content = rs.getString("content");
                LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
                Long reply = rs.getLong("reply");
                if (rs.wasNull())
                    reply = null;

                Set<Long> receivers = loadReceivers(id);

                Message message = new Message(sender,receivers,content,timestamp,reply);
                message.setId(id);

                messages.add(message);
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Error loading all events", ex);
        }

        return messages;
    }

    @Override
    public Message getById(Long id) {
        String sql = "SELECT * FROM messages WHERE id = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, id);
            var rs = st.executeQuery();

            if (rs.next()) {
                Long sender =  rs.getLong("sender");
                String content = rs.getString("content");
                LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
                Long reply = rs.getLong("reply");
                if (rs.wasNull())
                    reply = null;

                Set<Long> receivers = loadReceivers(id);

                Message m = new Message(sender, receivers, content, timestamp, reply);
                m.setId(id);
                return m;
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Error loading event by ID", ex);
        }
        return null;
    }


    public List<Message> getMessagesBetweenUsers(Long user1, Long user2) {
        List<Message> messages = new ArrayList<>();

        String sql = """
            SELECT m.*
            FROM messages m
            JOIN message_receivers mr ON m.id = mr.id
            WHERE (m.sender = ? AND mr.receiver = ?)
               OR (m.sender = ? AND mr.receiver = ?)
            ORDER BY m.timestamp
            """;

        try (PreparedStatement st = connection.prepareStatement(sql)) {

            st.setLong(1, user1);
            st.setLong(2, user2);
            st.setLong(3, user2);
            st.setLong(4, user1);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Long id = rs.getLong("id");
                Long sender = rs.getLong("sender");
                String content = rs.getString("content");
                LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();

                Long reply = rs.getLong("reply");
                if (rs.wasNull())
                    reply = null;

                //load receivers
                Set<Long> receivers = loadReceivers(id);

                Message m = new Message(sender, receivers, content, timestamp, reply);
                m.setId(id);

                messages.add(m);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving messages between users", e);
        }

        return messages;
    }

}
