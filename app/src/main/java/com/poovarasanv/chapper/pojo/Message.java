package com.poovarasanv.chapper.pojo;


import java.util.Date;

/**
 * Created by poovarasanv on 19/7/16.
 */
public class Message implements Comparable<Message> {
    int messageId;
    String fromUser;
    String toUser;
    String message;
    Date createdAt;
    boolean status;

    public Message() {
    }

    public Message(int messageId, String fromUser, String toUser, String message, Date createdAt, boolean status) {
        this.messageId = messageId;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.message = message;
        this.createdAt = createdAt;
        this.status = status;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


    @Override
    public int compareTo(Message message) {
        if (this.getMessageId() == message.getMessageId())
            return 0;
        else if (this.getMessageId() > message.getMessageId())
            return -1;
        else
            return 1;
    }
}
