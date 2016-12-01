package com.tesca.dabbaapp;

import java.io.Serializable;

/**
 * Created by drdr_ on 18/11/2016.
 */

public class Custom_User implements Serializable {

    User_Dabba user;
    String phone_number;

    public Custom_User() {
    }

    public Custom_User(User_Dabba user, String phone_number) {
        this.user = user;
        this.phone_number = phone_number;
    }

    public User_Dabba getUser() {
        return user;
    }

    public void setUser(User_Dabba user) {
        this.user = user;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
