import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)

    // Get Admin_ID as input for the whole program session
    println("Enter Admin_ID:")
    val adminId = scanner.nextInt()

    while (true) {
        // Get Employee_ID as input
        println("Enter Employee_ID:")
        val employeeId = scanner.nextInt()

        // Record time entry for the given Employee_ID
        val recordedDTR = DTRProcessor.recordTimeEntry(DatabaseConnector.getConnection(), adminId, employeeId)

        if (recordedDTR != null) {
            println("Time entry recorded successfully:")
            println("Attendance_ID: ${recordedDTR.attendanceId}")
            println("Employee_ID: ${recordedDTR.employeeId}")
            println("Time_In: ${recordedDTR.timeIn}")
            println("Break_Out: ${recordedDTR.breakOut}")
            println("Break_In: ${recordedDTR.breakIn}")
            println("Time_Out: ${recordedDTR.timeOut}")
            println("NumHours: ${recordedDTR.numHours}")
        } else {
            println("Error recording time entry.")
        }

        // Ask the user if they want to continue recording time entries
        println("Do you want to record another time entry? (y/n)")
        val continueRecording = scanner.next().toLowerCase()

        if (continueRecording != "y") {
            break
        }
    }

    println("Program session ended.")
    scanner.close()
}
