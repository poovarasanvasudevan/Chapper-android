package com.poovarasanv.chapper.models;

import com.poovarasanv.chapper.pojo.IDClass;

/**
 * Created by poovarasanv on 15/7/16.
 */
public class Contact extends IDClass{
    long id;
    String name;
    String number;
    String image;

    public Contact() {
    }

    public Contact(long id, String name, String number, String image) {
        ID = id;
        this.name = name;
        this.number = number;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
}
