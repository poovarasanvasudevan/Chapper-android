package com.poovarasanv.chapper.pojo;


import java.util.Date;

/**
 * Created by poovarasanv on 19/7/16.
 */
public class Message extends IDClass implements Comparable<Message> {

    String fromUser;
    String toUser;
    String message;
    Date createdAt;
    boolean status;

    public Message() {
    }

    public Message(long id, String fromUser, String toUser, String message, Date createdAt, boolean status) {
        ID = id;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.message = message;
        this.createdAt = createdAt;
        this.status = status;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
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
        if (this.getID() == message.getID())
            return 0;
        else if (this.getID() > message.getID())
            return -1;
        else
            return 1;
    }
}
