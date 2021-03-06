package com.wnp.passwdmanager.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "passwordList")
public class PasswordEntity implements Serializable {
    @PrimaryKey(autoGenerate = true)
    int ID;

    private String domain_name;
    private String URL, username, password;

    public PasswordEntity(String domain_name, String URL, String username, String password) {
        this.domain_name = domain_name;
        this.URL = URL;
        this.username = username;
        this.password = password;
    }

    int getId() {
        return ID;
    }

    public String getDomain_name() {
        return domain_name;
    }

    public void setDomain_name(String domain_name) {
        this.domain_name = domain_name;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String url) {
        this.URL = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
