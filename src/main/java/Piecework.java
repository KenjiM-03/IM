        /*
         * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
         * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
         */
        import java.awt.event.ActionEvent;
        import java.awt.event.ActionListener;
        import java.sql.*;
        import javax.swing.*;
        import javax.swing.table.DefaultTableModel;

        /**
         *
         * @author keo
         */
        public class Piecework extends javax.swing.JFrame {
            //======================================================================
            private void loadPieceworkDetails() {
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
                    String query = "SELECT pd.Employee_ID, e.Employee_Name, pt.Size, pd.Quantity, pd.Transaction_ID, t.Date "
                            + "FROM piecework_details pd "
                            + "JOIN piecework p ON pd.Employee_ID = p.Employee_ID "
                            + "JOIN transaction t ON pd.Transaction_ID = t.Transaction_ID "
                            + "JOIN packtype pt ON pd.PackType_ID = pt.PackType_ID "
                            + "JOIN employees e ON pd.Employee_ID = e.Employee_ID";

                    PreparedStatement statement = connection.prepareStatement(query);
                    ResultSet resultSet = statement.executeQuery();

                    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                    model.setRowCount(0); // Clear previous data

                    DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();

                    while (resultSet.next()) {
                        int employeeId = resultSet.getInt("Employee_ID");
                        String employeeName = resultSet.getString("Employee_Name");
                        String size = resultSet.getString("Size");
                        int quantity = resultSet.getInt("Quantity");
                        int transactionId = resultSet.getInt("Transaction_ID");
                        String date = resultSet.getString("Date");

                        model.addRow(new Object[]{employeeId, employeeName, size, quantity, transactionId, date});
                        comboBoxModel.addElement(employeeName); // Add employee name to the dropdown
                    }

                    Dropdown_EName.setModel(comboBoxModel); // Set the model for the dropdown

                    resultSet.close();
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
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

            private void insertPiecework() {
                // Validate input fields
                if (TField_EmployeeID.getText().isEmpty() || TField_Quantity.getText().isEmpty() || Dropdown_Size.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
                    return;
                }

                try {
                    // Parse input values
                    int employeeId = Integer.parseInt(TField_EmployeeID.getText());
                    int sizeId = getSelectedSizeId();
                    int quantity = Integer.parseInt(TField_Quantity.getText());
                    int transactionId = generateTransactionId();

                    // Get a connection from DatabaseConnector
                    Connection connection = DatabaseConnector.getConnection();

                    // Prepare and execute INSERT statement
                    String sql = "INSERT INTO piecework_details (Employee_ID, PackType_ID, Quantity, Transaction_ID) VALUES (?, ?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, employeeId);
                    statement.setInt(2, sizeId);
                    statement.setInt(3, quantity);
                    statement.setInt(4, transactionId);
                    statement.executeUpdate();

                    // Refresh table to reflect changes
                    loadPieceworkDetails();

                    // Provide user feedback
                    JOptionPane.showMessageDialog(this, "Piecework added successfully.");

                } catch (SQLException e) {
                    // Handle insertion error gracefully
                    JOptionPane.showMessageDialog(this, "Error adding piecework: " + e.getMessage());
                }
            }//test
            private void populateEmployeeDropdown() {
                // Remove default items
                Dropdown_EName.removeAllItems();

                // Connect to the database and populate the dropdown
                try (Connection conn = DatabaseConnector.getConnection();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT e.Employee_Name FROM Employees e INNER JOIN Piecework p ON e.Employee_ID = p.Employee_ID"))

                {
                    while (rs.next()) {
                        Dropdown_EName.addItem(rs.getString("Employee_Name"));
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error populating employee list: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            private  void populateSizeDropdown(){
                Dropdown_Size.removeAllItems();
                try (Connection conn = DatabaseConnector.getConnection();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT Size FROM packtype p"))

                {
                    while (rs.next()) {
                        Dropdown_Size.addItem(rs.getString("Size"));
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error populating employee list: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }


            private int getSelectedSizeId() {
                int selectedIndex = Dropdown_Size.getSelectedIndex();
                if (selectedIndex == -1) {
                    // Handle the case where no size is selected
                    JOptionPane.showMessageDialog(this, "Please select a size.");
                    return -1;  // Or throw an exception if appropriate
                }

                // Assuming the Dropdown_Size's items are populated with size IDs, retrieve the ID
                return Integer.parseInt((String) Dropdown_Size.getSelectedItem());
            }
            private int generateTransactionId() {
                // Option 1: Leverage database-generated IDs (if applicable)
                return -1; // Database will assign ID during insert

                // Option 2: Implement custom logic for unique ID generation
                // Replace with your preferred logic (e.g., using timestamps or sequences)
                // ...
            }
            private void autofillEmployeeID() {
                // Get the selected employee name from the dropdown
                String selectedEmployeeName = (String) Dropdown_EName.getSelectedItem();

                // Connect to the database and fetch the corresponding employee ID
                try (Connection conn = DatabaseConnector.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("SELECT Employee_ID FROM Employees WHERE Employee_Name = ?")) {

                    stmt.setString(1, selectedEmployeeName);
                    ResultSet rs = stmt.executeQuery();

                    // If a result is found, populate the Employee ID field
                    if (rs.next()) {
                        int employeeID = rs.getInt("Employee_ID");
                        TField_EmployeeID.setText(String.valueOf(employeeID));
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error fetching employee ID: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            private void createPiecework() {
                // Validate input fields
                if (TField_EmployeeID.getText().isEmpty() || TField_Quantity.getText().isEmpty() || Dropdown_Size.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
                    return;
                }

                try {
                    // Parse input values
                    int employeeId = Integer.parseInt(TField_EmployeeID.getText());
                    int sizeId = getSelectedSizeId();
                    int quantity = Integer.parseInt(TField_Quantity.getText());
                    int transactionId = generateTransactionId();

                    // Get a connection from DatabaseConnector
                    Connection connection = DatabaseConnector.getConnection();

                    // Prepare and execute INSERT statement
                    String sql = "INSERT INTO piecework_details (Employee_ID, PackType_ID, Quantity, Transaction_ID) VALUES (?, ?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, employeeId);
                    statement.setInt(2, sizeId);
                    statement.setInt(3, quantity);
                    statement.setInt(4, transactionId);
                    statement.executeUpdate();

                    // Refresh table to reflect changes
                    loadPieceworkDetails();

                    // Provide user feedback
                    JOptionPane.showMessageDialog(this, "Piecework added successfully.");

                } catch (SQLException e) {
                    // Handle insertion error gracefully
                    JOptionPane.showMessageDialog(this, "Error adding piecework: " + e.getMessage());
                }
            }

            // Call this method when the "Add" button is clicked



            // Method to populate text fields from selected row in JTable
            private void populateFieldsFromTable() {
                int selectedRow = jTable1.getSelectedRow();
                if (selectedRow != -1) { // Check if a row is selected
                    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                    TField_EmployeeID.setText(model.getValueAt(selectedRow, 0).toString());
                    // Populate other text fields similarly with the corresponding column values
                }
            }

            // Method to clear text fields
            // Method to clear text fields
            private void clearFields() {
                TField_EmployeeID.setText("");
                TField_Quantity.setText("");
                Dropdown_EName.setSelectedIndex(-1);
                Dropdown_Size.setSelectedIndex(-1);
            }

            // Method to update piecework details
            private void updatePiecework() {
                int selectedRow = jTable1.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this, "Please select a piecework to update.");
                    return;
                }

                int employeeId = Integer.parseInt(TField_EmployeeID.getText().trim());
                String size = jTable1.getValueAt(selectedRow, 2).toString();
                int quantity = Integer.parseInt(TField_Quantity.getText().trim());
                int transactionId = Integer.parseInt(jTable1.getValueAt(selectedRow, 4).toString());

                try {
                    Connection connection = DatabaseConnector.getConnection();
                    String packTypeIdQuery = "SELECT PackType_ID FROM PackType WHERE Size = ?";
                    try (PreparedStatement packTypeStatement = connection.prepareStatement(packTypeIdQuery)) {
                        packTypeStatement.setString(1, size);
                        ResultSet resultSet = packTypeStatement.executeQuery();
                        int packTypeId = -1;
                        if (resultSet.next()) {
                            packTypeId = resultSet.getInt("PackType_ID");
                        } else {
                            JOptionPane.showMessageDialog(this, "Size not found in the PackType table.");
                            return;
                        }

                        String query = "UPDATE Piecework_Details SET PackType_ID = ?, Quantity = ? WHERE Employee_ID = ? AND Transaction_ID = ?";
                        try (PreparedStatement statement = connection.prepareStatement(query)) {
                            statement.setInt(1, packTypeId);
                            statement.setInt(2, quantity);
                            statement.setInt(3, employeeId);
                            statement.setInt(4, transactionId);
                            int rowsAffected = statement.executeUpdate();
                            if (rowsAffected > 0) {
                                JOptionPane.showMessageDialog(this, "Piecework updated successfully!");
                                loadPieceworkDetails();
                            } else {
                                JOptionPane.showMessageDialog(this, "Piecework not found for the specified Employee ID and Transaction ID.");
                            }
                        }
                    }
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(this, "Please enter valid numeric values for Quantity.");
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error updating piecework: " + e.getMessage());
                }
            }



            private void deletePiecework() {
                int selectedRow = jTable1.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this, "Please select a piecework to delete.");
                    return;
                }

                int employeeId = Integer.parseInt(TField_EmployeeID.getText().trim());
                int transactionId = Integer.parseInt(jTable1.getValueAt(selectedRow, 4).toString());

                try {
                    Connection connection = DatabaseConnector.getConnection();
                    String query = "DELETE FROM Piecework_Details WHERE Employee_ID = ? AND Transaction_ID = ?";
                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        statement.setInt(1, employeeId);
                        statement.setInt(2, transactionId);
                        int rowsAffected = statement.executeUpdate();
                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(this, "Piecework deleted successfully!");
                            loadPieceworkDetails();
                        } else {
                            JOptionPane.showMessageDialog(this, "Piecework not found for the specified Employee ID and Transaction ID.");
                        }
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error deleting piecework: " + e.getMessage());
                }
            }





            //===========================================================================================
            /**
             * Creates new form Piecework
             */
            public Piecework() {
                initComponents();
                loadPieceworkDetails();
                populateEmployeeDropdown();
                populateSizeDropdown();
                TField_EmployeeID.setEditable(false);

                Button_update.addActionListener(e -> updatePiecework());
                Button_clear.addActionListener(e -> clearFields());
                Button_delete.addActionListener(e -> deletePiecework());

                // Add this line in the constructor to attach a ListSelectionListener to jTable1
                jTable1.getSelectionModel().addListSelectionListener(e -> {
                    if (!e.getValueIsAdjusting()) {
                        int selectedRow = jTable1.getSelectedRow();
                        if (selectedRow != -1) {
                            // Read data from the selected row and populate the fields
                            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                            TField_EmployeeID.setText(String.valueOf(model.getValueAt(selectedRow, 0)));
                            Dropdown_EName.setSelectedItem(model.getValueAt(selectedRow, 1));
                            Dropdown_Size.setSelectedItem(model.getValueAt(selectedRow, 2));
                            TField_Quantity.setText(String.valueOf(model.getValueAt(selectedRow, 3)));
                        }
                    }
                });




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
                EmployeeID1 = new javax.swing.JLabel();
                EmployeeID2 = new javax.swing.JLabel();
                EmployeeID4 = new javax.swing.JLabel();
                TField_EmployeeID = new javax.swing.JTextField();
                Dropdown_EName = new javax.swing.JComboBox<>();
                TField_Quantity = new javax.swing.JTextField();
                Button_add = new javax.swing.JButton();
                Button_update = new javax.swing.JButton();
                Button_clear = new javax.swing.JButton();
                jScrollPane1 = new javax.swing.JScrollPane();
                jTable1 = new javax.swing.JTable();
                Button_delete = new javax.swing.JButton();
                Dropdown_Size = new javax.swing.JComboBox<>();

                setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

                Button_back.setText("<Back");
                Button_back.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        Button_backActionPerformed(evt);
                    }
                });
                Button_add.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        insertPiecework();
                    }
                });
                // Add action listener to Dropdown_EName
                Dropdown_EName.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Call method to autofill employee ID field
                        autofillEmployeeID();
                    }
                });



                jPanel1.setBackground(new java.awt.Color(0, 51, 51));

                EmployeeID.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
                EmployeeID.setText("Employee ID:");

                EmployeeID1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
                EmployeeID1.setText("Size:");

                EmployeeID2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
                EmployeeID2.setText("Quantity:");

                EmployeeID4.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
                EmployeeID4.setText("Employee Name:");

                Dropdown_EName.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

                Button_add.setText("Add");

                Button_update.setText("Update");

                Button_clear.setText("Clear");

                jTable1.setModel(new javax.swing.table.DefaultTableModel(
                        new Object [][] {

                        },
                        new String [] {
                                "Employee ID", "Employee Name", "Size", "Quantity", "Transaction Id", "Date"
                        }
                ));
                jScrollPane1.setViewportView(jTable1);

                Button_delete.setBackground(new java.awt.Color(51, 0, 0));
                Button_delete.setText("Delete");

                Dropdown_Size.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

                javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
                jPanel1.setLayout(jPanel1Layout);
                jPanel1Layout.setHorizontalGroup(
                        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addGap(15, 15, 15)
                                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                .addComponent(EmployeeID1)
                                                                .addComponent(EmployeeID2)
                                                                .addComponent(TField_Quantity, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(EmployeeID)
                                                                .addComponent(EmployeeID4)
                                                                .addComponent(Dropdown_EName, 0, 324, Short.MAX_VALUE)
                                                                .addComponent(TField_EmployeeID)
                                                                .addComponent(Dropdown_Size, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                                        .addGap(20, 20, 20)
                                                                        .addComponent(Button_add)
                                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                        .addComponent(Button_update)
                                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                        .addComponent(Button_clear)))
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 823, Short.MAX_VALUE))
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(Button_delete)))
                                        .addContainerGap())
                );
                jPanel1Layout.setVerticalGroup(
                        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(25, 25, 25)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 503, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addComponent(EmployeeID)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(TField_EmployeeID, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(EmployeeID4)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(Dropdown_EName, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(EmployeeID1)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(Dropdown_Size, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(24, 24, 24)
                                                        .addComponent(EmployeeID2)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(TField_Quantity, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(48, 48, 48)
                                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                .addComponent(Button_add)
                                                                .addComponent(Button_update)
                                                                .addComponent(Button_clear))))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(Button_delete)
                                        .addContainerGap(22, Short.MAX_VALUE))
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
            private void Button_addActionPerformed(java.awt.event.ActionEvent evt) {
                createPiecework();
            }

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
                    java.util.logging.Logger.getLogger(Piecework.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    java.util.logging.Logger.getLogger(Piecework.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    java.util.logging.Logger.getLogger(Piecework.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                } catch (javax.swing.UnsupportedLookAndFeelException ex) {
                    java.util.logging.Logger.getLogger(Piecework.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
                //</editor-fold>

                /* Create and display the form */
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        new Piecework().setVisible(true);
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
            private javax.swing.JComboBox<String> Dropdown_Size;
            private javax.swing.JLabel EmployeeID;
            private javax.swing.JLabel EmployeeID1;
            private javax.swing.JLabel EmployeeID2;
            private javax.swing.JLabel EmployeeID4;
            private javax.swing.JTextField TField_EmployeeID;
            private javax.swing.JTextField TField_Quantity;
            private javax.swing.JPanel jPanel1;
            private javax.swing.JScrollPane jScrollPane1;
            private javax.swing.JTable jTable1;
            // End of variables declaration//GEN-END:variables
        }
