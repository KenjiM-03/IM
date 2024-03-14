


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

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
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"Employee ID", "Employee Name", "Job Type", "Gross Pay", "Total Deduction", "Net Salary"});
        jTable1.setModel(tableModel);
    }

    private void initButtonListeners() {
        Button_AllReg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayRegularEmployees();
            }
        });

        Button_Allpwe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayPieceworkEmployees();
            }
        });

        Button_Allemploy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayAllEmployees();
            }
        });

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
    }


    private void displayRegularEmployees() {
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT e.Employee_ID, e.Employee_Name, r.Job_Type_Description "
                     + "FROM Employees e INNER JOIN Regular r ON e.Employee_ID = r.Employee_ID");
             ResultSet resultSet = statement.executeQuery()) {

            displayEmployees(resultSet);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching regular employees: " + ex.getMessage());
        }
    }

    private void displayPieceworkEmployees() {
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT e.Employee_ID, e.Employee_Name, p.Job_Type_Description "
                     + "FROM Employees e INNER JOIN Piecework p ON e.Employee_ID = p.Employee_ID");
             ResultSet resultSet = statement.executeQuery()) {

            displayEmployees(resultSet);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching piecework employees: " + ex.getMessage());
        }
    }

    private void displayAllEmployees() {
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT e.Employee_ID, e.Employee_Name, "
                     + "CASE WHEN r.Employee_ID IS NOT NULL THEN 'Regular' ELSE 'Piecework' END AS Job_Type_Description "
                     + "FROM Employees e LEFT JOIN Regular r ON e.Employee_ID = r.Employee_ID "
                     + "LEFT JOIN Piecework p ON e.Employee_ID = p.Employee_ID");
             ResultSet resultSet = statement.executeQuery()) {

            displayEmployees(resultSet);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching all employees: " + ex.getMessage());
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
        // Check if the employee is a regular employee
        String query = "SELECT * FROM Regular WHERE Employee_ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, employeeID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return "Regular";
                }
            }
        }

        // Check if the employee is a piecework employee
        query = "SELECT * FROM Piecework WHERE Employee_ID = ?";
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
            String employeeIDString = (String) jTable1.getValueAt(rowIndex, 0);

            // Parse the employee ID string to an integer
            int employeeID = Integer.parseInt(employeeIDString);

            // Fetch employee details and calculate payslip
            String employeeName = (String) jTable1.getValueAt(rowIndex, 1);
            String jobType = (String) jTable1.getValueAt(rowIndex, 2);
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
            String fileName = "Payslip_" + employeeID + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // Add content to the PDF
            document.add(new Paragraph(payslipPreview));

            document.close();
            JOptionPane.showMessageDialog(this, "Payslip printed successfully. File saved as " + fileName);
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
                String fileName = "Payslip_" + employeeID + ".pdf";
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(fileName));
                document.open();

                // Add content to the PDF
                document.add(new Paragraph(payslipPreview));

                document.close();
                JOptionPane.showMessageDialog(this, "Payslip printed successfully. File saved as " + fileName);
            } catch (FileNotFoundException | DocumentException e) {
                JOptionPane.showMessageDialog(this, "Error printing payslip: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // Your variable declarations here
    // End of variables declaration//GEN-END:variables



    // End of variables declaration//GEN-END:variables

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Button_back = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        Button_Allemploy = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        TField_EmployeeID = new javax.swing.JTextField();
        Button_add = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        Button_delete = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        Total_employee_cal = new javax.swing.JLabel();
        Total_payment_cal = new javax.swing.JLabel();
        Button_PrintAll = new javax.swing.JButton();
        Button_Print = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        Button_AllReg = new javax.swing.JButton();
        Button_Allpwe = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Button_back.setText("<Back");
        Button_back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_backActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));

        Button_Allemploy.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Button_Allemploy.setText("All Employees");
        Button_Allemploy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_AllemployActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel1.setText("Employee ID:");

        TField_EmployeeID.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        TField_EmployeeID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TField_EmployeeIDActionPerformed(evt);
            }
        });

        Button_add.setText("Add");
        Button_add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_addActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(0, 51, 51));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {

                },
                new String [] {
                        "Employee ID", "Employee Name", "Job Type", "Gross Pay", "Total Deduction", "Net Salary"
                }
        ));
        jScrollPane1.setViewportView(jTable1);

        Button_delete.setBackground(new java.awt.Color(51, 0, 0));
        Button_delete.setText("Delete");
        Button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_deleteActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(102, 102, 102));
        jPanel3.setForeground(new java.awt.Color(51, 51, 51));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(153, 255, 255));
        jLabel4.setText("Total Employee:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(153, 255, 255));
        jLabel5.setText("Total Payment:");

        Total_employee_cal.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        Total_employee_cal.setForeground(new java.awt.Color(153, 255, 255));
        Total_employee_cal.setText("00");

        Total_payment_cal.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        Total_payment_cal.setForeground(new java.awt.Color(153, 255, 255));
        Total_payment_cal.setText("00");

        Button_PrintAll.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        Button_PrintAll.setText("Print All");
        Button_PrintAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_PrintAllActionPerformed(evt);
            }
        });

        Button_Print.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        Button_Print.setText("Print");
        Button_Print.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_PrintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(75, 75, 75)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(Total_payment_cal, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(Total_employee_cal, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 657, Short.MAX_VALUE)
                                .addComponent(Button_PrintAll, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addContainerGap(469, Short.MAX_VALUE)
                                        .addComponent(Button_Print, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(196, 196, 196)))
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(49, 49, 49)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(Total_employee_cal))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel5)
                                        .addComponent(Total_payment_cal))
                                .addGap(57, 57, 57))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(Button_PrintAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(Button_Print, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addContainerGap()))
        );

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 712, Short.MAX_VALUE)
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addGap(0, 0, Short.MAX_VALUE)
                                                                .addComponent(Button_delete)))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(Button_delete))
                                        .addComponent(jScrollPane2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );

        Button_AllReg.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Button_AllReg.setText("All Regular Employees");
        Button_AllReg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_AllRegActionPerformed(evt);
            }
        });

        Button_Allpwe.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Button_Allpwe.setText("All Piecework Employees");
        Button_Allpwe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_AllpweActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(Button_add)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(Button_Allemploy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addComponent(jLabel1)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                                                        .addComponent(TField_EmployeeID, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addComponent(Button_Allpwe, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addComponent(Button_AllReg, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(59, 59, 59)
                                .addComponent(Button_AllReg, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Button_Allpwe, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Button_Allemploy, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(TField_EmployeeID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Button_add)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(Button_back)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(Button_back)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
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
    private javax.swing.JButton Button_AllReg;
    private javax.swing.JButton Button_Allemploy;
    private javax.swing.JButton Button_Allpwe;
    private javax.swing.JButton Button_Print;
    private javax.swing.JButton Button_PrintAll;
    private javax.swing.JButton Button_add;
    private javax.swing.JButton Button_back;
    private javax.swing.JButton Button_delete;
    private javax.swing.JTextField TField_EmployeeID;
    private javax.swing.JLabel Total_employee_cal;
    private javax.swing.JLabel Total_payment_cal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}




