package com.example.app.Model;


import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

public class Lesson {

    private final int id;
    private final int courseId;
    private final String lessonDate;
    private final String lessonTopic;
    private final String lessonStatus;


    public Lesson(int id, int courseId, String lessonDate, String lessonTopic, String lessonStatus) {
        this.id = id;
        this.courseId = courseId;
        this.lessonDate = lessonDate;
        this.lessonTopic = lessonTopic;
        this.lessonStatus = lessonStatus;

    }

    public int getId() {
        return id;
    }
    public int getCourseId() {
        return courseId;
    }
    public String getLessonDate() {
        return lessonDate;
    }
    public String getLessonTopic() {
        return lessonTopic;
    }
    public String getLessonStatus() {
        return lessonStatus;
    }
    public boolean isDone() {
        return "done".equals(lessonStatus);
    }
    public String getStatusText() {
        if("done".equals(lessonStatus)) return "Merkitty";
        if("ongoing".equals(lessonStatus)) return "Käynnissä";
        return "Odottaa";
    }

    public String getcleanIsoData() {
        if (lessonDate == null || lessonDate.isEmpty()) return null;
        String clean = lessonDate.replace(" ", "T");
        if (clean.contains(".")) {
            clean = clean.substring(0, clean.indexOf("."));
        }
        return clean;
    }

    public String getDayofTheWeek() {
        String cleanDate = getcleanIsoData();
        if (cleanDate == null) return "";

        if (lessonDate == null || lessonDate.isEmpty()) return "";


        try {
            LocalDateTime dateTime = LocalDateTime.parse(cleanDate);
            return dateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        } catch (Exception e) {
            return "";
        }
    }



    public int getLessonTime() {
        String cleanDate = getcleanIsoData();
        if (cleanDate == null) return 0;

        if (lessonDate == null) return 0;
        try {
            String form = lessonDate.replace(" ", "T");
            LocalDateTime time = LocalDateTime.parse(form);
            return (time.getHour() * 3600) + (time.getMinute() * 60) + time.getSecond();

        } catch (Exception e) {
            return 0;
        }


    }
}
