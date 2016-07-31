package com.poovarasanv.chapper.models;

import com.poovarasanv.chapper.pojo.IDClass;

/**
 * Created by poovarasanv on 25/7/16.
 */
public class MessageContact extends IDClass {
    long id;
    String name;
    String number;
    String image;
    String message;

    public MessageContact() {
    }

    public MessageContact(long id, String name, String number, String image, String message) {
        ID = id;
        this.name = name;
        this.number = number;
        this.image = image;
        this.message = message;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
