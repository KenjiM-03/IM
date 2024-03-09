import java.sql.*
import java.sql.Connection


fun savePieceworkTransaction(
    employeeId: Int,
    packTypeId: Int,
    quantity: Int,
    currentDate: java.sql.Date,
    adminId: Int,
    connection: Connection // Pass the DatabaseConnector instance
) {

    try {
        // Step 1: Insert into Transaction table and get generated Transaction_ID
        val transactionInsertStmt = connection.prepareStatement(
            "INSERT INTO Transaction (Date, Admin_ID) VALUES (?, ?)",
            Statement.RETURN_GENERATED_KEYS
        )
        try {
            transactionInsertStmt.setDate(1, currentDate)
            transactionInsertStmt.setInt(2, adminId)
            transactionInsertStmt.executeUpdate()

            // Step 2: Retrieve the auto-generated Transaction_ID
            val generatedKeys = transactionInsertStmt.generatedKeys
            if (generatedKeys.next()) {
                val transactionId = generatedKeys.getInt(1)

                // Step 3: Insert into Piecework_Details using the retrieved Transaction_ID
                val pieceworkDetailsStmt = connection.prepareStatement(
                    "INSERT INTO Piecework_Details (Transaction_ID, Employee_ID, PackType_ID, Quantity) VALUES (?, ?, ?, ?)"
                )
                try {
                    pieceworkDetailsStmt.setInt(1, transactionId)
                    pieceworkDetailsStmt.setInt(2, employeeId)
                    pieceworkDetailsStmt.setInt(3, packTypeId)
                    pieceworkDetailsStmt.setInt(4, quantity)
                    pieceworkDetailsStmt.executeUpdate()
                } finally {
                    pieceworkDetailsStmt.close() // Close the PreparedStatement
                }
            } else {
                throw SQLException("Inserting transaction failed, no ID obtained.")
            }
        } finally {
            transactionInsertStmt.close()
        }
        connection.commit() // Commit the transaction
    } catch (ex: SQLException) {
        connection.rollback() // Rollback the transaction on error
        throw ex // Rethrow the exception to handle it at a higher level
    } finally {
        connection.autoCommit = true // Restore default behavior
        connection.close() // Close the connection
    }
}

fun main() {
    try {
        val connection = DatabaseConnector.getConnection()
        connection.autoCommit = false // Set autocommit to false

        val employeeId = 2
        val packTypeId = 3
        val quantity = 29
        val currentDate = java.sql.Date(System.currentTimeMillis())
        val adminId = 1 // Replace with the actual admin ID

        savePieceworkTransaction(employeeId, packTypeId, quantity, currentDate, adminId, connection)

        connection.commit() // Commit the transaction explicitly

        println("Transaction saved successfully.")
    } catch (ex: SQLException) {
        println("Error saving transaction: ${ex.message}")
    }
} 


