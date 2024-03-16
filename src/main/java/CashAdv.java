import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class CashAdv extends javax.swing.JFrame {

    private Connection conn;
    private int selectedCashAdvanceID;


    /**
     * Creates new form CashAdv
     */
    public CashAdv() {
        initComponents();
        try {
            conn = DatabaseConnector.getConnection(); // Initialize the database connection
            // Populate employee dropdown after initializing the connection
            populateEmployeeDropdown();
            // Update the cash advance table
            updateCashAdvanceTable();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to the database: " + ex.getMessage());
            // Handle the exception appropriately (e.g., exit the application)
            System.exit(1);
        }

        // Make jTable1 not editable
        jTable1.setDefaultEditor(Object.class, null);

        // Other initialization code...



        TField_EmployeeID.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int employeeID = Integer.parseInt(TField_EmployeeID.getText());
                String employeeName = getEmployeeNameByID(employeeID);
                if (employeeName != null) {
                    Dropdown_EName.setSelectedItem(employeeName);
                } else {
                    JOptionPane.showMessageDialog(CashAdv.this, "Employee not found!");
                }
            }
        });

        Dropdown_EName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object selectedItem = Dropdown_EName.getSelectedItem();
                if (selectedItem != null) {
                    String employeeName = selectedItem.toString();
                    int employeeID = getEmployeeIDByName(employeeName);
                    if (employeeID != -1) {
                        TField_EmployeeID.setText(String.valueOf(employeeID));
                    } else {
                        JOptionPane.showMessageDialog(CashAdv.this, "Employee not found!");
                    }
                }
            }
        });


        Button_add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCashAdvance();
            }
        });

        Button_delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCashAdvance();
            }
        });

        Button_update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCashAdvance();
            }
        });

        Button_clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });


        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = jTable1.getSelectedRow();
                    if (selectedRow != -1) {
                        int cashAdvanceID = Integer.parseInt(jTable1.getValueAt(selectedRow, 0).toString());
                        int employeeID = Integer.parseInt(jTable1.getValueAt(selectedRow, 1).toString());
                        String employeeName = jTable1.getValueAt(selectedRow, 2).toString();
                        double amount = Double.parseDouble(jTable1.getValueAt(selectedRow, 3).toString());
                        String description = jTable1.getValueAt(selectedRow, 4).toString();
                        java.sql.Date dateRequested = (java.sql.Date) jTable1.getValueAt(selectedRow, 5);

                        TField_EmployeeID.setText(String.valueOf(employeeID));
                        Dropdown_EName.setSelectedItem(employeeName);
                        TField_Amount.setText(String.valueOf(amount));
                        TArea_Description.setText(description);
                        // Set the date in a formatted manner, if necessary

                        // Store the Cash_Advance_ID of the selected row
                        selectedCashAdvanceID = cashAdvanceID;
                    }
                }
            }
        });


    }

    // Method to populate the employee dropdown menu
    private void populateEmployeeDropdown() {
        try {
            String sql = "SELECT Employee_Name FROM Employees";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
                while (rs.next()) {
                    String employeeName = rs.getString("Employee_Name");
                    model.addElement(employeeName);
                }
                Dropdown_EName.setModel(model);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private String getEmployeeNameByID(int employeeID) {
        String employeeName = null;
        try {
            String sql = "SELECT Employee_Name FROM Employees WHERE Employee_ID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, employeeID);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        employeeName = rs.getString("Employee_Name");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
        return employeeName;
    }

    private int getEmployeeIDByName(String employeeName) {
        int employeeID = -1;
        try {
            String sql = "SELECT Employee_ID FROM Employees WHERE Employee_Name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, employeeName);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        employeeID = rs.getInt("Employee_ID");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
        return employeeID;
    }

    // Method to add a cash advance record
    private void addCashAdvance() {
        try {
            int employeeID = Integer.parseInt(TField_EmployeeID.getText());
            // Check if the employee has a record in Piecework_Details
            if (!employeeHasPieceworkRecord(employeeID)) {
                JOptionPane.showMessageDialog(this, "Employee does not have a piecework record. Cash advance cannot be created.");
                return;
            }

            double amount = Double.parseDouble(TField_Amount.getText());
            if (amount < 0) {
                JOptionPane.showMessageDialog(this, "Amount cannot be negative!");
                return;
            }

            // Get the current date
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());

            // Prepare SQL statement
            String sql = "INSERT INTO Cash_Advance (Employee_ID, Amount, Date_Requested, Description) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, employeeID); // Assuming Employee_ID is an integer
                pstmt.setDouble(2, amount);
                pstmt.setDate(3, currentDate);
                pstmt.setString(4, TArea_Description.getText()); // Adding description

                // Execute the query
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Cash advance added successfully!");
                    // Clear input fields after adding the cash advance
                    clearFields();
                    // Update the JTable with added cash advances
                    updateCashAdvanceTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add cash advance!");
                }
            }

        } catch (NumberFormatException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private boolean employeeHasPieceworkRecord(int employeeID) {
        try {
            String sql = "SELECT COUNT(*) AS recordCount FROM Piecework_Details WHERE Employee_ID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, employeeID);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int recordCount = rs.getInt("recordCount");
                        return recordCount > 0;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
        return false;
    }



    // Method to update the JTable with cash advances from the database
    private void updateCashAdvanceTable() {
        try {
            String sql = "SELECT Cash_Advance_ID, Employee_ID, Amount, Date_Requested, Description FROM Cash_Advance";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                model.setRowCount(0); // Clear previous data
                while (rs.next()) {
                    int cashAdvanceID = rs.getInt("Cash_Advance_ID");
                    int employeeID = rs.getInt("Employee_ID");
                    String employeeName = getEmployeeNameByID(employeeID);
                    double amount = rs.getDouble("Amount");
                    java.sql.Date dateRequested = rs.getDate("Date_Requested");
                    String description = rs.getString("Description");
                    // Adding data to JTable
                    model.addRow(new Object[]{cashAdvanceID, employeeID, employeeName, amount, description, dateRequested});
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }








    private void clearFields() {
        TField_EmployeeID.setText("");
        Dropdown_EName.setSelectedIndex(-1);
        TField_Amount.setText("");
        TArea_Description.setText("");
        // Clear the selected row in the JTable
        jTable1.getSelectionModel().clearSelection();
    }


    private void deleteCashAdvance() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a cash advance to delete.");
            return;
        }
        int cashAdvanceID = Integer.parseInt(jTable1.getValueAt(selectedRow, 0).toString());
        try {
            String sql = "DELETE FROM Cash_Advance WHERE Cash_Advance_ID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, cashAdvanceID);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Cash advance deleted successfully!");
                    clearFields();
                    updateCashAdvanceTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete cash advance!");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }





    private void updateCashAdvance() {
        double amount = Double.parseDouble(TField_Amount.getText());
        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
        String description = TArea_Description.getText();

        try {
            String sql = "UPDATE Cash_Advance SET Amount = ?, Date_Requested = ?, Description = ? WHERE Cash_Advance_ID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDouble(1, amount);
                pstmt.setDate(2, currentDate);
                pstmt.setString(3, description);
                pstmt.setInt(4, selectedCashAdvanceID); // Use selectedCashAdvanceID here
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Cash advance updated successfully!");
                    clearFields();
                    updateCashAdvanceTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update cash advance!");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }






    // Other generated code...




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
        EmployeeID1 = new javax.swing.JLabel();
        EmployeeID2 = new javax.swing.JLabel();
        EmployeeID3 = new javax.swing.JLabel();
        TField_EmployeeID = new javax.swing.JTextField();
        Dropdown_EName = new javax.swing.JComboBox<>();
        TField_Amount = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        TArea_Description = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        Button_add = new javax.swing.JButton();
        Button_update = new javax.swing.JButton();
        Button_clear = new javax.swing.JButton();
        Button_delete = new javax.swing.JButton();

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

        EmployeeID1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        EmployeeID1.setText("Employee Name:");

        EmployeeID2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        EmployeeID2.setText("Amount:");

        EmployeeID3.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        EmployeeID3.setText("Description:");

        Dropdown_EName.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        TArea_Description.setColumns(20);
        TArea_Description.setRows(5);
        jScrollPane1.setViewportView(TArea_Description);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {

                },
                // Update the column names here
                new String [] {
                        "Cash Advance ID", "Employee ID", "Employee Name", "Amount", "Description", "Date Requested"
                }
        ));
        jScrollPane2.setViewportView(jTable1);

        Button_add.setText("Add");

        Button_update.setText("Update");

        Button_clear.setText("Clear");

        Button_delete.setBackground(new java.awt.Color(129, 39, 39));
        Button_delete.setText("Delete");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                                .addComponent(EmployeeID3)
                                .addComponent(EmployeeID2)
                                .addComponent(TField_Amount)
                                .addComponent(EmployeeID)
                                .addComponent(TField_EmployeeID)
                                .addComponent(EmployeeID1)
                                .addComponent(Dropdown_EName, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(Button_add)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Button_update)
                                .addGap(16, 16, 16)
                                .addComponent(Button_clear)))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 687, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Button_delete)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(EmployeeID)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TField_EmployeeID, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(EmployeeID1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Dropdown_EName, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(EmployeeID2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TField_Amount, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(EmployeeID3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Button_add)
                            .addComponent(Button_update)
                            .addComponent(Button_clear))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Button_delete)
                .addContainerGap(14, Short.MAX_VALUE))
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
            java.util.logging.Logger.getLogger(CashAdv.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CashAdv.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CashAdv.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CashAdv.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CashAdv().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_add;
    private javax.swing.JButton Button_back;
    private javax.swing.JButton Button_clear;
    private javax.swing.JButton Button_delete;
    private javax.swing.JButton Button_update;
    private javax.swing.JComboBox<String> Dropdown_EName;
    private javax.swing.JLabel EmployeeID;
    private javax.swing.JLabel EmployeeID1;
    private javax.swing.JLabel EmployeeID2;
    private javax.swing.JLabel EmployeeID3;
    private javax.swing.JTextArea TArea_Description;
    private javax.swing.JTextField TField_Amount;
    private javax.swing.JTextField TField_EmployeeID;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
