import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement
import java.util.Scanner

fun Scanner.nextDoubleOrBlank(): Double? {
    return readLine()?.takeIf { it.isNotBlank() }?.toDoubleOrNull()
}

data class PackType(val id: Int, val size: String, val description: String, val rate: Double)

object PackTypeDAO {
    fun createPackType(connection: Connection, size: String, description: String, rate: Double) {
        try {
            val insertPackTypeQuery = "INSERT INTO PackType (Size, Description, Rate) VALUES (?, ?, ?)"
            val preparedStatement = connection.prepareStatement(insertPackTypeQuery, Statement.RETURN_GENERATED_KEYS)

            preparedStatement.setString(1, size)
            preparedStatement.setString(2, description)
            preparedStatement.setDouble(3, rate)

            preparedStatement.executeUpdate()

            val generatedKeys = preparedStatement.generatedKeys
            if (generatedKeys.next()) {
                val id = generatedKeys.getInt(1)
                println("PackType with ID $id successfully created.")
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun readPackType(connection: Connection, packTypeId: Int): PackType? {
        try {
            val selectPackTypeQuery = "SELECT * FROM PackType WHERE PackType_ID = ?"
            val preparedStatement = connection.prepareStatement(selectPackTypeQuery)

            preparedStatement.setInt(1, packTypeId)

            val resultSet = preparedStatement.executeQuery()

            return if (resultSet.next()) {
                PackType(
                    resultSet.getInt("PackType_ID"),
                    resultSet.getString("Size"),
                    resultSet.getString("Description"),
                    resultSet.getDouble("Rate")
                )
            } else {
                null
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            return null
        }
    }

    fun updatePackType(connection: Connection, packTypeId: Int, newSize: String?, newDescription: String?, newRate: Double?) {
        try {
            val updatePackTypeQuery =
                "UPDATE PackType SET Size = COALESCE(?, Size), Description = COALESCE(?, Description), Rate = COALESCE(?, Rate) WHERE PackType_ID = ?"
            val preparedStatement = connection.prepareStatement(updatePackTypeQuery)

            preparedStatement.setString(1, if (newSize.isNullOrBlank()) null else newSize)
            preparedStatement.setString(2, if (newDescription.isNullOrBlank()) null else newDescription)
            preparedStatement.setDouble(3, newRate ?: 0.0)
            preparedStatement.setInt(4, packTypeId)

            val updatedRows = preparedStatement.executeUpdate()

            if (updatedRows > 0) {
                println("PackType with ID $packTypeId successfully updated.")
            } else {
                println("PackType not found.")
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun deletePackType(connection: Connection, packTypeId: Int) {
        try {
            val deletePackTypeQuery = "DELETE FROM PackType WHERE PackType_ID = ?"
            val preparedStatement = connection.prepareStatement(deletePackTypeQuery)

            preparedStatement.setInt(1, packTypeId)

            val deletedRows = preparedStatement.executeUpdate()

            if (deletedRows > 0) {
                println("PackType with ID $packTypeId successfully deleted.")
            } else {
                println("PackType not found.")
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    // Additional CRUD methods as needed...

    // Additional methods to interact with the database as needed...
}

fun main() {
    try {
        val connection = DatabaseConnector.getConnection()

        println("Choose operation:")
        println("1. Create PackType")
        println("2. Read PackType")
        println("3. Update PackType")
        println("4. Delete PackType")

        val scanner = Scanner(System.`in`)
        val choice = scanner.nextInt()

        when (choice) {
            1 -> {
                println("Enter size for PackType:")
                val size = scanner.next()

                println("Enter description for PackType:")
                val description = scanner.next()

                println("Enter rate for PackType:")
                val rate = scanner.nextDouble()

                PackTypeDAO.createPackType(connection, size, description, rate)
            }
            2 -> {
                println("Enter PackType ID to read:")
                val packTypeId = scanner.nextInt()

                PackTypeDAO.readPackType(connection, packTypeId)?.let { println(it) }
            }
            3 -> {
                println("Enter PackType ID to update:")
                val packTypeId = scanner.nextInt()

                println("Enter new size for PackType (press Enter to keep current value):")
                val newSize = readLine()?.takeIf { it.isNotBlank() }

                println("Enter new description for PackType (press Enter to keep current value):")
                val newDescription = readLine()?.takeIf { it.isNotBlank() }

                println("Enter new rate for PackType (press Enter to keep current value):")
                val newRate = scanner.nextDoubleOrBlank()


                PackTypeDAO.updatePackType(connection, packTypeId, newSize, newDescription, newRate)
            }
            4 -> {
                println("Enter PackType ID to delete:")
                val packTypeId = scanner.nextInt()

                PackTypeDAO.deletePackType(connection, packTypeId)
            }
            else -> println("Invalid choice.")
        }

    } catch (e: SQLException) {
        e.printStackTrace()
    }
}
