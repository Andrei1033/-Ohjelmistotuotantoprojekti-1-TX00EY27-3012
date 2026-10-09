# Classroom Attendance System

Classroom Attendance System is a JavaFX desktop application for recording and
monitoring attendance in lessons. The system is intended for students,
teachers and administrators in an educational organisation. Students can log
in, mark their attendance and review their attendance history and percentage.
Teachers can manage courses and lessons, view student attendance and edit
attendance records. Administrators can manage the system's user data.

The main objective is to replace manual attendance handling with a shared,
structured system. This reduces recording errors, makes attendance information
available to the appropriate users and gives teachers a more efficient way to
manage lessons.

## Setup and execution

### Prerequisites

Install the following before running the project locally:

1. JDK 21 and Maven 3.9 or newer
2. Git
3. Access to the MariaDB instance, including the required Metropolia VPN
   connection, when using the shared database
4. JavaFX desktop support supplied by the Maven dependencies

### Clone and configure

```bash
git clone https://github.com/Andrei1033/-Ohjelmistotuotantoprojekti-1-TX00EY27-3012.git
cd -Ohjelmistotuotantoprojekti-1-TX00EY27-3012/Classroom_Attendance_System
```

Review `src/main/resources/database.properties` and configure the database URL,
username and password for the environment. Do not publish real credentials.
The SQL schema and sample data can be loaded into a suitable MariaDB database
using [`Classroom_Attendance_System_2.sql`](Documents/SQLKaaviot/Classroom_Attendance_System_2.sql)
from the repository root.

### Build and test

```bash
mvn clean test
mvn clean package
```

The package command produces
`target/Classroom_Attendance_System.jar`, an executable fat JAR containing the
application dependencies.

### Run locally

From the `Classroom_Attendance_System` directory:

```bash
mvn javafx:run
```

Alternatively, run the packaged application:

```bash
java -jar target/Classroom_Attendance_System.jar
```

The application opens a JavaFX login window. A working database connection is
required for login and database-backed operations.

### Run with Docker

The included multi-stage [`Dockerfile`](Classroom_Attendance_System/Dockerfile)
builds the application with Java 21, installs the libraries required by
JavaFX and starts it with Xvfb. From `Classroom_Attendance_System`:

```bash
docker build -t classroom-attendance-system .
docker run --rm classroom-attendance-system
```

For the published image:

```bash
docker pull andrei1033/classroom_attendance_system:latest
docker run --rm andrei1033/classroom_attendance_system:latest
```

## Features

- Role-based login for students, teachers and administrators
- Student attendance marking and attendance history
- Attendance percentage calculation
- Teacher course and lesson management
- Teacher attendance review and editing
- User profile management
- Persistent storage of users, courses, lessons and attendance records

## Technology stack and dependencies

| Area | Technology |
| --- | --- |
| Language and runtime | Java 21 |
| Build tool | Apache Maven; the CI/Docker build uses Maven 3.9.6 |
| User interface | JavaFX 21.0.4 (`javafx-controls`, `javafx-fxml`) |
| Production database | MariaDB, accessed through MariaDB Java Client 3.5.6 |
| Additional JDBC driver | MySQL Connector/J 8.4.0 |
| Test database | H2 2.2.224 in MySQL compatibility mode |
| Unit and integration testing | JUnit Jupiter 5.11.4, Mockito 5.23.0 |
| JavaFX UI testing | TestFX 4.0.18 |
| Coverage | JaCoCo 0.8.12 |
| Configuration | Java properties files and dotenv-java 3.0.0 |
| Packaging | Maven Shade Plugin 3.5.1 creates an executable fat JAR |
| Containerisation | Docker with Eclipse Temurin 21 JRE and Xvfb |
| Continuous integration | Jenkins (`Jenkinsfile`) |
| Collaboration and design | GitHub, Trello and Figma |

The application source is in
[`Classroom_Attendance_System`](Classroom_Attendance_System). Runtime database
configuration is read from
`Classroom_Attendance_System/src/main/resources/database.properties`. The
test configuration in `src/test/resources/database.properties` points to an
in-memory H2 database, so the automated tests do not require the shared
MariaDB server. Database credentials must be supplied through the local
configuration and must not be committed to the repository.

There are no separate translation or resource-bundle files. The user
interface text is currently defined in the JavaFX view classes, with shared
styling in [`style.css`](Classroom_Attendance_System/src/main/resources/style.css).

## Design and development methodology

### Architecture

The application uses a layered architecture with MVC responsibilities:

- **View:** JavaFX screens in `src/main/java/com/example/app/View` render the
  login, student, teacher and administrator interfaces.
- **Controller:** Classes in `.../Controller` handle user actions, validate
  input and coordinate views and data access.
- **Model:** Classes in `.../Model` represent users, roles, courses, lessons
  and attendance records.
- **DAO/data-access layer:** Classes in `.../DaoElements` contain SQL
  operations for users, courses, lessons, teachers and attendance.
- **Database layer:** `DatabaseConnection` centralises JDBC connection
  creation from `database.properties`.

The application starts in `Main`, which launches the JavaFX `App`. After a
successful login, `LoginController` selects the start page according to the
user's role. DAOs use prepared statements and try-with-resources for database
operations. This separation keeps UI code independent from SQL and makes
controllers and models testable with stubs, H2 and Mockito.

Core functionality was implemented incrementally. The login flow validates
input, loads a user through `UserDao` and routes the user to the correct
role-specific view. Controllers pass user actions to DAOs, which execute
prepared SQL statements and map result sets to model objects. JavaFX views
provide the screens and controls for each role, while CSS provides shared
visual styling. The database design uses foreign keys and constraints so that
attendance cannot reference a non-existent student or lesson and duplicate
student/lesson records are prevented.

### Data model

The MariaDB schema is defined in
[`Classroom_Attendance_System_2.sql`](Documents/SQLKaaviot/Classroom_Attendance_System_2.sql).
Its central entities are:

- `users`, with a unique email and a `student`, `teacher` or `admin` role
- `courses`, owned by a teacher
- `course_students`, the many-to-many relationship between courses and students
- `lessons`, belonging to a course and constrained to a valid time interval
- `attendance`, linking a student to a lesson with `present`, `absent`, `late`
  or `excused` status

Foreign keys, unique constraints and check constraints protect the data model.
The ER and relational database designs are available in
[`ER_Kaavio.png`](Documents/Kaaviot/ER_Kaavio.png) and
[`Relaatiotietokantakaavio.png`](Documents/SQLKaaviot/Relaatiotietokantakaavio.png).
The use-case design is available in
[`Käyttötapauskaavio.pdf`](Documents/Kaaviot/Käyttötapauskaavio.pdf).
The project's UML/class and other visual design material is collected in
[`Visio.pdf`](Documents/Visio.pdf) and the data-modelling document
[`Classroom Attendance System Datamallinnus (1).pdf`](Documents/Kaaviot/Classroom%20Attendance%20System%20Datamallinnus%20(1).pdf).

### Development process

Development followed an Agile/Scrum-inspired process. Work was organised in
user stories in a Trello Product Backlog and delivered in sprints. The team
used sprint planning, implementation, review and testing activities to
incrementally develop the UI, database operations and role-specific
functionality. Figma was used for UI planning and the project documentation
and design artefacts are stored in the `Documents` directory.

## Functional testing

Verification combines automated Java tests, database CRUD verification and
CI automation.
Jenkins automates the build and test process, while JaCoCo generates test coverage 
reports to help evaluate the extent of the automated tests.

### Automated tests

The test source tree contains tests for:

- controllers and role-based navigation
- DAOs and database connection behaviour
- model classes and validation
- JavaFX views using TestFX
- mocked collaborators using Mockito

Run the complete automated suite with:

```bash
cd Classroom_Attendance_System
mvn clean test
```

The Maven build creates Surefire reports under `target/surefire-reports` and a
JaCoCo HTML report under `target/site/jacoco`. The Jenkins pipeline also runs
the tests and publishes JaCoCo coverage after the build. The generated report
is available at `target/site/jacoco`, and the execution data is stored in
`target/jacoco.exec`.

### Database verification

The SQL test results in
[`crud db test result.txt`](Documents/SQLKaaviot/crud%20db%20test%20result.txt)
document successful checks for:

- Create, read, update and delete operations
- Foreign-key relationships
- Unique email and student/lesson constraints
- Attendance-status and lesson-time check constraints

The SQL script includes representative sample data for users, courses,
lessons and attendance records. When testing against MariaDB, use a test
database or a disposable dataset rather than modifying shared production
data.

## Continuous integration

`Jenkinsfile` defines the following pipeline:

1. Check out the `main` branch.
2. Build and install with Maven.
3. Run the test suite.
4. Generate and publish JaCoCo coverage.
5. Build the Docker image.
6. Push the image to Docker Hub.

The Jenkins agent therefore verifies compilation, tests and packaging before
publishing a new container image.

## Project documentation

Additional project material is available in [`Documents`](Documents),
including the project plan, UI and data-model designs, use-case diagram and
SQL verification results.

## Team

**SEP1 Team 3**

- Khaled Marai
- Aaro Haavisto
- Andrei Tsizikov
- Sidiiq Mohamed
