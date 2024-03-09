/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.awt.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


/**
 *
 * @author keo
 */
public class EmployeeCRUD extends javax.swing.JFrame {

//=======================================================================================================
    // Method to load employee data from the database
    private void loadEmployeeDataFromDatabase() {
        // Get database connection
        Connection connection = null;
        try {
            connection = DatabaseConnector.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (connection == null) {
            // Handle connection failure
            return;
        }
        try {
            // SQL query to join employees with piecework or regular table based on Job Type
            String query = "SELECT e.Employee_ID, e.Employee_Name, e.Contact_Number, e.Gender, e.Date_of_Birth, "
                    + "CASE WHEN p.Job_Type_Description IS NOT NULL THEN p.Job_Type_Description ELSE r.Job_Type_Description END AS Job_Type "
                    + "FROM employees e "
                    + "LEFT JOIN piecework p ON e.Employee_ID = p.Employee_ID "
                    + "LEFT JOIN regular r ON e.Employee_ID = r.Employee_ID";

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int employeeId = resultSet.getInt("Employee_ID");
                String name = resultSet.getString("Employee_Name");
                String contact = resultSet.getString("Contact_Number");
                String gender = resultSet.getString("Gender");
                String dob = resultSet.getString("Date_of_Birth");
                String jobType = resultSet.getString("Job_Type");

                // Add the data to the JTable
                DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                model.addRow(new Object[]{employeeId, name, contact, gender, dob, jobType});
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQL errors
        } finally {
            // Close the connection in the finally block to ensure it gets closed even if an exception occurs
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    // Handle connection closure error
                }
            }
        }
    }
// ============================================================================================
private void createEmployee() {
    String employeeName = TField_EmployeeName.getText();
    String contactNumber = TField_ContactNumber.getText();
    String gender = (String) Dropdown_Gender.getSelectedItem();
    String dateOfBirth = TField_DateOfBirth.getText();
    String jobTypeDescription = (String) Dropdown_JobType.getSelectedItem();
    try {
        Connection connection = DatabaseConnector.getConnection();
        Employee.insertEmployee(connection, employeeName, contactNumber, gender, dateOfBirth, jobTypeDescription);
        refreshTable();
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error creating employee: " + e.getMessage());
    }
}

    private void updateEmployee() {
        try {
            int employeeId = Integer.parseInt(TField_EmployeeID.getText());
            String employeeName = TField_EmployeeName.getText();
            String contactNumber = TField_ContactNumber.getText();
            String gender = (String) Dropdown_Gender.getSelectedItem();
            String dateOfBirth = TField_DateOfBirth.getText();
            String jobTypeDescription = (String) Dropdown_JobType.getSelectedItem();

            Connection connection = DatabaseConnector.getConnection();
            Employee.updateEmployee(connection, employeeId, employeeName, contactNumber, gender, dateOfBirth, jobTypeDescription);
            JOptionPane.showMessageDialog(this, "Employee updated successfully!");
            refreshTable();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Employee ID.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating employee: " + e.getMessage());
        }
    }

    private void deleteEmployee() {
        try {
            int employeeId = Integer.parseInt(TField_EmployeeID.getText());
            Connection connection = DatabaseConnector.getConnection();
            Employee.deleteEmployee(connection, employeeId);
            JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
            refreshTable();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Employee ID.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting employee: " + e.getMessage());
        }
    }

    private void clearForm() {
        TField_EmployeeID.setText("");
        TField_EmployeeName.setText("");
        TField_ContactNumber.setText("");
        Dropdown_Gender.setSelectedIndex(0);
        TField_DateOfBirth.setText("");
        Dropdown_JobType.setSelectedIndex(0);
    }

    private void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        loadEmployeeDataFromDatabase();
    }


//==========================================================================================

//===========================================================================
    /**
     * Creates new form EmployeeCRUD
     */
    public EmployeeCRUD() {
        initComponents();
        loadEmployeeDataFromDatabase();

    }

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
        EmployeeID = new javax.swing.JLabel();
        EmployeeName = new javax.swing.JLabel();
        Contactnumber = new javax.swing.JLabel();
        Gender = new javax.swing.JLabel();
        DateOfBirth = new javax.swing.JLabel();
        JobType = new javax.swing.JLabel();
        TField_EmployeeID = new javax.swing.JTextField();
        TField_DateOfBirth = new javax.swing.JTextField();
        TField_ContactNumber = new javax.swing.JTextField();
        TField_EmployeeName = new javax.swing.JTextField();
        Dropdown_Gender = new javax.swing.JComboBox<>();
        Dropdown_JobType = new javax.swing.JComboBox<>();
        Button_update = new javax.swing.JButton();
        Button_clear = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        Button_delete = new javax.swing.JButton();
        Button_add = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Button_back.setText("<Back");
        Button_back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_backActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(0, 51, 51));

        EmployeeID.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        EmployeeID.setText("Employee ID:");

        EmployeeName.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        EmployeeName.setText("Employee Name:");

        Contactnumber.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Contactnumber.setText("Contact Number:");

        Gender.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        Gender.setText("Gender:");

        DateOfBirth.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        DateOfBirth.setText("Date of Birth (YYYY-MM-DD):");

        JobType.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        JobType.setText("Job Type:");

        Dropdown_Gender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female"}));
        Dropdown_Gender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Dropdown_GenderActionPerformed(evt);
            }
        });

        Dropdown_JobType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "regular", "piecework" }));

        Button_update.setText("Update");
        Button_update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_updateActionPerformed(evt);
            }
        });

        Button_clear.setText("Clear");
        Button_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_clearActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Employee ID", "Employee Name", "Contact Number", "Gender", "Date of Birth", "Job Type"
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

        Button_add.setText("Add");
        Button_add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_addActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(EmployeeID)
                                    .addComponent(EmployeeName)
                                    .addComponent(JobType)
                                    .addComponent(Contactnumber)
                                    .addComponent(Gender)
                                    .addComponent(DateOfBirth)
                                    .addComponent(TField_EmployeeName)
                                    .addComponent(TField_EmployeeID)
                                    .addComponent(TField_ContactNumber)
                                    .addComponent(Dropdown_Gender, 0, 300, Short.MAX_VALUE)
                                    .addComponent(TField_DateOfBirth)
                                    .addComponent(Dropdown_JobType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(Button_add)
                                .addGap(18, 18, 18)
                                .addComponent(Button_update)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                                .addComponent(Button_clear)))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 850, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(Button_delete)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(EmployeeID)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TField_EmployeeID, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(EmployeeName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TField_EmployeeName, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Contactnumber)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TField_ContactNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Gender)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Dropdown_Gender, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(DateOfBirth)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TField_DateOfBirth, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(JobType)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Dropdown_JobType, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Button_update)
                    .addComponent(Button_clear)
                    .addComponent(Button_add))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 597, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Button_delete)
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Button_back)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void Button_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_updateActionPerformed
        updateEmployee();
    }//GEN-LAST:event_Button_updateActionPerformed

    private void Button_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_clearActionPerformed
        clearForm();
    }//GEN-LAST:event_Button_clearActionPerformed

    private void Button_addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_addActionPerformed
        createEmployee();
    }//GEN-LAST:event_Button_addActionPerformed

    private void Dropdown_GenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Dropdown_GenderActionPerformed
    }//GEN-LAST:event_Dropdown_GenderActionPerformed

    private void Button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_deleteActionPerformed
        deleteEmployee();
    }//GEN-LAST:event_Button_deleteActionPerformed

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
            java.util.logging.Logger.getLogger(EmployeeCRUD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EmployeeCRUD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EmployeeCRUD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EmployeeCRUD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EmployeeCRUD().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_add;
    private javax.swing.JButton Button_back;
    private javax.swing.JButton Button_clear;
    private javax.swing.JButton Button_delete;
    private javax.swing.JButton Button_update;
    private javax.swing.JLabel Contactnumber;
    private javax.swing.JLabel DateOfBirth;
    private javax.swing.JComboBox<String> Dropdown_Gender;
    private javax.swing.JComboBox<String> Dropdown_JobType;
    private javax.swing.JLabel EmployeeID;
    private javax.swing.JLabel EmployeeName;
    private javax.swing.JLabel Gender;
    private javax.swing.JLabel JobType;
    private javax.swing.JTextField TField_ContactNumber;
    private javax.swing.JTextField TField_DateOfBirth;
    private javax.swing.JTextField TField_EmployeeID;
    private javax.swing.JTextField TField_EmployeeName;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
