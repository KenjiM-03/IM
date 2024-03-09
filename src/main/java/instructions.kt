import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement
import javax.swing.JComboBox
import javax.swing.JOptionPane
import javax.swing.JTextField
import kotlin.system.exitProcess
import javax.swing.JFrame
import java.awt.Component
import java.awt.Window
import javax.swing.JButton
import java.sql.PreparedStatement
import java.math.BigDecimal
import java.awt.BorderLayout
import java.sql.DriverManager
import java.sql.ResultSet
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.SwingUtilities
import javax.swing.table.DefaultTableColumnModel

//
//
// GENERAL FUNCTIONS
//
//
fun redirectToDashboard(currentWindow: JFrame) {
    val dashboardForm = DashBoard()
    dashboardForm.isVisible = true
    currentWindow.dispose()
}


//
//
// AdminSignIn.java
//
//
fun populateAdminNames(comboBox: JComboBox<String>) {
    try {
        // Assuming you already have a database connection
        val connection: Connection = DatabaseConnector.getConnection() // Replace YourDatabaseConnector.getConnection() with your actual method for getting database connection

        // SQL query to retrieve Admin_Name from the Admin table
        val sqlQuery = "SELECT Admin_Name FROM Admin"

        // Create a statement
        val statement: Statement = connection.createStatement()

        // Execute the query
        val resultSet = statement.executeQuery(sqlQuery)

        // Clear any existing items in the combo box
        comboBox.removeAllItems()

        // Iterate through the result set and add each Admin_Name to the combo box
        while (resultSet.next()) {
            val adminName = resultSet.getString("Admin_Name")
            comboBox.addItem(adminName)
        }

        // Close resources
        resultSet.close()
        statement.close()
        connection.close()
    } catch (e: SQLException) {
        e.printStackTrace()
        // Handle any SQL exceptions here
    }
}

fun handleLoginButton(dropdown: JComboBox<String>, currentForm: Component) {
    val selectedAdmin = dropdown.selectedItem as? String
    if (!selectedAdmin.isNullOrBlank()) {
        // Redirect to the Dashboard form if an admin is selected
        val dashboardForm = DashBoard()
        dashboardForm.isVisible = true
        // Close the current form if it's a Window
        if (currentForm is Window) {
            currentForm.dispose()
        }
    } else {
        // Show an error message if no admin is selected
        JOptionPane.showMessageDialog(currentForm, "Please select an admin before logging in", "Error", JOptionPane.ERROR_MESSAGE)
    }
}

//
//
// DashBoard.java
//
//

fun redirectToAdminSignIn(currentWindow: JFrame) {
    val adminSignInForm = AdminSignIn()
    adminSignInForm.isVisible = true
    currentWindow.dispose()
}

fun redirectToEmployeeCRUD(currentWindow: JFrame) {
    val employeeCRUDForm = EmployeeCRUD()
    employeeCRUDForm.isVisible = true
    currentWindow.dispose()
}

fun redirectToPiecework(currentWindow: JFrame) {
    val pieceworkForm = Piecework()
    pieceworkForm.isVisible = true
    currentWindow.dispose()
}

fun redirectToRegular(currentWindow: JFrame) {
    val regularForm = Regular()
    regularForm.isVisible = true
    currentWindow.dispose()
}

fun redirectToPayFactor(currentWindow: JFrame) {
    val payFactorForm = PayFactors()
    payFactorForm.isVisible = true
    currentWindow.dispose()
}

fun redirectToCashAdvance(currentWindow: JFrame) {
    val cashAdvanceForm = CashAdv()
    cashAdvanceForm.isVisible = true
    currentWindow.dispose()
}

fun redirectToOvertimeEligible(currentWindow: JFrame) {
    val overtimeEligibleForm = OvertimeEligible()
    overtimeEligibleForm.isVisible = true
    currentWindow.dispose()
}

fun redirectToPayslip(currentWindow: JFrame) {
    val payslipForm = Payslip()
    payslipForm.isVisible = true
    currentWindow.dispose()
}

//
//
// EmployeeCRUD.java
//
//



//
//
// Piecework.java
//
//



//
//
// Regular.java
//
//



//
//
// PayFactors.java
//
//
// Function to retrieve the 'Rate' for the 'Small' size and set it to the text of the JTextField
val TField_small = JTextField()
val TField_medium = JTextField()
val TField_large = JTextField()
fun retrieveAndSetSmallSizeRate() {
    val connection: Connection = DatabaseConnector.getConnection()

    val sqlQuery = "SELECT Rate FROM packtype WHERE Size = 'Small'"

    try {
        val statement: Statement = connection.createStatement()
        val resultSet = statement.executeQuery(sqlQuery)

        if (resultSet.next()) {
            val rateData = resultSet.getBigDecimal("Rate")
            TField_small.text = rateData.toString()
        }

        resultSet.close()
        statement.close()
        connection.close()
    } catch (e: SQLException) {
        e.printStackTrace()
    }
}

fun retrieveAndSetMediumSizeRate() {
    val connection: Connection = DatabaseConnector.getConnection()

    val sqlQuery = "SELECT Rate FROM packtype WHERE Size = 'Medium'"

    try {
        val statement: Statement = connection.createStatement()
        val resultSet = statement.executeQuery(sqlQuery)

        if (resultSet.next()) {
            val rateData = resultSet.getBigDecimal("Rate")
            TField_medium.text = rateData.toString()
        }

        resultSet.close()
        statement.close()
        connection.close()
    } catch (e: SQLException) {
        e.printStackTrace()
    }
}

fun retrieveAndSetLargeSizeRate() {
    val connection: Connection = DatabaseConnector.getConnection()

    val sqlQuery = "SELECT Rate FROM packtype WHERE Size = 'Large'"

    try {
        val statement: Statement = connection.createStatement()
        val resultSet = statement.executeQuery(sqlQuery)

        if (resultSet.next()) {
            val rateData = resultSet.getBigDecimal("Rate")
            TField_large.text = rateData.toString()
        }

        resultSet.close()
        statement.close()
        connection.close()
    } catch (e: SQLException) {
        e.printStackTrace()
    }
}

// Function to update the 'Rate' for the 'Small' size in the database
fun updateSmallSizeRateInDatabase(newRate: BigDecimal) {
    val connection: Connection = DatabaseConnector.getConnection()

    val sqlUpdate = "UPDATE packtype SET Rate = ? WHERE Size = 'Small'"

    try {
        val preparedStatement: PreparedStatement = connection.prepareStatement(sqlUpdate)
        preparedStatement.setBigDecimal(1, newRate)
        preparedStatement.executeUpdate()

        preparedStatement.close()
        connection.close()
    } catch (e: SQLException) {
        e.printStackTrace()
    }
}


//
//
// CashAdv.java
//
//



//
//
// OvertimeEligible.java
//
//



//
//
// Payslip.java
//
//



//
//
// SimulationalTimeKeeping.java
//
//