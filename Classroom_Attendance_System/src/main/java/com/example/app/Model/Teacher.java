package com.example.app.Model;

public class Teacher extends User {

    public Teacher(int id,  String firstName,  String lastName, String email) {
        super(id, firstName, lastName ,email ,Role.TEACHER);
    }

    public String getInitials() {
        String f = (getFirstName() != null && !getFirstName().isEmpty()) ? getFirstName().substring(0, 1) : "";
        String l = (getLastName() != null && !getLastName().isEmpty()) ? getLastName().substring(0, 1) : "";
        return (f + l).toUpperCase();
    }





}
