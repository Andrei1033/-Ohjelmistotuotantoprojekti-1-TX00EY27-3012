package com.example.app.Model.StudentComponents;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {

    // ---------------------------------------------------------------
    // Constructor / getters
    // ---------------------------------------------------------------

    @Test
    void constructor_storesAllFinalFieldsCorrectly() {
        Course course = new Course(1, "CS101", "Java Basics", 50);

        assertEquals(1, course.getId());
        assertEquals("CS101", course.getCode());
        assertEquals("Java Basics", course.getName());
        assertEquals(50, course.getTeacherId());
    }

    @Test
    void constructor_allowsNullCodeAndName() {
        Course course = new Course(2, null, null, 7);

        assertNull(course.getCode());
        assertNull(course.getName());
        assertEquals(2, course.getId());
        assertEquals(7, course.getTeacherId());
    }

    @Test
    void constructor_allowsZeroAndNegativeIds() {
        // Documents current behavior — no validation is performed.
        Course zero = new Course(0, "X", "Y", 0);
        Course negative = new Course(-1, "X", "Y", -5);

        assertEquals(0, zero.getId());
        assertEquals(0, zero.getTeacherId());
        assertEquals(-1, negative.getId());
        assertEquals(-5, negative.getTeacherId());
    }

    // ---------------------------------------------------------------
    // lessonCount
    // ---------------------------------------------------------------

    @Test
    void lessonCount_defaultsToZero() {
        Course course = new Course(1, "CS101", "Java", 50);

        assertEquals(0, course.getLessonCount(),
                "lessonCount should default to 0");
    }

    @Test
    void setLessonCount_storesPositiveValue() {
        Course course = new Course(1, "CS101", "Java", 50);
        course.setLessonCount(5);

        assertEquals(5, course.getLessonCount());
    }

    @Test
    void setLessonCount_acceptsZero() {
        Course course = new Course(1, "CS101", "Java", 50);
        course.setLessonCount(3);
        course.setLessonCount(0);

        assertEquals(0, course.getLessonCount());
    }

    @Test
    void setLessonCount_acceptsNegativeValue_byCurrentBehavior() {
        // Documents current behavior — no validation is performed.
        Course course = new Course(1, "CS101", "Java", 50);
        course.setLessonCount(-2);

        assertEquals(-2, course.getLessonCount());
    }

    @Test
    void setLessonCount_lastWriteWins() {
        Course course = new Course(1, "CS101", "Java", 50);
        course.setLessonCount(3);
        course.setLessonCount(7);
        course.setLessonCount(1);

        assertEquals(1, course.getLessonCount());
    }

    // ---------------------------------------------------------------
    // Immutability of the final fields
    // ---------------------------------------------------------------

    @Test
    void id_code_name_teacherId_areFinal() throws Exception {
        for (String fieldName :
                Arrays.asList("id", "code", "name", "teacherId")) {

            Field f = Course.class.getDeclaredField(fieldName);
            assertTrue(Modifier.isFinal(f.getModifiers()),
                    fieldName + " should be final");
            assertTrue(Modifier.isPrivate(f.getModifiers()),
                    fieldName + " should be private");
        }
    }

    @Test
    void lessonCount_isNotFinal() throws Exception {
        Field f = Course.class.getDeclaredField("lessonCount");
        assertFalse(Modifier.isFinal(f.getModifiers()),
                "lessonCount must remain mutable");
        assertTrue(Modifier.isPrivate(f.getModifiers()),
                "lessonCount should be private");
    }

    @Test
    void noSettersExistForImmutableFields() {
        boolean hasIdSetter = Arrays.stream(Course.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("setId"));
        boolean hasCodeSetter = Arrays.stream(Course.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("setCode"));
        boolean hasNameSetter = Arrays.stream(Course.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("setName"));
        boolean hasTeacherSetter = Arrays.stream(Course.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("setTeacherId"));

        assertFalse(hasIdSetter);
        assertFalse(hasCodeSetter);
        assertFalse(hasNameSetter);
        assertFalse(hasTeacherSetter);
    }

    // ---------------------------------------------------------------
    // toString
    // ---------------------------------------------------------------

    @Test
    void toString_returnsExpectedFormat() {
        Course course = new Course(1, "CS101", "Java Basics", 50);

        assertEquals(
                "Course{id=1, code='CS101', name='Java Basics'}",
                course.toString()
        );
    }

    @Test
    void toString_handlesNullCodeAndName() {
        Course course = new Course(2, null, null, 7);

        assertEquals(
                "Course{id=2, code='null', name='null'}",
                course.toString()
        );
    }

    @Test
    void toString_doesNotIncludeTeacherIdOrLessonCount() {
        Course course = new Course(1, "CS101", "Java", 50);
        course.setLessonCount(4);

        String s = course.toString();

        assertFalse(s.contains("teacherId"),
                "toString should not expose teacherId: " + s);
        assertFalse(s.contains("lessonCount"),
                "toString should not expose lessonCount: " + s);
    }

    // ---------------------------------------------------------------
    // equals / hashCode (documents current behavior)
    // ---------------------------------------------------------------

    @Test
    void twoCoursesWithSameData_areNotEqual_byDefault() {
        Course a = new Course(1, "CS101", "Java", 50);
        Course b = new Course(1, "CS101", "Java", 50);

        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void sameInstance_isEqualToItself() {
        Course a = new Course(1, "CS101", "Java", 50);

        assertEquals(a, a);
        assertEquals(a.hashCode(), a.hashCode());
    }
}