package com.example.app.Model;



public class TeacherCourse {

    private final int courseid;

    private final String coursename;

    private final int teacherid;



    public TeacherCourse(int courseid, String coursename, int teacherid) {

        this.courseid = courseid;

        this.coursename = coursename;

        this.teacherid = teacherid;



    }



    public int getCourseid() {

        return courseid;

    }

    public String getCoursename() {

        return coursename;

    }

    public int getTeacherid() {

        return teacherid;

    }







}