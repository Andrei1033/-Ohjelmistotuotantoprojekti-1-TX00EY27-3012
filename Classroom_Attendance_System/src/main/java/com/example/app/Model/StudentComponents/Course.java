package com.example.app.Model.StudentComponents;

public class Course {
    private final int id;
    private final String code;
    private final String name;
    private final int teacherId;

    private int lessonCount;

    public Course(int id, String code, String name, int teacherId) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.teacherId = teacherId;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public int getLessonCount() {
        return lessonCount;
    }

    public void setLessonCount(int lessonCount) {
        this.lessonCount = lessonCount;
    }

    @Override
    public String toString() {
        return "Course{id=" + id + ", code='" + code + "', name='" + name + "'}";
    }
}
