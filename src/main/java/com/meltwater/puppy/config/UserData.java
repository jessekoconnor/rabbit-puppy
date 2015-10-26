package com.meltwater.puppy.config;

public class UserData {

    private String password;
    private boolean admin = false;

    public UserData() {}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "password='" + password + '\'' +
                ", admin=" + admin +
                '}';
    }
}
