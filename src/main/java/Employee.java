import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Employee {

    private int employeeId;
    private String employeeName;
    private String contactNumber;
    private String gender;
    private String dateOfBirth;
    private String jobTypeDescription;

    public Employee(int employeeId, String employeeName, String contactNumber, String gender, String dateOfBirth, String jobTypeDescription) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.contactNumber = contactNumber;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.jobTypeDescription = jobTypeDescription;
    }

    public int getEmployeeId() {
        return this.employeeId;
    }

    public String getEmployeeName() {
        return this.employeeName;
    }

    public String getContactNumber() {
        return this.contactNumber;
    }

    public String getGender() {
        return this.gender;
    }

    public String getDateOfBirth() {
        return this.dateOfBirth;
    }

    public String getJobTypeDescription() {
        return this.jobTypeDescription;
    }

    public static void insertEmployee(Connection connection, String employeeName, String contactNumber, String gender, String dateOfBirth, String jobTypeDescription) {
        try {
            String insertEmployeeQuery = "INSERT INTO Employees (Employee_Name, Contact_Number, Gender, Date_of_Birth) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertEmployeeQuery, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, employeeName);
                preparedStatement.setString(2, contactNumber);
                preparedStatement.setString(3, gender);
                preparedStatement.setString(4, dateOfBirth);
                preparedStatement.executeUpdate();

                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int lastEmployeeId = generatedKeys.getInt(1);
                    insertJobType(connection, lastEmployeeId, jobTypeDescription);
                    System.out.println("Employee successfully inserted.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateEmployee(Connection connection, int employeeId, String employeeName, String contactNumber, String gender, String dateOfBirth, String jobTypeDescription) {
        try {
            String updateEmployeeQuery = "UPDATE Employees SET Employee_Name=?, Contact_Number=?, Gender=?, Date_of_Birth=? WHERE Employee_ID=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateEmployeeQuery)) {
                preparedStatement.setString(1, employeeName);
                preparedStatement.setString(2, contactNumber);
                preparedStatement.setString(3, gender);
                preparedStatement.setString(4, dateOfBirth);
                preparedStatement.setInt(5, employeeId);
                preparedStatement.executeUpdate();

                updateJobType(connection, employeeId, jobTypeDescription);
                System.out.println("Employee successfully updated.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteEmployee(Connection connection, int employeeId) {
        try {

            if (isTableExists(connection, "Piecework")) {
                deleteJobType(connection, employeeId, "Piecework");
            }

            String deleteEmployeeQuery = "DELETE FROM Employees WHERE Employee_ID=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteEmployeeQuery)) {
                preparedStatement.setInt(1, employeeId);
                preparedStatement.executeUpdate();

                System.out.println("Employee successfully deleted.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Employee getEmployeeById(Connection connection, int employeeId) {
        try {
            String selectEmployeeQuery = "SELECT * FROM Employees WHERE Employee_ID=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectEmployeeQuery)) {
                preparedStatement.setInt(1, employeeId);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String employeeName = resultSet.getString("Employee_Name");
                    String contactNumber = resultSet.getString("Contact_Number");
                    String gender = resultSet.getString("Gender");
                    String dateOfBirth = resultSet.getString("Date_of_Birth");
                    String jobTypeDescription = getJobTypeDescription(connection, employeeId);

                    return new Employee(employeeId, employeeName, contactNumber, gender, dateOfBirth, jobTypeDescription);
                } else {
                    System.out.println("Employee not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static void insertJobType(Connection connection, int employeeId, String jobTypeDescription) {
        try {
            String jobTypeTable;
            switch (jobTypeDescription) {
                case "regular":
                    jobTypeTable = "Regular";
                    break;
                case "piecework":
                    jobTypeTable = "Piecework";
                    break;
                case "admin":
                    jobTypeTable = "Admin";
                    break;
                default:
                    throw new IllegalArgumentException("Invalid Job Type: " + jobTypeDescription);
            }

            String insertJobTypeQuery = "INSERT INTO " + jobTypeTable + " (Employee_ID, Job_Type_Description) VALUES (?, ?)";
            try (PreparedStatement jobTypePreparedStatement = connection.prepareStatement(insertJobTypeQuery)) {
                jobTypePreparedStatement.setInt(1, employeeId);
                jobTypePreparedStatement.setString(2, jobTypeDescription);
                jobTypePreparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateJobType(Connection connection, int employeeId, String jobTypeDescription) throws SQLException {
        deleteJobType(connection, employeeId, "Regular");
        insertJobType(connection, employeeId, jobTypeDescription);
    }

    private static void deleteJobType(Connection connection, int employeeId, String jobTypeTable) throws SQLException {
        String deleteJobTypeQuery = "DELETE FROM " + jobTypeTable + " WHERE Employee_ID=?";
        try (PreparedStatement jobTypePreparedStatement = connection.prepareStatement(deleteJobTypeQuery)) {
            jobTypePreparedStatement.setInt(1, employeeId);
            jobTypePreparedStatement.executeUpdate();
        }
    }

    private static String getJobTypeDescription(Connection connection, int employeeId) throws SQLException {
        String jobTypeQuery = "SELECT Job_Type_Description FROM ";

        // Assuming your tables are named Regular and Piecework
        if (isTableExists(connection, "Regular")) {
            jobTypeQuery += "Regular";
        } else if (isTableExists(connection, "Piecework")) {
            jobTypeQuery += "Piecework";
        } else {
            // If neither table exists, return an empty string or handle it as needed
            return "";
        }

        jobTypeQuery += " WHERE Employee_ID=?";

        try (PreparedStatement jobTypePreparedStatement = connection.prepareStatement(jobTypeQuery)) {
            jobTypePreparedStatement.setInt(1, employeeId);
            ResultSet resultSet = jobTypePreparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("Job_Type_Description");
            } else {
                return "";
            }
        }
    }


    private static boolean isTableExists(Connection connection, String tableName) throws SQLException {
        try (ResultSet tables = connection.getMetaData().getTables(null, null, tableName, null)) {
            return tables.next();
        }
    }
}
