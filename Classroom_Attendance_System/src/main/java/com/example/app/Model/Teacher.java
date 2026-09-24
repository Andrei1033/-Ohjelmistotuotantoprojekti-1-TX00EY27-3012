package com.example.app.Model;

import com.example.app.Model.LoginComponents.Role;
import com.example.app.Model.LoginComponents.User;

public class Teacher extends User {

    public Teacher(int id,  String firstName,  String lastName, String email) {
        super(id, firstName, lastName ,email , Role.TEACHER);
    }






}
