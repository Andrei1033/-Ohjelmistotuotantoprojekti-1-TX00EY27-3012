package com.example.app.Model.LoginComponents;

public class User {

    private final int id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final Role role;

    public User(int userId, String firstName, String lastName, String email, Role role) {
        this.id = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public int getId()          { return id; }
    public String getEmail()    { return email; }
    public String getFirstName(){ return firstName; }
    public String getLastName() { return lastName; }
    public Role getRole()       { return role; }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", email='" + email + "', role=" + role + "}";
    }
}

