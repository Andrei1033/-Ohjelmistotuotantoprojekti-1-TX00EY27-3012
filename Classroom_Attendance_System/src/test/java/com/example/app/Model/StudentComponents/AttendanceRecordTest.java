package com.example.app.Model.StudentComponents;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AttendanceRecordTest {

    private static final LocalDateTime START =
            LocalDateTime.of(2025, 1, 10, 9, 0);
    private static final LocalDateTime END =
            LocalDateTime.of(2025, 1, 10, 11, 0);

    // ---------------------------------------------------------------
    // Constructor / getters
    // ---------------------------------------------------------------

    @Test
    void constructor_storesAllFieldsCorrectly() {
        AttendanceRecord record =
                new AttendanceRecord(1, START, END, "Java Basics", "present");

        assertEquals(1, record.getLessonId());
        assertEquals(START, record.getStartTime());
        assertEquals(END, record.getEndTime());
        assertEquals("Java Basics", record.getTopic());
        assertEquals("present", record.getStatus());
    }

    @Test
    void constructor_allowsNullStartAndEndTimes() {
        AttendanceRecord record =
                new AttendanceRecord(2, null, null, "OOP", "absent");

        assertEquals(2, record.getLessonId());
        assertNull(record.getStartTime());
        assertNull(record.getEndTime());
        assertEquals("OOP", record.getTopic());
        assertEquals("absent", record.getStatus());
    }

    @Test
    void constructor_allowsNullTopicAndStatus() {
        AttendanceRecord record =
                new AttendanceRecord(3, START, END, null, null);

        assertNull(record.getTopic());
        assertNull(record.getStatus());
    }

    @Test
    void constructor_allowsZeroAndNegativeLessonId() {
        // Documents current behavior — no validation is performed.
        AttendanceRecord zero =
                new AttendanceRecord(0, START, END, "Topic", "present");
        AttendanceRecord negative =
                new AttendanceRecord(-5, START, END, "Topic", "present");

        assertEquals(0, zero.getLessonId());
        assertEquals(-5, negative.getLessonId());
    }

    // ---------------------------------------------------------------
    // Status values
    // ---------------------------------------------------------------

    @Test
    void status_acceptsAllDocumentedValues() {
        List<String> validStatuses =
                Arrays.asList("present", "absent", "late", "excused");

        for (String status : validStatuses) {
            AttendanceRecord record =
                    new AttendanceRecord(1, START, END, "Topic", status);
            assertEquals(status, record.getStatus(),
                    "Status should be preserved for: " + status);
        }
    }

    @Test
    void status_isCaseSensitiveAndNotNormalized() {
        // The DAO / DB is responsible for normalization, not this class.
        AttendanceRecord record =
                new AttendanceRecord(1, START, END, "Topic", "PRESENT");

        assertEquals("PRESENT", record.getStatus());
        assertNotEquals("present", record.getStatus());
    }

    // ---------------------------------------------------------------
    // Immutability
    // ---------------------------------------------------------------

    @Test
    void allFieldsAreFinal() throws Exception {
        for (Field field : AttendanceRecord.class.getDeclaredFields()) {
            assertTrue(Modifier.isFinal(field.getModifiers()),
                    "Field should be final: " + field.getName());
        }
    }

    @Test
    void allFieldsArePrivate() throws Exception {
        for (Field field : AttendanceRecord.class.getDeclaredFields()) {
            assertTrue(Modifier.isPrivate(field.getModifiers()),
                    "Field should be private: " + field.getName());
        }
    }

    @Test
    void classHasNoSetters() {
        boolean hasSetter = Arrays.stream(
                        AttendanceRecord.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().startsWith("set"));

        assertFalse(hasSetter,
                "Immutable value class should not expose setters");
    }

    // ---------------------------------------------------------------
    // equals / hashCode / toString (documents current behavior)
    // ---------------------------------------------------------------

    @Test
    void twoRecordsWithSameData_areNotEqual_byDefault() {
        // No equals() override → identity equality.
        AttendanceRecord a =
                new AttendanceRecord(1, START, END, "Topic", "present");
        AttendanceRecord b =
                new AttendanceRecord(1, START, END, "Topic", "present");

        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void sameInstance_isEqualToItself() {
        AttendanceRecord a =
                new AttendanceRecord(1, START, END, "Topic", "present");

        assertEquals(a, a);
        assertEquals(a.hashCode(), a.hashCode());
    }

    @Test
    void toString_usesDefaultImplementation() {
        AttendanceRecord record =
                new AttendanceRecord(1, START, END, "Topic", "present");

        String s = record.toString();

        assertNotNull(s);
        // Default Object.toString format: className@hashCodeHex
        assertTrue(s.startsWith("com.example.app.Model.StudentComponents.AttendanceRecord@"),
                "Unexpected toString(): " + s);
    }
}