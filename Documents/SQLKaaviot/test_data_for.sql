USE `classroomattendance_app`;

-- ============================================================
-- 1. USERS
-- ============================================================
-- Password hashes below are for the plaintext password: "password123"
-- (Example bcrypt-style placeholder; replace with real hashes in production.)

INSERT INTO users (email, first_name, last_name, password, role) VALUES
-- Admins
('admin1@school.edu',   'Alice',   'Admin',    '$2b$10$abcdefghijklmnopqrstuv', 'admin'),
('admin2@school.edu',   'Bob',     'Manager',  '$2b$10$abcdefghijklmnopqrstuv', 'admin'),
-- Teachers
('t.smith@school.edu',  'Thomas',  'Smith',    '$2b$10$abcdefghijklmnopqrstuv', 'teacher'),
('t.jones@school.edu',  'Sarah',   'Jones',    '$2b$10$abcdefghijklmnopqrstuv', 'teacher'),
('t.lee@school.edu',    'David',   'Lee',      '$2b$10$abcdefghijklmnopqrstuv', 'teacher'),
('t.brown@school.edu',  'Emma',    'Brown',    '$2b$10$abcdefghijklmnopqrstuv', 'teacher'),
-- Students
('s.anderson@school.edu','Liam',   'Anderson', '$2b$10$abcdefghijklmnopqrstuv', 'student'),
('s.garcia@school.edu',  'Mia',    'Garcia',   '$2b$10$abcdefghijklmnopqrstuv', 'student'),
('s.miller@school.edu',  'Noah',   'Miller',   '$2b$10$abcdefghijklmnopqrstuv', 'student'),
('s.davis@school.edu',   'Olivia', 'Davis',    '$2b$10$abcdefghijklmnopqrstuv', 'student'),
('s.wilson@school.edu',  'Ethan',  'Wilson',   '$2b$10$abcdefghijklmnopqrstuv', 'student'),
('s.moore@school.edu',   'Ava',    'Moore',    '$2b$10$abcdefghijklmnopqrstuv', 'student'),
('s.taylor@school.edu',  'Lucas',  'Taylor',   '$2b$10$abcdefghijklmnopqrstuv', 'student'),
('s.thomas@school.edu',  'Sophia', 'Thomas',   '$2b$10$abcdefghijklmnopqrstuv', 'student'),
('s.jackson@school.edu', 'Mason',  'Jackson',  '$2b$10$abcdefghijklmnopqrstuv', 'student'),
('s.white@school.edu',   'Isabella','White',   '$2b$10$abcdefghijklmnopqrstuv', 'student');

-- ============================================================
-- 2. COURSES
-- ============================================================
-- teacher_id references users.user_id (teachers are IDs 3–6)

INSERT INTO courses (name, teacher_id) VALUES
                                           ('Introduction to Computer Science', 3),
                                           ('Algebra II',                       4),
                                           ('World History',                    5),
                                           ('Biology 101',                      6),
                                           ('English Literature',               3);

-- ============================================================
-- 3. COURSE_STUDENTS (enrollments)
-- ============================================================
-- student_id references users.user_id (students are IDs 7–16)

INSERT INTO course_students (course_id, student_id) VALUES
-- Course 1: Intro to CS (students 7,8,9,10)
(1, 7), (1, 8), (1, 9), (1, 10),
-- Course 2: Algebra II (students 7,8,11,12)
(2, 7), (2, 8), (2, 11), (2, 12),
-- Course 3: World History (students 9,10,13,14)
(3, 9), (3, 10), (3, 13), (3, 14),
-- Course 4: Biology 101 (students 11,12,15,16)
(4, 11), (4, 12), (4, 15), (4, 16),
-- Course 5: English Literature (students 7,13,15,16)
(5, 7), (5, 13), (5, 15), (5, 16);

-- ============================================================
-- 4. LESSONS
-- ============================================================
-- Each course gets 4 lessons spread across a few weeks.

INSERT INTO lessons (start_time, end_time, course_id) VALUES
-- Course 1: Intro to CS
('2025-09-01 09:00:00', '2025-09-01 10:30:00', 1),
('2025-09-03 09:00:00', '2025-09-03 10:30:00', 1),
('2025-09-08 09:00:00', '2025-09-08 10:30:00', 1),
('2025-09-10 09:00:00', '2025-09-10 10:30:00', 1),
-- Course 2: Algebra II
('2025-09-01 11:00:00', '2025-09-01 12:30:00', 2),
('2025-09-03 11:00:00', '2025-09-03 12:30:00', 2),
('2025-09-08 11:00:00', '2025-09-08 12:30:00', 2),
('2025-09-10 11:00:00', '2025-09-10 12:30:00', 2),
-- Course 3: World History
('2025-09-02 13:00:00', '2025-09-02 14:30:00', 3),
('2025-09-04 13:00:00', '2025-09-04 14:30:00', 3),
('2025-09-09 13:00:00', '2025-09-09 14:30:00', 3),
('2025-09-11 13:00:00', '2025-09-11 14:30:00', 3),
-- Course 4: Biology 101
('2025-09-02 15:00:00', '2025-09-02 16:30:00', 4),
('2025-09-04 15:00:00', '2025-09-04 16:30:00', 4),
('2025-09-09 15:00:00', '2025-09-09 16:30:00', 4),
('2025-09-11 15:00:00', '2025-09-11 16:30:00', 4),
-- Course 5: English Literature
('2025-09-01 14:00:00', '2025-09-01 15:30:00', 5),
('2025-09-05 14:00:00', '2025-09-05 15:30:00', 5),
('2025-09-08 14:00:00', '2025-09-08 15:30:00', 5),
('2025-09-12 14:00:00', '2025-09-12 15:30:00', 5);

-- ============================================================
-- 5. ATTENDANCE
-- ============================================================
-- Note: student must be enrolled in the course that owns the lesson.
-- Lesson IDs 1–4   -> Course 1 (students 7,8,9,10)
-- Lesson IDs 5–8   -> Course 2 (students 7,8,11,12)
-- Lesson IDs 9–12  -> Course 3 (students 9,10,13,14)
-- Lesson IDs 13–16 -> Course 4 (students 11,12,15,16)
-- Lesson IDs 17–20 -> Course 5 (students 7,13,15,16)

INSERT INTO attendance (status, student_id, lesson_id) VALUES
-- Course 1 lessons
( 'present', 7, 1), ( 'present', 8, 1), ( 'late',   9, 1), ( 'absent', 10, 1),
( 'present', 7, 2), ( 'absent',  8, 2), ( 'present',9, 2), ( 'present',10, 2),
( 'present', 7, 3), ( 'present', 8, 3), ( 'excused',9, 3), ( 'present',10, 3),
( 'late',    7, 4), ( 'present', 8, 4), ( 'present',9, 4), ( 'absent', 10, 4),
-- Course 2 lessons
( 'present', 7, 5), ( 'present', 8, 5), ( 'present',11, 5), ( 'late',   12, 5),
( 'present', 7, 6), ( 'late',    8, 6), ( 'absent', 11, 6), ( 'present',12, 6),
( 'present', 7, 7), ( 'present', 8, 7), ( 'present',11, 7), ( 'present',12, 7),
( 'absent',  7, 8), ( 'excused', 8, 8), ( 'present',11, 8), ( 'present',12, 8),
-- Course 3 lessons
( 'present', 9, 9),  ( 'present',10, 9),  ( 'present',13, 9),  ( 'absent', 14, 9),
( 'late',    9, 10), ( 'present',10, 10), ( 'present',13, 10), ( 'present',14, 10),
( 'present', 9, 11), ( 'absent', 10, 11), ( 'excused',13, 11), ( 'present',14, 11),
( 'present', 9, 12), ( 'present',10, 12), ( 'present',13, 12), ( 'late',   14, 12),
-- Course 4 lessons
( 'present',11, 13), ( 'present',12, 13), ( 'absent', 15, 13), ( 'present',16, 13),
( 'present',11, 14), ( 'late',   12, 14), ( 'present',15, 14), ( 'present',16, 14),
( 'excused',11, 15), ( 'present',12, 15), ( 'present',15, 15), ( 'absent', 16, 15),
( 'present',11, 16), ( 'present',12, 16), ( 'present',15, 16), ( 'present',16, 16),
-- Course 5 lessons
( 'present', 7, 17), ( 'present',13, 17), ( 'absent', 15, 17), ( 'late',   16, 17),
( 'present', 7, 18), ( 'present',13, 18), ( 'present',15, 18), ( 'present',16, 18),
( 'late',    7, 19), ( 'absent', 13, 19), ( 'present',15, 19), ( 'excused',16, 19),
( 'present', 7, 20), ( 'present',13, 20), ( 'present',15, 20), ( 'present',16, 20);