
CREATE DATABASE IF NOT EXISTS `classroomattendance_app`;
USE `classroomattendance_app`;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('student', 'teacher', 'admin'))
) ENGINE=InnoDB;

CREATE TABLE courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(250) NOT NULL,
    teacher_id INT NOT NULL,
    CONSTRAINT fk_course_teacher 
        FOREIGN KEY (teacher_id) 
        REFERENCES users(user_id) 
        ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE course_students (
    course_id INT NOT NULL,
    student_id INT NOT NULL,
    PRIMARY KEY (course_id, student_id),
    CONSTRAINT fk_cs_course 
        FOREIGN KEY (course_id) 
        REFERENCES courses(course_id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_cs_student 
        FOREIGN KEY (student_id) 
        REFERENCES users(user_id) 
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE lessons (
    lesson_id INT AUTO_INCREMENT PRIMARY KEY,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    course_id INT NOT NULL,
    CONSTRAINT fk_lesson_course 
        FOREIGN KEY (course_id) 
        REFERENCES courses(course_id) 
        ON DELETE CASCADE,
    CONSTRAINT chk_lesson_times CHECK (end_time > start_time)
) ENGINE=InnoDB;

CREATE TABLE attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(20) NOT NULL CHECK (status IN ('present', 'absent', 'late', 'excused')),
    student_id INT NOT NULL,
    lesson_id INT NOT NULL,
    CONSTRAINT fk_attendance_student 
        FOREIGN KEY (student_id) 
        REFERENCES users(user_id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_attendance_lesson 
        FOREIGN KEY (lesson_id) 
        REFERENCES lessons(lesson_id) 
        ON DELETE CASCADE,
    CONSTRAINT uq_student_lesson UNIQUE (student_id, lesson_id)
) ENGINE=InnoDB;