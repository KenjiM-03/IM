import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        try (Connection connection = DatabaseConnector.getConnection()) {
            EmployeeRegistration.mainMenu(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
