import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class EmployeeDAO {

    public static void createEmployee(Connection connection) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter Employee Name: ");
            String employeeName = scanner.nextLine();

            System.out.print("Enter Contact Number: ");
            String contactNumber = scanner.nextLine();

            System.out.print("Enter Gender: ");
            String gender = scanner.nextLine();

            System.out.print("Enter Date of Birth (YYYY-MM-DD): ");
            String dateOfBirth = scanner.nextLine();

            System.out.print("Enter Job Type (Regular/Piecework/Admin): ");
            String jobTypeDescription = scanner.nextLine().toLowerCase();

            Employee.insertEmployee(connection, employeeName, contactNumber, gender, dateOfBirth, jobTypeDescription);
        }
    }

    public static void readEmployee(Connection connection) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter Employee ID: ");
            int employeeId = scanner.nextInt();

            Employee employee = Employee.getEmployeeById(connection, employeeId);
            if (employee != null) {
                System.out.println("Employee Details:");
                System.out.println("Employee ID: " + employee.getEmployeeId());
                System.out.println("Employee Name: " + employee.getEmployeeName());
                System.out.println("Contact Number: " + employee.getContactNumber());
                System.out.println("Gender: " + employee.getGender());
                System.out.println("Date of Birth: " + employee.getDateOfBirth());
                System.out.println("Job Type: " + employee.getJobTypeDescription());
            } else {
                System.out.println("Employee not found.");
            }
        }
    }

    public static void updateEmployee(Connection connection) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter Employee ID to update: ");
            int employeeId = scanner.nextInt();

            scanner.nextLine();  // Consume the newline character after reading the employeeId

            Employee employee = Employee.getEmployeeById(connection, employeeId);
            if (employee != null) {
                System.out.println("Current Employee Details:");
                System.out.println("Employee ID: " + employee.getEmployeeId());
                System.out.println("Employee Name: " + employee.getEmployeeName());
                System.out.println("Contact Number: " + employee.getContactNumber());
                System.out.println("Gender: " + employee.getGender());
                System.out.println("Date of Birth: " + employee.getDateOfBirth());
                System.out.println("Job Type: " + employee.getJobTypeDescription());

                // Prompt the user for new information
                System.out.println("Enter new information (press Enter to keep current value):");

                // Read the new name, handling the newline character
                System.out.print("New Employee Name: ");
                String newEmployeeName = scanner.nextLine().trim();

                // Handle the case where the user presses Enter without entering anything
                if (newEmployeeName.isEmpty()) {
                    newEmployeeName = employee.getEmployeeName();
                }

                // Read the other information as before
                System.out.print("New Contact Number: ");
                String newContactNumber = scanner.nextLine().trim();
                if (newContactNumber.isEmpty()) {
                    newContactNumber = employee.getContactNumber();
                }

                System.out.print("New Gender: ");
                String newGender = scanner.nextLine().trim();
                if (newGender.isEmpty()) {
                    newGender = employee.getGender();
                }

                System.out.print("New Date of Birth (YYYY-MM-DD): ");
                String newDateOfBirth = scanner.nextLine().trim();
                if (newDateOfBirth.isEmpty()) {
                    newDateOfBirth = employee.getDateOfBirth();
                }

                System.out.print("New Job Type (Regular/Piecework/Admin): ");
                String newJobTypeDescription = scanner.nextLine().trim().toLowerCase();
                if (newJobTypeDescription.isEmpty()) {
                    newJobTypeDescription = employee.getJobTypeDescription();
                }

                Employee.updateEmployee(connection, employeeId, newEmployeeName, newContactNumber, newGender, newDateOfBirth, newJobTypeDescription);
                System.out.println("Employee successfully updated.");
            } else {
                System.out.println("Employee not found.");
            }
        }
    }



    public static void deleteEmployee(Connection connection) {
        // Implement delete employee logic using EmployeeDAO
        // For example, prompt the user for an employee ID and call EmployeeDAO.deleteEmployee
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter Employee ID to delete: ");
            int employeeId = scanner.nextInt();

            Employee.deleteEmployee(connection, employeeId);
        }
    }

    // Additional CRUD methods as needed...

    // Additional methods to interact with the database as needed...
}
