package com.example.app.Model;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.Locale;

public class Lesson {

    private final int id;
    private final int courseId;
    private final String lessonTopic;
    private final String lessonStatus;
    private final String startTime;
    private final String endTime;

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.S]");


    public Lesson(int id, int courseId, String startTime, String endTime,
                  String lessonTopic, String lessonStatus) {
        this.id = id;
        this.courseId = courseId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lessonTopic = lessonTopic;
        this.lessonStatus = lessonStatus;
    }

    public int getId() {
        return id;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getLessonTopic() {
        return lessonTopic;
    }

    public String getLessonStatus() {
        if ("done".equals(lessonStatus)) return "Merkitty";
        if ("ongoing".equals(lessonStatus)) return "Käynnissä";
        return "Odottaa";
    }

    public boolean isDone() {
        return "done".equals(lessonStatus);
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }


    private LocalDateTime getDateTime() {
        if (startTime == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(startTime, formatter);
        } catch (DateTimeParseException e) {
            System.err.println("Virheellinen aika ja päivä " + startTime);
            return null;
        }
    }

    public String getDayofTheWeek() {
        LocalDateTime date = getDateTime();
        if (date == null) return null;
        return date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US);
    }

    public String getFormattedData() {
        LocalDateTime date = getDateTime();
        if (date == null) {
            return startTime; // fallback: raaka arvo
        }
        return date.format(DateTimeFormatter.ofPattern("d.M.yyyy"));
    }
}