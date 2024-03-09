import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.*

data class Attendance(val attendanceId: Int, val date: Date, val adminId: Int)

object AttendanceProcessor {

    @Throws(SQLException::class)
    fun getLatestAttendanceId(connection: Connection, adminId: Int): Int {
        val query = "SELECT Attendance_ID FROM Attendance WHERE Admin_ID = ? ORDER BY Date DESC LIMIT 1"

        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setInt(1, adminId)
            val resultSet = preparedStatement.executeQuery()

            if (resultSet.next()) {
                val attendanceId = resultSet.getInt("Attendance_ID")
                resultSet.close()
                preparedStatement.close()
                return attendanceId
            }

            resultSet.close()
            preparedStatement.close()
        } catch (e: SQLException) {
            println("Error retrieving Attendance_ID: ${e.message}")
            // Handle exception as needed (e.g., throw, log)
        }

        return -1 // Return -1 if no valid Attendance_ID is found
    }
}
