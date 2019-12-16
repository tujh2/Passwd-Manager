package com.wnp.passwdmanager.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "passwordList")
public class PasswordEntity {
    @PrimaryKey(autoGenerate = true)
    int ID;
    String domain_name, URL, username, password;
}
