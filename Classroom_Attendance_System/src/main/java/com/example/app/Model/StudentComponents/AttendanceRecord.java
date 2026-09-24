package com.example.app.Model.StudentComponents;

import java.time.LocalDateTime;

public class AttendanceRecord {
    private final int lessonId;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final String topic;

    // Database attendance.status value: "present", "absent", "late", or "excused".
    // If no entry has been made, the DAO sets this to "absent" by default.
    private final String status;

    public AttendanceRecord(int lessonId, LocalDateTime startTime, LocalDateTime endTime, String topic, String status) {
        this.lessonId = lessonId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.topic = topic;
        this.status = status;
    }

    public int getLessonId() {
        return lessonId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getTopic() {
        return topic;
    }

    public String getStatus() {
        return status;
    }
}
