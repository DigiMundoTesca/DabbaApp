package com.tesca.dabbaapp.Estructuras;

import java.io.Serializable;

/**
 * Created by drdr_ on 18/11/2016.
 */

public class User_Dabba implements Serializable {

    String id;
    String username;
    String first_name;
    String last_name;
    String email;

    public User_Dabba() {
    }

    public User_Dabba(String id, String username, String first_name, String last_name, String email) {
        this.id = id;
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
