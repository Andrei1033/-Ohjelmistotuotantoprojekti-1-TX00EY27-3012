-- Dumping database structure for classroomattendance_app
DROP DATABASE IF EXISTS `classroomattendance_app`;
CREATE DATABASE IF NOT EXISTS `classroomattendance_app` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_uca1400_ai_ci */;
USE `classroomattendance_app`;

-- Dumping structure for table classroomattendance_app.attendance
DROP TABLE IF EXISTS `attendance`;
CREATE TABLE IF NOT EXISTS `attendance` (
  `attendance_id` int(11) NOT NULL AUTO_INCREMENT,
  `status` varchar(20) NOT NULL CHECK (`status` in ('present','absent','late','excused')),
  `student_id` int(11) NOT NULL,
  `lesson_id` int(11) NOT NULL,
  PRIMARY KEY (`attendance_id`),
  UNIQUE KEY `uq_student_lesson` (`student_id`,`lesson_id`),
  KEY `fk_attendance_lesson` (`lesson_id`),
  CONSTRAINT `fk_attendance_lesson` FOREIGN KEY (`lesson_id`) REFERENCES `lessons` (`lesson_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_attendance_student` FOREIGN KEY (`student_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dumping data for table classroomattendance_app.attendance: ~80 rows (approximately)
DELETE FROM `attendance`;
INSERT INTO `attendance` (`attendance_id`, `status`, `student_id`, `lesson_id`) VALUES
	(1, 'present', 7, 1),
	(2, 'present', 8, 1),
	(3, 'late', 9, 1),
	(4, 'absent', 10, 1),
	(5, 'present', 7, 2),
	(6, 'absent', 8, 2),
	(7, 'present', 9, 2),
	(8, 'present', 10, 2),
	(9, 'present', 7, 3),
	(10, 'present', 8, 3),
	(11, 'excused', 9, 3),
	(12, 'present', 10, 3),
	(13, 'late', 7, 4),
	(14, 'present', 8, 4),
	(15, 'present', 9, 4),
	(16, 'absent', 10, 4),
	(17, 'present', 7, 5),
	(18, 'present', 8, 5),
	(19, 'present', 11, 5),
	(20, 'late', 12, 5),
	(21, 'present', 7, 6),
	(22, 'late', 8, 6),
	(23, 'absent', 11, 6),
	(24, 'present', 12, 6),
	(25, 'present', 7, 7),
	(26, 'present', 8, 7),
	(27, 'present', 11, 7),
	(28, 'present', 12, 7),
	(29, 'absent', 7, 8),
	(30, 'excused', 8, 8),
	(31, 'present', 11, 8),
	(32, 'present', 12, 8),
	(33, 'present', 9, 9),
	(34, 'present', 10, 9),
	(35, 'present', 13, 9),
	(36, 'absent', 14, 9),
	(37, 'late', 9, 10),
	(38, 'present', 10, 10),
	(39, 'present', 13, 10),
	(40, 'present', 14, 10),
	(41, 'present', 9, 11),
	(42, 'absent', 10, 11),
	(43, 'excused', 13, 11),
	(44, 'present', 14, 11),
	(45, 'present', 9, 12),
	(46, 'present', 10, 12),
	(47, 'present', 13, 12),
	(48, 'late', 14, 12),
	(49, 'present', 11, 13),
	(50, 'present', 12, 13),
	(51, 'absent', 15, 13),
	(52, 'present', 16, 13),
	(53, 'present', 11, 14),
	(54, 'late', 12, 14),
	(55, 'present', 15, 14),
	(56, 'present', 16, 14),
	(57, 'excused', 11, 15),
	(58, 'present', 12, 15),
	(59, 'present', 15, 15),
	(60, 'absent', 16, 15),
	(61, 'present', 11, 16),
	(62, 'present', 12, 16),
	(63, 'present', 15, 16),
	(64, 'present', 16, 16),
	(65, 'present', 7, 17),
	(66, 'present', 13, 17),
	(67, 'absent', 15, 17),
	(68, 'late', 16, 17),
	(69, 'present', 7, 18),
	(70, 'present', 13, 18),
	(71, 'present', 15, 18),
	(72, 'present', 16, 18),
	(73, 'late', 7, 19),
	(74, 'absent', 13, 19),
	(75, 'present', 15, 19),
	(76, 'excused', 16, 19),
	(77, 'present', 7, 20),
	(78, 'present', 13, 20),
	(79, 'present', 15, 20),
	(80, 'present', 16, 20);

-- Dumping structure for table classroomattendance_app.course_students
DROP TABLE IF EXISTS `course_students`;
CREATE TABLE IF NOT EXISTS `course_students` (
  `course_id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  PRIMARY KEY (`course_id`,`student_id`),
  KEY `fk_cs_student` (`student_id`),
  CONSTRAINT `fk_cs_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_cs_student` FOREIGN KEY (`student_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dumping data for table classroomattendance_app.course_students: ~20 rows (approximately)
DELETE FROM `course_students`;
INSERT INTO `course_students` (`course_id`, `student_id`) VALUES
	(1, 7),
	(1, 8),
	(1, 9),
	(1, 10),
	(2, 7),
	(2, 8),
	(2, 11),
	(2, 12),
	(3, 9),
	(3, 10),
	(3, 13),
	(3, 14),
	(4, 11),
	(4, 12),
	(4, 15),
	(4, 16),
	(5, 7),
	(5, 13),
	(5, 15),
	(5, 16);

-- Dumping structure for table classroomattendance_app.courses
DROP TABLE IF EXISTS `courses`;
CREATE TABLE IF NOT EXISTS `courses` (
  `course_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(250) NOT NULL,
  `code` varchar(20) NOT NULL DEFAULT '',
  `teacher_id` int(11) NOT NULL,
  PRIMARY KEY (`course_id`),
  KEY `fk_course_teacher` (`teacher_id`),
  CONSTRAINT `fk_course_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dumping data for table classroomattendance_app.courses: ~5 rows (approximately)
DELETE FROM `courses`;
INSERT INTO `courses` (`course_id`, `name`, `code`, `teacher_id`) VALUES
	(1, 'Introduction to Computer Science', 'CS2026', 3),
	(2, 'Algebra II', 'AL2026', 4),
	(3, 'World History', 'WH2026', 5),
	(4, 'Biology 101', 'BI2026', 6),
	(5, 'English Literature', 'EL2026', 3);

-- Dumping structure for table classroomattendance_app.lessons
DROP TABLE IF EXISTS `lessons`;
CREATE TABLE IF NOT EXISTS `lessons` (
  `lesson_id` int(11) NOT NULL AUTO_INCREMENT,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `topic` varchar(255) NOT NULL DEFAULT '',
  `course_id` int(11) NOT NULL,
  PRIMARY KEY (`lesson_id`),
  KEY `fk_lesson_course` (`course_id`),
  CONSTRAINT `fk_lesson_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE,
  CONSTRAINT `chk_lesson_times` CHECK (`end_time` > `start_time`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dumping data for table classroomattendance_app.lessons: ~20 rows (approximately)
DELETE FROM `lessons`;
INSERT INTO `lessons` (`lesson_id`, `start_time`, `end_time`, `topic`, `course_id`) VALUES
	(1, '2025-09-01 09:00:00', '2025-09-01 10:30:00', '', 1),
	(2, '2025-09-03 09:00:00', '2025-09-03 10:30:00', '', 1),
	(3, '2025-09-08 09:00:00', '2025-09-08 10:30:00', '', 1),
	(4, '2025-09-10 09:00:00', '2025-09-10 10:30:00', '', 1),
	(5, '2025-09-01 11:00:00', '2025-09-01 12:30:00', '', 2),
	(6, '2025-09-03 11:00:00', '2025-09-03 12:30:00', '', 2),
	(7, '2025-09-08 11:00:00', '2025-09-08 12:30:00', '', 2),
	(8, '2025-09-10 11:00:00', '2025-09-10 12:30:00', '', 2),
	(9, '2025-09-02 13:00:00', '2025-09-02 14:30:00', '', 3),
	(10, '2025-09-04 13:00:00', '2025-09-04 14:30:00', '', 3),
	(11, '2025-09-09 13:00:00', '2025-09-09 14:30:00', '', 3),
	(12, '2025-09-11 13:00:00', '2025-09-11 14:30:00', '', 3),
	(13, '2025-09-02 15:00:00', '2025-09-02 16:30:00', '', 4),
	(14, '2025-09-04 15:00:00', '2025-09-04 16:30:00', '', 4),
	(15, '2025-09-09 15:00:00', '2025-09-09 16:30:00', '', 4),
	(16, '2025-09-11 15:00:00', '2025-09-11 16:30:00', '', 4),
	(17, '2025-09-01 14:00:00', '2025-09-01 15:30:00', '', 5),
	(18, '2025-09-05 14:00:00', '2025-09-05 15:30:00', '', 5),
	(19, '2025-09-08 14:00:00', '2025-09-08 15:30:00', '', 5),
	(20, '2025-09-12 14:00:00', '2025-09-12 15:30:00', '', 5);

-- Dumping structure for table classroomattendance_app.users
DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `first_name` varchar(100) NOT NULL,
  `last_name` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(20) NOT NULL CHECK (`role` in ('student','teacher','admin')),
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Dumping data for table classroomattendance_app.users: ~16 rows (approximately)
DELETE FROM `users`;
INSERT INTO `users` (`user_id`, `email`, `first_name`, `last_name`, `password`, `role`) VALUES
	(1, 'admin1@school.edu', 'Alice', 'Admin', '$2b$10$abcdefghijklmnopqrstuv', 'admin'),
	(2, 'admin2@school.edu', 'Bob', 'Manager', '$2b$10$abcdefghijklmnopqrstuv', 'admin'),
	(3, 't.smith@school.edu', 'Thomas', 'Smith', '$2b$10$abcdefghijklmnopqrstuv', 'teacher'),
	(4, 't.jones@school.edu', 'Sarah', 'Jones', '$2b$10$abcdefghijklmnopqrstuv', 'teacher'),
	(5, 't.lee@school.edu', 'David', 'Lee', '$2b$10$abcdefghijklmnopqrstuv', 'teacher'),
	(6, 't.brown@school.edu', 'Emma', 'Brown', '$2b$10$abcdefghijklmnopqrstuv', 'teacher'),
	(7, 's.anderson@school.edu', 'Liam', 'Anderson', '$2b$10$abcdefghijklmnopqrstuv', 'student'),
	(8, 's.garcia@school.edu', 'Mia', 'Garcia', '$2b$10$abcdefghijklmnopqrstuv', 'student'),
	(9, 's.miller@school.edu', 'Noah', 'Miller', '$2b$10$abcdefghijklmnopqrstuv', 'student'),
	(10, 's.davis@school.edu', 'Olivia', 'Davis', '$2b$10$abcdefghijklmnopqrstuv', 'student'),
	(11, 's.wilson@school.edu', 'Ethan', 'Wilson', '$2b$10$abcdefghijklmnopqrstuv', 'student'),
	(12, 's.moore@school.edu', 'Ava', 'Moore', '$2b$10$abcdefghijklmnopqrstuv', 'student'),
	(13, 's.taylor@school.edu', 'Lucas', 'Taylor', '$2b$10$abcdefghijklmnopqrstuv', 'student'),
	(14, 's.thomas@school.edu', 'Sophia', 'Thomas', '$2b$10$abcdefghijklmnopqrstuv', 'student'),
	(15, 's.jackson@school.edu', 'Mason', 'Jackson', '$2b$10$abcdefghijklmnopqrstuv', 'student'),
	(16, 's.white@school.edu', 'Isabella', 'White', '$2b$10$abcdefghijklmnopqrstuv', 'student');
