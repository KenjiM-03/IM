


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

import java.awt.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;


import java.sql.Date;
import java.time.temporal.TemporalAdjusters;
import javax.swing.JOptionPane;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;


public class Payslip extends javax.swing.JFrame {

    private DefaultTableModel tableModel;

    public Payslip() {
        initComponents();
        initTableModel();
        initButtonListeners();
    }

    private void initTableModel() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disallow editing in the table
            }
        };
        tableModel.setColumnIdentifiers(new String[]{"Employee ID", "Employee Name", "Job Type"});
        jTable1.setModel(tableModel);
    }

    private void initButtonListeners() {


        Button_add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEmployee();
            }
        });

        Button_delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteEmployee();
            }
        });

        Button_Print.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printEmployee();
            }
        });

        Button_PrintAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printAllEmployees();
            }
        });

        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        // Add action listener for other buttons as needed

        Button_AllRE2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayAllEmployees();
            }
        });
        Button_deleteall.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteAllEmployees();
            }
        });


    }


//    private void displayRegularEmployees() {
//        try (Connection connection = DatabaseConnector.getConnection();
//             PreparedStatement statement = connection.prepareStatement("SELECT e.Employee_ID, e.Employee_Name, r.Job_Type_Description "
//                     + "FROM Employees e INNER JOIN Regular r ON e.Employee_ID = r.Employee_ID");
//             ResultSet resultSet = statement.executeQuery()) {
//
//            displayEmployees(resultSet);
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "Error fetching regular employees: " + ex.getMessage());
//        }
//    }

    private void displayPieceworkEmployees() {
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT p.Employee_ID, e.Employee_Name, p.Job_Type_Description "
                     + "FROM Piecework p INNER JOIN Employees e ON p.Employee_ID = e.Employee_ID");
             ResultSet resultSet = statement.executeQuery()) {

            displayEmployees(resultSet);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching piecework employees: " + ex.getMessage());
        }
    }

    private void displayAllEmployees() {
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT DISTINCT e.Employee_ID, e.Employee_Name, 'Piecework' AS Job_Type_Description " +
                             "FROM Employees e " +
                             "JOIN Piecework_Details pd ON e.Employee_ID = pd.Employee_ID")) {
            ResultSet resultSet = statement.executeQuery();
            displayEmployees(resultSet);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching all employees with piecework records: " + ex.getMessage());
        }
    }



    private void displayEmployees(ResultSet resultSet) throws SQLException {
        tableModel.setRowCount(0);
        while (resultSet.next()) {
            String employeeID = resultSet.getString("Employee_ID");
            String employeeName = resultSet.getString("Employee_Name");
            String jobType = resultSet.getString("Job_Type_Description");
            tableModel.addRow(new Object[]{employeeID, employeeName, jobType});
        }
    }

    private void addEmployee() {
        // Get the employee ID from the text field
        String employeeIDText = TField_EmployeeID.getText();

        // Check if the employee ID text field is empty
        if (employeeIDText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an employee ID.");
            return;
        }

        // Convert the employee ID from string to integer
        int employeeID;
        try {
            employeeID = Integer.parseInt(employeeIDText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid employee ID. Please enter a valid integer ID.");
            return;
        }

        // Fetch employee details from the database based on the entered ID
        try (Connection connection = DatabaseConnector.getConnection()) {
            String query = "SELECT * FROM Employees WHERE Employee_ID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, employeeID);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // Employee exists, add details to the table
                        String employeeName = resultSet.getString("Employee_Name");
                        String jobType = getJobType(connection, employeeID);
                        Object[] rowData = new Object[]{employeeID, employeeName, jobType};
                        tableModel.addRow(rowData);
                    } else {
                        // Employee does not exist
                        JOptionPane.showMessageDialog(this, "Employee with ID " + employeeID + " does not exist.");
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching employee details: " + e.getMessage());
        }
    }

    private String getJobType(Connection connection, int employeeID) throws SQLException {
        String query = "SELECT * FROM Piecework WHERE Employee_ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, employeeID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return "Piecework";
                }
            }
        }
        return "Unknown";
    }




    private void deleteEmployee() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete.");
            return;
        }

        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this employee from the table?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Employee deleted from the table.");
        }
    }

    private void deleteAllEmployees() {
        int rowCount = tableModel.getRowCount();
        if (rowCount == 0) {
            JOptionPane.showMessageDialog(this, "No employees to delete.");
            return;
        }

        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete all employees from the table?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            tableModel.setRowCount(0); // Remove all rows from the table
            JOptionPane.showMessageDialog(this, "All employees deleted from the table.");
        }
    }


    // Helper method to generate a unique employee ID for demonstration purposes
    private int getNextEmployeeID() {
        int rowCount = tableModel.getRowCount();
        return rowCount + 1; // Simply increment the row count for demonstration, in practice, you'd use a more robust method to generate unique IDs
    }

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {
        // Get the selected row index
        int rowIndex = jTable1.getSelectedRow();

        // Check if a row is selected
        if (rowIndex != -1) {
            // Get the employee ID from the selected row (assuming it's stored as a String)
            String employeeIDString = jTable1.getValueAt(rowIndex, 0).toString();

            // Parse the employee ID string to an integer
            int employeeID = Integer.parseInt(employeeIDString);

            // Fetch employee details and calculate payslip
            String employeeName = jTable1.getValueAt(rowIndex, 1).toString();
            String jobType = jTable1.getValueAt(rowIndex, 2).toString();
            double[] payslipDetails = calculatePayslip(employeeID);

            // Generate payslip preview
            String payslipPreview = generatePayslipPreview(employeeID, employeeName, jobType, payslipDetails);

            // Display payslip preview in JTextArea
            jTextArea1.setText(payslipPreview);
        }
    }

    private void printEmployee() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to print payslip.");
            return;
        }

        String employeeID = jTable1.getValueAt(selectedRow, 0).toString();
        String employeeName = jTable1.getValueAt(selectedRow, 1).toString();
        String jobType = jTable1.getValueAt(selectedRow, 2).toString();
        double[] payslipDetails = calculatePayslip(Integer.parseInt(employeeID));
        String payslipPreview = generatePayslipPreview(Integer.parseInt(employeeID), employeeName, jobType, payslipDetails);

        // Print the payslip as a PDF
        try {
            // Generate unique file name based on employee ID, current date, and current time
            String fileName = generateFileName(employeeID);
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // Add content to the PDF
            document.add(new Paragraph(payslipPreview));

            document.close();
            JOptionPane.showMessageDialog(this, "Payslip printed successfully. File saved as " + fileName);

            // Reset employee data after printing payslip
            resetEmployeeData(Integer.parseInt(employeeID)); // Call resetEmployeeData method here

        } catch (FileNotFoundException | DocumentException e) {
            JOptionPane.showMessageDialog(this, "Error printing payslip: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printAllEmployees() {
        // Iterate through each row of the jTable
        for (int row = 0; row < jTable1.getRowCount(); row++) {
            String employeeID = jTable1.getValueAt(row, 0).toString();
            String employeeName = jTable1.getValueAt(row, 1).toString();
            String jobType = jTable1.getValueAt(row, 2).toString();
            double[] payslipDetails = calculatePayslip(Integer.parseInt(employeeID));
            String payslipPreview = generatePayslipPreview(Integer.parseInt(employeeID), employeeName, jobType, payslipDetails);

            // Print the payslip as a PDF
            try {
                // Generate unique file name based on employee ID, current date, and current time
                String fileName = generateFileName(employeeID);
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(fileName));
                document.open();

                // Add content to the PDF
                document.add(new Paragraph(payslipPreview));

                document.close();
                JOptionPane.showMessageDialog(this, "Payslip printed successfully. File saved as " + fileName);

                // Reset employee data after printing payslip
                resetEmployeeData(Integer.parseInt(employeeID)); // Call resetEmployeeData method here

            } catch (FileNotFoundException | DocumentException e) {
                JOptionPane.showMessageDialog(this, "Error printing payslip: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String generateFileName(String employeeID) {
        String currentDate = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
        String currentTime = new SimpleDateFormat("HH:mm").format(new java.util.Date());
        return employeeID + "-" + currentDate + "-" + currentTime + ".pdf";
    }

    private void resetEmployeeData(int employeeID) {
        try (Connection connection = DatabaseConnector.getConnection()) {
            // Delete records for small, medium, and large sizes in Piecework_Details
            String deleteQuery = "DELETE FROM Piecework_Details WHERE Employee_ID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setInt(1, employeeID);
                preparedStatement.executeUpdate();
            }

            // Delete cash advance record
            deleteQuery = "DELETE FROM Cash_Advance WHERE Employee_ID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setInt(1, employeeID);
                preparedStatement.executeUpdate();
            }

            // Delete deductions associated with the employee's payslips
            deleteQuery = "DELETE FROM Deduction WHERE Deduction_ID IN (SELECT Deduction_ID FROM Employee_Deductions WHERE PaySlip_ID IN " +
                    "(SELECT PaySlip_ID FROM PaySlip WHERE Transaction_ID IN " +
                    "(SELECT Transaction_ID FROM Piecework_Details WHERE Employee_ID = ?)))";

            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setInt(1, employeeID);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error resetting employee data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }









    private double[] calculatePayslip(int employeeID) {
        double[] payslipDetails = new double[11]; // Increased the size to accommodate gross pay, deductions, and cash advances
        // Query the packtype table to get the rate for each size
        double rateSmall = getRateFromPackType("Small");
        double rateMedium = getRateFromPackType("Medium");
        double rateLarge = getRateFromPackType("Large");

        // Query the piecework_details table to get the quantities for each size for the current week
        int quantitySmall = getQuantityForSize(employeeID, "Small");
        int quantityMedium = getQuantityForSize(employeeID, "Medium");
        int quantityLarge = getQuantityForSize(employeeID, "Large");

        // Calculate total pay for each size based on rate and quantity
        double totalSmall = rateSmall * quantitySmall;
        double totalMedium = rateMedium * quantityMedium;
        double totalLarge = rateLarge * quantityLarge;

        // Store the calculated totals in the payslipDetails array
        payslipDetails[0] = totalSmall;
        payslipDetails[1] = totalMedium;
        payslipDetails[2] = totalLarge;

        // Calculate the gross pay
        double grossPay = totalSmall + totalMedium + totalLarge;
        payslipDetails[3] = grossPay;

        // Assuming you're fetching the deduction rates and calculating them...
        Map<String, Double> deductionRates = fetchDeductionRates();
        double pagIbig = deductionRates.getOrDefault("Pag-Ibig", 0.0);
        double philhealth = deductionRates.getOrDefault("Philhealth", 0.0);
        double sss = deductionRates.getOrDefault("SSS", 0.0);
        double cashAdvanceTotal = getCashAdvanceTotal(employeeID); // Corrected method call

        payslipDetails[4] = pagIbig;
        payslipDetails[5] = philhealth;
        payslipDetails[6] = sss;
        payslipDetails[9] = cashAdvanceTotal; // Moved the cash advance total to the correct index

        // Calculate total deductions and net salary
        double totalDeductions = pagIbig + philhealth + sss + cashAdvanceTotal;
        payslipDetails[7] = totalDeductions;
        double netSalary = grossPay - totalDeductions;
        payslipDetails[8] = netSalary;

        return payslipDetails;
    }

    private double getCashAdvanceTotal(int employeeID) {
        double totalCashAdvance = 0.0;
        try (Connection connection = DatabaseConnector.getConnection()) {
            String query = "SELECT SUM(Amount) AS TotalAmount FROM Cash_Advance WHERE Employee_ID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, employeeID);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        totalCashAdvance = resultSet.getDouble("TotalAmount");
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching cash advance total: " + e.getMessage());
        }
        return totalCashAdvance;
    }





    private String generatePayslipPreview(int employeeID, String employeeName, String jobType, double[] payslipDetails) {
        StringBuilder payslipPreview = new StringBuilder();

        // Current date handling remains the same
        long currentTimeMillis = System.currentTimeMillis();
        Date currentDate = new Date(currentTimeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(currentDate);

        // Building the payslip preview
        payslipPreview.append("No. ").append(employeeID).append("\n");
        payslipPreview.append("Date: ").append(formattedDate).append("\n");
        payslipPreview.append("Name: ").append(employeeName).append("\n");
        payslipPreview.append("Job Type: ").append(jobType).append("\n\n");
        payslipPreview.append("\t\tPAY SLIP\n\n");

        payslipPreview.append("Small\t\t\t").append(String.format("%.2f", payslipDetails[0])).append("\n");
        payslipPreview.append("Medium\t\t\t").append(String.format("%.2f", payslipDetails[1])).append("\n");
        payslipPreview.append("Large\t\t\t").append(String.format("%.2f", payslipDetails[2])).append("\n");
        payslipPreview.append(" _______________________________________________________\n");
        payslipPreview.append("Gross Pay\t\t\t").append(String.format("%.2f", payslipDetails[3])).append("\n");
        payslipPreview.append(" _______________________________________________________\n\n");

        payslipPreview.append("Deductions\n");
        payslipPreview.append("Pag-Ibig\t\t\t").append(String.format("%.2f", payslipDetails[4])).append("\n");
        payslipPreview.append("Philhealth\t\t\t").append(String.format("%.2f", payslipDetails[5])).append("\n");
        payslipPreview.append("SSS\t\t\t").append(String.format("%.2f", payslipDetails[6])).append("\n\n");
        payslipPreview.append("Cash Advance\t\t\t").append(String.format("%.2f", payslipDetails[9])).append("\n");
        payslipPreview.append(" _______________________________________________________\n");
        payslipPreview.append("Total Deduction\t\t").append(String.format("%.2f", payslipDetails[7])).append("\n");
        payslipPreview.append(" _______________________________________________________\n\n");
        payslipPreview.append("Net Salary\t\t\t").append(String.format("%.2f", payslipDetails[8])).append("\n");

        return payslipPreview.toString();
    }



    private double getRateFromPackType(String size) {
        double rate = 0.0;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnector.getConnection();
            String query = "SELECT Rate FROM packtype WHERE Size = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, size);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                rate = resultSet.getDouble("Rate");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return rate;
    }

    private int getQuantityForSize(int employeeID, String size) {
        int quantity = 0;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        // Get the current date
        LocalDate currentDate = LocalDate.now();
        LocalDate startDateOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endDateOfWeek = startDateOfWeek.plusDays(6);

        try {
            connection = DatabaseConnector.getConnection();
            String query = "SELECT SUM(pd.Quantity) AS TotalQuantity " +
                    "FROM Piecework_Details pd " +
                    "JOIN Transaction t ON pd.Transaction_ID = t.Transaction_ID " +
                    "JOIN PackType pt ON pd.PackType_ID = pt.PackType_ID " +
                    "WHERE pd.Employee_ID = ? AND pt.Size = ? " +
                    "AND t.Date BETWEEN ? AND ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, employeeID);
            statement.setString(2, size);
            statement.setDate(3, Date.valueOf(startDateOfWeek));
            statement.setDate(4, Date.valueOf(endDateOfWeek));
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                quantity = resultSet.getInt("TotalQuantity");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return quantity;
    }



    private Map<String, Double> fetchDeductionRates() {
        Map<String, Double> rates = new HashMap<>();
        String query = "SELECT Deduction_Type, Amount FROM Deduction";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String type = rs.getString("Deduction_Type");
                double amount = rs.getDouble("Amount");
                rates.put(type, amount);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching deduction rates.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return rates;
    }


    // Inside the Payslip class

//    private void printEmployee() {
//        int selectedRow = jTable1.getSelectedRow();
//        if (selectedRow == -1) {
//            JOptionPane.showMessageDialog(this, "Please select an employee to print payslip.");
//            return;
//        }
//
//        String employeeID = jTable1.getValueAt(selectedRow, 0).toString();
//        // Add logic to print payslip for the selected employee
//        String payslipPreview = jTextArea1.getText();
//        createPdfPayslip(employeeID, payslipPreview);
//    }
//
//    private void createPdfPayslip(String employeeID, String payslipContent) {
//        Document document = new Document();
//        try {
//            // Modify this path as needed to save the PDF in a specific location
//            PdfWriter.getInstance(document, new FileOutputStream("Payslip_" + employeeID + ".pdf"));
//            document.open();
//            document.add(new Paragraph(payslipContent));
//            JOptionPane.showMessageDialog(this, "Payslip PDF created successfully.");
//        } catch (Exception e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Error creating PDF: " + e.getMessage(), "PDF Creation Error", JOptionPane.ERROR_MESSAGE);
//        } finally {
//            document.close();
//        }
//    }




    // Main method is not required here as it's already implemented in the auto-generated code

    // Variables declaration - do not modify                     
    // Your variable declarations here
    // End of variables declaration                   



    // End of variables declaration                   

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    // Generated using JFormDesigner Evaluation license - Zevoex
    private void initComponents() {
        Button_back = new JButton();
        jPanel1 = new JPanel();
        Button_AllRE2 = new JButton();
        jLabel1 = new JLabel();
        TField_EmployeeID = new JTextField();
        Button_add = new JButton();
        jPanel2 = new JPanel();
        jScrollPane1 = new JScrollPane();
        jTable1 = new JTable();
        jPanel3 = new JPanel();
        Button_PrintAll = new JButton();
        Button_Print = new JButton();
        jScrollPane2 = new JScrollPane();
        jTextArea1 = new JTextArea();
        Button_delete = new JButton();
        Button_deleteall = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var contentPane = getContentPane();

        //---- Button_back ----
        Button_back.setText("<Back");
        Button_back.addActionListener(e -> Button_backActionPerformed(e));

        //======== jPanel1 ========
        {
            jPanel1.setBackground(new Color(0x333333));
            jPanel1.setBorder ( new javax . swing. border .CompoundBorder ( new javax . swing. border .TitledBorder ( new
            javax . swing. border .EmptyBorder ( 0, 0 ,0 , 0) ,  "JF\u006frmDes\u0069gner \u0045valua\u0074ion" , javax
            . swing .border . TitledBorder. CENTER ,javax . swing. border .TitledBorder . BOTTOM, new java
            . awt .Font ( "D\u0069alog", java .awt . Font. BOLD ,12 ) ,java . awt
            . Color .red ) ,jPanel1. getBorder () ) ); jPanel1. addPropertyChangeListener( new java. beans .
            PropertyChangeListener ( ){ @Override public void propertyChange (java . beans. PropertyChangeEvent e) { if( "\u0062order" .
            equals ( e. getPropertyName () ) )throw new RuntimeException( ) ;} } );

            //---- Button_AllRE2 ----
            Button_AllRE2.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            Button_AllRE2.setText("All Employees");

            //---- jLabel1 ----
            jLabel1.setBackground(new Color(0x99ffff));
            jLabel1.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            jLabel1.setText("Employee ID:");

            //---- TField_EmployeeID ----
            TField_EmployeeID.setFont(new Font("Segoe UI", Font.PLAIN, 16));

            //---- Button_add ----
            Button_add.setText("Add");

            //======== jPanel2 ========
            {
                jPanel2.setBackground(new Color(0x003333));

                //======== jScrollPane1 ========
                {

                    //---- jTable1 ----
                    jTable1.setModel(new DefaultTableModel(
                        new Object[][] {
                        },
                        new String[] {
                            "Employee ID", "Employee Name", "Job Type", "Gross Pay", "Total Deduction", "Net Salary"
                        }
                    ));
                    jScrollPane1.setViewportView(jTable1);
                }

                //======== jPanel3 ========
                {
                    jPanel3.setBackground(new Color(0x666666));
                    jPanel3.setForeground(new Color(0x333333));

                    //---- Button_PrintAll ----
                    Button_PrintAll.setFont(new Font("Segoe UI", Font.BOLD, 36));
                    Button_PrintAll.setText("Print All");

                    //---- Button_Print ----
                    Button_Print.setFont(new Font("Segoe UI", Font.BOLD, 36));
                    Button_Print.setText("Print");

                    GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
                    jPanel3.setLayout(jPanel3Layout);
                    jPanel3Layout.setHorizontalGroup(
                        jPanel3Layout.createParallelGroup()
                            .addGroup(jPanel3Layout.createParallelGroup()
                                .addGroup(GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                    .addContainerGap(17, Short.MAX_VALUE)
                                    .addComponent(Button_Print, GroupLayout.PREFERRED_SIZE, 169, GroupLayout.PREFERRED_SIZE)
                                    .addGap(196, 196, 196)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Button_PrintAll, GroupLayout.PREFERRED_SIZE, 169, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                    );
                    jPanel3Layout.setVerticalGroup(
                        jPanel3Layout.createParallelGroup()
                            .addGroup(jPanel3Layout.createParallelGroup()
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(Button_Print, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addContainerGap()))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(Button_PrintAll, GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                                .addContainerGap())
                    );
                }

                //======== jScrollPane2 ========
                {

                    //---- jTextArea1 ----
                    jTextArea1.setColumns(20);
                    jTextArea1.setRows(5);
                    jScrollPane2.setViewportView(jTextArea1);
                }

                //---- Button_delete ----
                Button_delete.setBackground(new Color(0x330000));
                Button_delete.setText("Delete");

                //---- Button_deleteall ----
                Button_deleteall.setBackground(new Color(0x330000));
                Button_deleteall.setText("Delete All");
                Button_deleteall.addActionListener(e -> Button_deleteallActionPerformed(e));

                GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
                jPanel2.setLayout(jPanel2Layout);
                jPanel2Layout.setHorizontalGroup(
                    jPanel2Layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel2Layout.createParallelGroup()
                                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 712, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(0, 0, Short.MAX_VALUE)
                                    .addComponent(Button_deleteall)
                                    .addGap(18, 18, 18)
                                    .addComponent(Button_delete)))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE))
                            .addContainerGap())
                );
                jPanel2Layout.setVerticalGroup(
                    jPanel2Layout.createParallelGroup()
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel2Layout.createParallelGroup()
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 651, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(Button_delete)
                                        .addComponent(Button_deleteall))
                                    .addGap(0, 0, Short.MAX_VALUE))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 480, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addContainerGap())
                );
            }

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(Button_add)
                            .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(Button_AllRE2, GroupLayout.PREFERRED_SIZE, 240, GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(TField_EmployeeID, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE))))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(Button_AllRE2, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(TField_EmployeeID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(Button_add)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(Button_back)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(Button_back)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void Button_backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_backActionPerformed
        InstructionsKt.redirectToDashboard(this);
    }//GEN-LAST:event_Button_backActionPerformed

    private void Button_AllRegActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_AllRegActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_Button_AllRegActionPerformed

    private void Button_AllpweActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_AllpweActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Button_AllpweActionPerformed

    private void Button_AllemployActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_AllemployActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Button_AllemployActionPerformed

    private void TField_EmployeeIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TField_EmployeeIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TField_EmployeeIDActionPerformed

    private void Button_addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_addActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Button_addActionPerformed

    private void Button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_deleteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Button_deleteActionPerformed

    private void Button_PrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_PrintActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Button_PrintActionPerformed

    private void Button_PrintAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_PrintAllActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Button_PrintAllActionPerformed

    private void Button_deleteallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_deleteallActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Button_deleteallActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Payslip.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Payslip.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Payslip.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Payslip.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Payslip().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Zevoex
    private JButton Button_back;
    private JPanel jPanel1;
    private JButton Button_AllRE2;
    private JLabel jLabel1;
    private JTextField TField_EmployeeID;
    private JButton Button_add;
    private JPanel jPanel2;
    private JScrollPane jScrollPane1;
    private JTable jTable1;
    private JPanel jPanel3;
    private JButton Button_PrintAll;
    private JButton Button_Print;
    private JScrollPane jScrollPane2;
    private JTextArea jTextArea1;
    private JButton Button_delete;
    private JButton Button_deleteall;
    // End of variables declaration//GEN-END:variables
}




