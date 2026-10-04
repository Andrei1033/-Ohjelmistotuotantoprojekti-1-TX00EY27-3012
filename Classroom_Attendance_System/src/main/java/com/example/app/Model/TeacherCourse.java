package com.example.app.Model;

public class TeacherCourse {

    private final int courseid;
    private final String coursename;
    private final String code;
    private final int teacherid;

    public TeacherCourse(int courseid, String coursename, String code, int teacherid) {
        this.courseid = courseid;
        this.coursename = coursename;
        this.code = code;
        this.teacherid = teacherid;
    }

    public int getCourseid() {
        return courseid;
    }

    public String getCoursename() {
        return coursename;
    }

    public String getCode() {
        return code;
    }

    public int getTeacherid() {
        return teacherid;
    }
}