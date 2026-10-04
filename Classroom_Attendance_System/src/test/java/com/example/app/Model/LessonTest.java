package com.example.app.Model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Lesson Model Tests")
class LessonTest {

    @Nested
    @DisplayName("Status & State Tests")
    class StatusTests {

        @Test
        @DisplayName("getLessonStatus palauttaa oikeat suomennokset")
        void testGetLessonStatus() {
            var doneLesson = new Lesson(1, 101, "2026-10-05 10:00:00", "2026-10-05 11:00:00", "Math", "done");
            var ongoingLesson = new Lesson(2, 101, "2026-10-05 10:00:00", "2026-10-05 11:00:00", "Physics", "ongoing");
            var pendingLesson = new Lesson(3, 101, "2026-10-05 10:00:00", "2026-10-05 11:00:00", "History", "pending");

            assertAll(
                    () -> assertEquals("Merkitty", doneLesson.getLessonStatus()),
                    () -> assertEquals("Käynnissä", ongoingLesson.getLessonStatus()),
                    () -> assertEquals("Odottaa", pendingLesson.getLessonStatus())
            );
        }

        @Test
        @DisplayName("isDone palauttaa true vain jos status on 'done'")
        void testIsDone() {
            var doneLesson = new Lesson(1, 101, "2026-10-05 10:00:00", "2026-10-05 11:00:00", "Math", "done");
            var pendingLesson = new Lesson(2, 101, "2026-10-05 10:00:00", "2026-10-05 11:00:00", "Math", "pending");

            assertTrue(doneLesson.isDone());
            assertFalse(pendingLesson.isDone());
        }
    }

    @Nested
    @DisplayName("Date Formatting Tests")
    class DateFormattingTests {

        @ParameterizedTest(name = "Kello/päivä {0} muotoillaan muotoon {1}")
        @CsvSource({
                "2026-10-05 10:00:00, 5.10.2026",
                "2026-01-01 08:30:00, 1.1.2026",
                "2026-12-31T23:59:59, 31.12.2026",    // Testaa 'T'-erotinta
                "2026-05-15 12:00:00.1, 15.5.2026"   // Testaa sekunnin murto-osia [.S]
        })
        @DisplayName("getFormattedData muotoilee voimassa olevat aikaleimat oikein")
        void testGetFormattedDataValid(String inputTime, String expectedDate) {
            var lesson = new Lesson(1, 101, inputTime, "2026-10-05 11:00:00", "Topic", "pending");
            assertEquals(expectedDate, lesson.getFormattedData());
        }

        @Test
        @DisplayName("getFormattedData palauttaa raaka-arvon fallbackina jos aika on virheellinen tai null")
        void testGetFormattedDataFallback() {
            var invalidTimeLesson = new Lesson(1, 101, "virheellinen-aika", "2026-10-05 11:00:00", "Topic", "pending");
            var nullTimeLesson = new Lesson(2, 101, null, "2026-10-05 11:00:00", "Topic", "pending");

            assertEquals("virheellinen-aika", invalidTimeLesson.getFormattedData());
            assertNull(nullTimeLesson.getFormattedData());
        }

        @Test
        @DisplayName("getDayofTheWeek palauttaa oikean viikonpäivälyhenteen (US locale)")
        void testGetDayOfTheWeek() {
            // 2026-10-05 on maanantai (Mon)
            var lesson = new Lesson(1, 101, "2026-10-05 10:00:00", "2026-10-05 11:00:00", "Topic", "pending");
            assertEquals("Mon", lesson.getDayofTheWeek());
        }

        @Test
        @DisplayName("getDayofTheWeek palauttaa null virheelliselle päivämäärälle tai null-arvolle")
        void testGetDayOfTheWeekInvalid() {
            var invalidLesson = new Lesson(1, 101, "invalid-date", "2026-10-05 11:00:00", "Topic", "pending");
            var nullLesson = new Lesson(2, 101, null, "2026-10-05 11:00:00", "Topic", "pending");

            assertNull(invalidLesson.getDayofTheWeek());
            assertNull(nullLesson.getDayofTheWeek());
        }
    }

    @Nested
    @DisplayName("Getter & Constructor Tests")
    class BasicTests {

        @Test
        @DisplayName("Perus-getterit palauttavat konstruktorissa annetut arvot")
        void testGetters() {
            var lesson = new Lesson(42, 7, "2026-10-05 10:00:00", "2026-10-05 11:00:00", "Java Basics", "done");

            assertAll(
                    () -> assertEquals(42, lesson.getId()),
                    () -> assertEquals(7, lesson.getCourseId()),
                    () -> assertEquals("2026-10-05 10:00:00", lesson.getStartTime()),
                    () -> assertEquals("2026-10-05 11:00:00", lesson.getEndTime()),
                    () -> assertEquals("Java Basics", lesson.getLessonTopic())
            );
        }
    }
}