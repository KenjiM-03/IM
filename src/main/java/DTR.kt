import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Timestamp

data class DTR(
    val dtrId: Int?,  // Nullable DTR_ID
    val attendanceId: Int,
    val employeeId: Int,
    val timeIn: Timestamp?,
    val breakOut: Timestamp?,
    val breakIn: Timestamp?,
    val timeOut: Timestamp?,
    val numHours: String?  // Updated return type
)

object DTRProcessor {

    fun recordTimeEntry(connection: Connection, adminId: Int, employeeId: Int): DTR? {
        val latestAttendanceId = AttendanceProcessor.getLatestAttendanceId(connection, adminId)

        if (latestAttendanceId == -1) {
            println("Error: No valid Attendance_ID found for Admin_ID $adminId")
            return null
        }

        val currentDTR = getLatestDTR(connection, latestAttendanceId, employeeId)

        val currentTime = Timestamp(System.currentTimeMillis())

        when {
            currentDTR == null -> {
                // Create a new DTR entry
                val newDTR = DTR(null, latestAttendanceId, employeeId, currentTime, null, null, null, null)
                insertDTR(connection, newDTR)
                return newDTR
            }

            currentDTR.timeIn == null -> {
                println("Error: Time_In already recorded for Employee_ID $employeeId and Attendance_ID $latestAttendanceId.")
                return null
            }

            currentDTR.breakOut == null -> {
                // Update existing DTR with Break_Out
                updateDTR(connection, currentDTR.copy(breakOut = currentTime))
                return currentDTR.copy(breakOut = currentTime)
            }

            currentDTR.breakIn == null -> {
                // Update existing DTR with Break_In
                updateDTR(connection, currentDTR.copy(breakIn = currentTime))
                return currentDTR.copy(breakIn = currentTime)
            }

            currentDTR.timeOut == null -> {
                // Update existing DTR with Time_Out
                val updatedDTR = currentDTR.copy(timeOut = currentTime)
                updateDTR(connection, updatedDTR.copy(numHours = calculateNumHours(updatedDTR)))
                return updatedDTR
            }

            else -> {
                // Employee completed all entries, create a new DTR entry
                val newDTR = DTR(null, latestAttendanceId, employeeId, currentTime, null, null, null, null)
                insertDTR(connection, newDTR)
                return newDTR
            }
        }
    }

    private fun getLatestDTR(connection: Connection, attendanceId: Int, employeeId: Int): DTR? {
        val query = "SELECT * FROM DTR WHERE Attendance_ID = ? AND Employee_ID = ? ORDER BY DTR_ID DESC LIMIT 1"

        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setInt(1, attendanceId)
            preparedStatement.setInt(2, employeeId)
            val resultSet = preparedStatement.executeQuery()

            if (resultSet.next()) {
                return DTR(
                    resultSet.getInt("DTR_ID"),
                    resultSet.getInt("Attendance_ID"),
                    resultSet.getInt("Employee_ID"),
                    resultSet.getTimestamp("Time_In"),
                    resultSet.getTimestamp("Break_Out"),
                    resultSet.getTimestamp("Break_In"),
                    resultSet.getTimestamp("Time_Out"),
                    resultSet.getString("NumHours")
                )
            }
        } catch (e: SQLException) {
            println("Error retrieving latest DTR entry: ${e.message}")
        }

        return null
    }

    private fun insertDTR(connection: Connection, dtr: DTR) {
        val query =
            "INSERT INTO DTR (Attendance_ID, Employee_ID, Time_In, Break_Out, Break_In, Time_Out, NumHours) VALUES (?, ?, ?, ?, ?, ?, ?)"

        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)
            preparedStatement.setInt(1, dtr.attendanceId)
            preparedStatement.setInt(2, dtr.employeeId)
            preparedStatement.setTimestamp(3, dtr.timeIn)
            preparedStatement.setTimestamp(4, dtr.breakOut)
            preparedStatement.setTimestamp(5, dtr.breakIn)
            preparedStatement.setTimestamp(6, dtr.timeOut)
            preparedStatement.setString(7, dtr.numHours ?: "00:00:00")  // Default to "00:00:00" if null
            preparedStatement.executeUpdate()

            val generatedKeys = preparedStatement.generatedKeys
            if (generatedKeys.next()) {
                val generatedId = generatedKeys.getInt(1)
                // Update the DTR object with the generated DTR_ID
                dtr.copy(dtrId = generatedId)
            }
        } catch (e: SQLException) {
            // Handle the duplicate entry error or any other SQL-related errors
            println("Error inserting DTR entry: ${e.message}")
        }
    }


    private fun updateDTR(connection: Connection, dtr: DTR) {
        val query =
            "UPDATE DTR SET Time_In = ?, Break_Out = ?, Break_In = ?, Time_Out = ?, NumHours = ? WHERE DTR_ID = ? AND Attendance_ID = ? AND Employee_ID = ?"

        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setTimestamp(1, dtr.timeIn)
            preparedStatement.setTimestamp(2, dtr.breakOut)
            preparedStatement.setTimestamp(3, dtr.breakIn)
            preparedStatement.setTimestamp(4, dtr.timeOut)
            preparedStatement.setString(5, dtr.numHours ?: "00:00:00")  // Default to "00:00:00" if null
            preparedStatement.setInt(6, dtr.dtrId!!)
            preparedStatement.setInt(7, dtr.attendanceId)
            preparedStatement.setInt(8, dtr.employeeId)
            preparedStatement.executeUpdate()
        } catch (e: SQLException) {
            println("Error updating DTR entry: ${e.message}")
        }
    }

    private fun calculateNumHours(dtr: DTR): String {
        // Add logging
        println("DTR_ID: ${dtr.dtrId} - Time_In: ${dtr.timeIn}, Break_Out: ${dtr.breakOut}, Break_In: ${dtr.breakIn}, Time_Out: ${dtr.timeOut}")

        // Implement your logic to calculate the duration based on Time_In, Break_Out, Break_In, Time_Out

        val timeIn = dtr.timeIn
        val breakOut = dtr.breakOut
        val breakIn = dtr.breakIn
        val timeOut = dtr.timeOut

        if (timeIn == null || timeOut == null) {
            // Cannot calculate duration without both Time_In and Time_Out
            return "00:00:00"
        }

        val workDuration: Long = if (breakOut != null && breakIn != null) {
            // If Break_Out and Break_In are both recorded, deduct break time
            val breakDuration = breakIn.time - breakOut.time
            timeOut.time - timeIn.time - breakDuration
        } else {
            // If no breaks recorded, consider total time as work duration
            timeOut.time - timeIn.time
        }

        val hours = workDuration / (1000 * 60 * 60)
        val minutes = (workDuration % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = (workDuration % (1000 * 60)) / 1000

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}
