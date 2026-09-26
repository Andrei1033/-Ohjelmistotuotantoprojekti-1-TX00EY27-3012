package com.example.app.Model.LoginComponents;

public class User {

    private final int id;
    private String email;
    private String firstName;
    private String lastName;
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

    // Sallitaan nimen ja sähköpostin päivitys paikan päällä, jotta esim.
    // sivupalkki näyttää heti profiilimuokkauksen jälkeen uudet tiedot
    // ilman että koko User-oliota tarvitsee rakentaa uudelleen ja
    // välittää ketjussa läpi eri Controllereiden.
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName)   { this.lastName = lastName; }
    public void setEmail(String email)         { this.email = email; }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", email='" + email + "', role=" + role + "}";
    }
}