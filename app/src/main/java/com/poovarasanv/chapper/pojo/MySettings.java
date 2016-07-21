package com.poovarasanv.chapper.pojo;


import java.util.Date;

/**
 * Created by poovarasanv on 19/7/16.
 */
public class MySettings  {
    String key;
    String value;
    Date createdDate;

    public MySettings() {
    }

    public MySettings(String key, String value, Date createdDate) {
        this.key = key;
        this.value = value;
        this.createdDate = createdDate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
