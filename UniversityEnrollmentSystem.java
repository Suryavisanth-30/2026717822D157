import java.sql.*;
import java.util.Scanner;

public class UniversityEnrollmentSystem {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/university";
    private static final String USER = "username";
    private static final String PASSWORD = "password";

    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            EnrollmentManager enrollmentManager = new EnrollmentManager(conn);
            enrollmentManager.createTables(); // Create tables if not exists

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("University Enrollment System");
                System.out.println("1. Add Student");
                System.out.println("2. Add Course");
                System.out.println("3. Add Faculty");
                System.out.println("4. Enroll Student");
                System.out.println("5. Unenroll Student");
                System.out.println("6. Assign Faculty to Course");
                System.out.println("7. Remove Course");
                System.out.println("8. Exit");
                System.out.print("Enter your choice: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        enrollmentManager.addStudent();
                        break;
                    case 2:
                        enrollmentManager.addCourse();
                        break;
                    case 3:
                        enrollmentManager.addFaculty();
                        break;
                    case 4:
                        enrollmentManager.enrollStudent();
                        break;
                    case 5:
                        enrollmentManager.unenrollStudent();
                        break;
                    case 6:
                        enrollmentManager.assignFacultyToCourse();
                        break;
                    case 7:
                        enrollmentManager.removeCourse();
                        break;
                    case 8:
                        System.out.println("Exiting...");
                        conn.close();
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please enter a number from 1 to 8.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class EnrollmentManager {
    private Connection connection;

    public EnrollmentManager(Connection connection) {
        this.connection = connection;
    }

    public void createTables() throws SQLException {
        String createStudentsTable = "CREATE TABLE IF NOT EXISTS Students (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(255)," +
                "age INT)";
        String createCoursesTable = "CREATE TABLE IF NOT EXISTS Courses (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(255))";
        String createFacultyTable = "CREATE TABLE IF NOT EXISTS Faculty (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(255))";
        String createEnrollmentsTable = "CREATE TABLE IF NOT EXISTS Enrollments (" +
                "student_id INT," +
                "course_id INT," +
                "FOREIGN KEY (student_id) REFERENCES Students(id)," +
                "FOREIGN KEY (course_id) REFERENCES Courses(id)," +
                "PRIMARY KEY (student_id, course_id))";

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createStudentsTable);
            stmt.executeUpdate(createCoursesTable);
            stmt.executeUpdate(createFacultyTable);
            stmt.executeUpdate(createEnrollmentsTable);
        }
    }

    public void addStudent() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();
        System.out.print("Enter student age: ");
        int age = scanner.nextInt();

        String insertStudent = "INSERT INTO Students (name, age) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertStudent)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.executeUpdate();
            System.out.println("Student added successfully.");
        }
    }

    public void addCourse() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter course name: ");
        String name = scanner.nextLine();

        String insertCourse = "INSERT INTO Courses (name) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertCourse)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            System.out.println("Course added successfully.");
        }
    }

    public void addFaculty() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter faculty name: ");
        String name = scanner.nextLine();

        String insertFaculty = "INSERT INTO Faculty (name) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertFaculty)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            System.out.println("Faculty added successfully.");
        }
    }

    public void enrollStudent() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter student ID: ");
        int studentId = scanner.nextInt();
        System.out.print("Enter course ID: ");
        int courseId = scanner.nextInt();

        String insertEnrollment = "INSERT INTO Enrollments (student_id, course_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertEnrollment)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            pstmt.executeUpdate();
            System.out.println("Student enrolled in the course successfully.");
        }
    }

    public void unenrollStudent() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter student ID: ");
        int studentId = scanner.nextInt();
        System.out.print("Enter course ID: ");
        int courseId = scanner.nextInt();

        String deleteEnrollment = "DELETE FROM Enrollments WHERE student_id = ? AND course_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteEnrollment)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Student unenrolled from the course successfully.");
            } else {
                System.out.println("No enrollment found for the given student and course.");
            }
        }
    }

    public void assignFacultyToCourse() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter faculty ID: ");
        int facultyId = scanner.nextInt();
        System.out.print("Enter course ID: ");
        int courseId = scanner.nextInt();

        // Assuming you have a table to store faculty-course assignments
        // You can adjust this query according to your database schema
        String insertAssignment = "INSERT INTO FacultyCourseAssignment (faculty_id, course_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertAssignment)) {
            pstmt.setInt(1, facultyId);
            pstmt.setInt(2, courseId);
            pstmt.executeUpdate();
            System.out.println("Faculty assigned to the course successfully.");
        }
    }

    public void removeCourse() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter course ID to remove: ");
        int courseId = scanner.nextInt();

        String deleteCourse = "DELETE FROM Courses WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteCourse)) {
            pstmt.setInt(1, courseId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Course removed successfully.");
            } else {
                System.out.println("No course found with the given ID.");
            }
        }
    }
}
