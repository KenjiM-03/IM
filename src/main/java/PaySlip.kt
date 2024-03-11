import Employee.getEmployeeById
import java.sql.Connection
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.*

data class PieceworkDetails(
    val transactionId: Int,
    val employeeId: Int,
    val packTypeId: Int,
    val quantity: Int
)

data class PaySlip(
    val employeeId: Int,
    val employeeName: String,
    val currentDate: String,
    val totalNumHoursOrQuantity: String,
    val salary: Double
)

object PaySlipGenerator {

    fun generatePaySlip(connection: Connection, employeeId: Int): PaySlip? {
        val employee = getEmployeeById(connection, employeeId)

        if (employee != null) {
            val currentDate = getCurrentDate()

            if (isRegularEmployee(connection, employeeId)) {
                val totalNumHours = getTotalNumHours(connection, employeeId)
                val salary = calculateSalary(totalNumHours)
                return PaySlip(employeeId, employee.employeeName, currentDate, totalNumHours, salary)
            } else if (isPieceworkEmployee(connection, employeeId)) {
                val pieceworkDetails = getPieceworkDetails(connection, employeeId)
                val totalQuantity = calculateTotalQuantity(pieceworkDetails)
                val totalSalary = calculatePieceworkSalary(pieceworkDetails, connection)
                return PaySlip(employeeId, employee.employeeName, currentDate, totalQuantity.toString(), totalSalary)
            } else {
                println("Employee with Employee_ID $employeeId is not a regular or piecework employee.")
            }
        } else {
            println("Employee with Employee_ID $employeeId not found.")
        }

        return null
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    private fun getTotalNumHours(connection: Connection, employeeId: Int): String {
        val query = """
            SELECT SEC_TO_TIME(SUM(TIME_TO_SEC(NumHours))) AS TotalNumTime
            FROM DTR
            WHERE Employee_ID = ? AND DAYOFWEEK(Time_In) BETWEEN 2 AND 7
        """.trimIndent()

        try {
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setInt(1, employeeId)

            val resultSet = preparedStatement.executeQuery()

            if (resultSet.next()) {
                return resultSet.getString("TotalNumTime") ?: "00:00:00"
            }
        } catch (e: SQLException) {
            println("Error retrieving total numHours: ${e.message}")
        }

        return "00:00:00"
    }

    private fun calculateSalary(totalNumHours: String): Double {
        val numHours = totalNumHours.split(":")
        val hours = numHours[0].toDouble()
        val minutes = numHours[1].toDouble()
        val seconds = numHours[2].toDouble()

        val salaryPerHour = 56.25
        val salaryPerMinute = 0.9375
        val salaryPerSecond = 0.015625

        val totalSalary = (hours * salaryPerHour) + (minutes * salaryPerMinute) + (seconds * salaryPerSecond)

        return String.format("%.2f", totalSalary).toDouble()
    }

    // Add a function to check if the employee is a regular employee
    fun isRegularEmployee(connection: Connection, employeeId: Int): Boolean {
        val query = "SELECT * FROM Regular WHERE Employee_ID = ?"

        try {
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setInt(1, employeeId)

            val resultSet = preparedStatement.executeQuery()
            return resultSet.next()
        } catch (e: SQLException) {
            println("Error checking if employee is regular: ${e.message}")
        }

        return false
    }

    // Add a function to check if the employee is a piecework employee
    private fun isPieceworkEmployee(connection: Connection, employeeId: Int): Boolean {
        val query = "SELECT * FROM Piecework WHERE Employee_ID = ?"

        try {
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setInt(1, employeeId)

            val resultSet = preparedStatement.executeQuery()
            return resultSet.next()
        } catch (e: SQLException) {
            println("Error checking if employee is piecework: ${e.message}")
        }

        return false
    }

    fun getPieceworkDetails(connection: Connection, employeeId: Int): List<PieceworkDetails> {
        val query = "SELECT * FROM Piecework_Details WHERE Employee_ID = ?"

        try {
            val preparedStatement = connection.prepareStatement(query)
            preparedStatement.setInt(1, employeeId)

            val resultSet = preparedStatement.executeQuery()

            val pieceworkDetailsList = mutableListOf<PieceworkDetails>()
            while (resultSet.next()) {
                val transactionId = resultSet.getInt("Transaction_ID")
                val packTypeId = resultSet.getInt("PackType_ID")
                val quantity = resultSet.getInt("Quantity")

                pieceworkDetailsList.add(PieceworkDetails(transactionId, employeeId, packTypeId, quantity))
            }

            return pieceworkDetailsList
        } catch (e: SQLException) {
            println("Error retrieving piecework details: ${e.message}")
        }

        return emptyList()
    }

    private fun calculateTotalQuantity(pieceworkDetails: List<PieceworkDetails>): Int {
        return pieceworkDetails.sumBy { it.quantity }
    }

    fun calculatePieceworkSalary(pieceworkDetails: List<PieceworkDetails>, connection: Connection): Double {
        val rates = getPieceworkRates(connection)

        var totalSalary = 0.0
        for (detail in pieceworkDetails) {
            val packTypeRate = rates[detail.packTypeId] ?: 0.0
            totalSalary += detail.quantity * packTypeRate
        }

        return String.format("%.2f", totalSalary).toDouble()
    }

    fun getPieceworkRates(connection: Connection): Map<Int, Double> {
        val query = "SELECT PackType_ID, Rate FROM PackType"

        try {
            val preparedStatement = connection.prepareStatement(query)
            val resultSet = preparedStatement.executeQuery()

            val pieceworkRates = mutableMapOf<Int, Double>()
            while (resultSet.next()) {
                val packTypeId = resultSet.getInt("PackType_ID")
                val rate = resultSet.getDouble("Rate")
                pieceworkRates[packTypeId] = rate
            }

            return pieceworkRates
        } catch (e: SQLException) {
            println("Error retrieving piecework rates: ${e.message}")
        }

        return emptyMap()
    }
}

fun main() {
    val connection = DatabaseConnector.getConnection()

    print("Enter Employee_ID: ")
    val employeeId = readLine()?.toInt() ?: -1

    val paySlip = PaySlipGenerator.generatePaySlip(connection, employeeId)

    if (paySlip != null) {
        println("Employee_ID: ${paySlip.employeeId}")
        println("Employee Name: ${paySlip.employeeName}")
        println("Current Date: ${paySlip.currentDate}")

        if (PaySlipGenerator.isRegularEmployee(connection, employeeId)) {
            println("TotalNumHours: ${paySlip.totalNumHoursOrQuantity}")
            println("Salary: ${paySlip.salary} pesos")
        } else {
            println("Total quantity for")

            val pieceworkDetails = PaySlipGenerator.getPieceworkDetails(connection, employeeId)
            val packTypeRates = PaySlipGenerator.getPieceworkRates(connection)

            for (detail in pieceworkDetails) {
                val packTypeRate = packTypeRates[detail.packTypeId] ?: 0.0
                val totalAmount = detail.quantity * packTypeRate

                val sizeDescription = getSizeDescription(connection, detail.packTypeId)
                println("$sizeDescription: ${detail.quantity} * $packTypeRate = $totalAmount pesos")
            }

            val totalSalary = PaySlipGenerator.calculatePieceworkSalary(pieceworkDetails, connection)
            println("Total Salary: ${String.format("%.2f", totalSalary)} pesos")
        }
    } else {
        println("Error generating PaySlip.")
    }

    // Close the database connection
    connection.close()
}

// Function to get Size Description based on PackType_ID
private fun getSizeDescription(connection: Connection, packTypeId: Int): String {
    val query = "SELECT Size FROM PackType WHERE PackType_ID = ?"

    try {
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setInt(1, packTypeId)

        val resultSet = preparedStatement.executeQuery()

        if (resultSet.next()) {
            return resultSet.getString("Size") ?: ""
        }
    } catch (e: SQLException) {
        println("Error retrieving Size: ${e.message}")
    }

    return ""
}
