import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class EmployeeRegistration {

    public static void mainMenu(Connection connection) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("Employee Management System");
                System.out.println("1. Create Employee");
                System.out.println("2. Read Employee");
                System.out.println("3. Update Employee");
                System.out.println("4. Delete Employee");
                System.out.println("0. Exit");

                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        EmployeeDAO.createEmployee(connection);
                        break;
                    case 2:
                        EmployeeDAO.readEmployee(connection);
                        break;
                    case 3:
                        EmployeeDAO.updateEmployee(connection);
                        break;
                    case 4:
                        EmployeeDAO.deleteEmployee(connection);
                        break;
                    case 0:
                        System.out.println("Exiting Employee Management System. Goodbye!");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a valid option.");
                        break;
                }
            }
        }
    }
}
