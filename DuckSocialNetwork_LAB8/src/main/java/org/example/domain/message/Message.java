package org.example.domain.message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class Message {
    private Long id;
    private Long sender; //the id of the user that sends the message
    private Set<Long> receivers;
    private String content;
    private LocalDateTime timestamp;
    private Long reply; //poate fi null daca mesajul nu e un reply

    public Message(Long sender, Set<Long> receivers, String content, LocalDateTime timestamp, Long reply) {
        this.sender = sender;
        this.receivers = receivers;
        this.content = content;
        this.timestamp = timestamp;
        this.reply = reply;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSender() {
        return sender;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }

    public Set<Long> getReceivers() {
        return receivers;
    }

    public void setReceivers(Set<Long> receivers) {
        this.receivers = receivers;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getReply() {
        return reply;
    }

    public void setReply(Long reply) {
        this.reply = reply;
    }

    @Override
    public String toString() {
        return this.content + '\'' +  this.timestamp ;
    }
}
