/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import javax.swing.table.DefaultTableModel;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author keo
 */
public class DashBoard extends javax.swing.JFrame {
// ================================================================================
private DefaultTableModel tableModel;
    private void loadPieceworkEmployeeTally() {
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
            String query = "SELECT p.Employee_ID, e.Employee_Name, "
                    + "SUM(CASE WHEN pt.Size = 'Small' THEN pd.Quantity ELSE 0 END) AS Small, "
                    + "SUM(CASE WHEN pt.Size = 'Medium' THEN pd.Quantity ELSE 0 END) AS Medium, "
                    + "SUM(CASE WHEN pt.Size = 'Large' THEN pd.Quantity ELSE 0 END) AS Large, "
                    + "t.Date "
                    + "FROM piecework p "
                    + "JOIN employees e ON p.Employee_ID = e.Employee_ID "
                    + "JOIN piecework_details pd ON p.Employee_ID = pd.Employee_ID "
                    + "JOIN transaction t ON pd.Transaction_ID = t.Transaction_ID "
                    + "JOIN packtype pt ON pd.PackType_ID = pt.PackType_ID "
                    + "GROUP BY p.Employee_ID, t.Date";

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int employeeId = resultSet.getInt("Employee_ID");
                String employeeName = resultSet.getString("Employee_Name");
                int smallQuantity = resultSet.getInt("Small");
                int mediumQuantity = resultSet.getInt("Medium");
                int largeQuantity = resultSet.getInt("Large");
                String date = resultSet.getString("Date");

                tableModel.addRow(new Object[]{employeeId, employeeName, smallQuantity, mediumQuantity, largeQuantity, date});
            }

        resultSet.close();
        statement.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    //====================================================================================
    private DefaultTableModel tableModel1;
    private void loadRegularEmployeeTally() {
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

        DefaultTableModel tableModel1 = (DefaultTableModel) jTable1.getModel();

        try {
            String query = "SELECT e.Employee_ID, e.Employee_Name, "
                    + "TIME_FORMAT(SEC_TO_TIME(SUM(TIMESTAMPDIFF(SECOND, dtr.Time_In, dtr.Time_Out) - 3600)), '%k Hrs, %i Min') AS Hours_Worked, "
                    + "TIME_FORMAT(SEC_TO_TIME(SUM(TIMESTAMPDIFF(SECOND, '17:00:00', dtr.Time_Out))), '%k Hrs, %i Min') AS Overtime, "
                    + "a.Date "
                    + "FROM employees e "
                    + "JOIN regular r ON e.Employee_ID = r.Employee_ID "
                    + "JOIN dtr ON e.Employee_ID = dtr.Employee_ID "
                    + "JOIN attendance a ON dtr.Attendance_ID = a.Attendance_ID "
                    + "GROUP BY e.Employee_ID, a.Date";

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            Set<Integer> processedEmployees = new HashSet<>();

            while (resultSet.next()) {
                int employeeId = resultSet.getInt("Employee_ID");
                String employeeName = resultSet.getString("Employee_Name");
                String hoursWorked = resultSet.getString("Hours_Worked");
                String overtime = resultSet.getString("Overtime");
                String date = resultSet.getString("Date");

                // Check if this employee for this date has already been processed
                if (processedEmployees.contains(employeeId)) {
                    continue; // Skip to the next iteration if already processed
                }

                tableModel1.addRow(new Object[]{employeeId, employeeName, hoursWorked, overtime, date});
                processedEmployees.add(employeeId); // Mark this employee as processed for this date
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ============================================================================
    /**
     * Creates new form DashBoard
     */
    public DashBoard() {
        initComponents();
        tableModel1 = (DefaultTableModel) jTable1.getModel();
        loadRegularEmployeeTally();
        tableModel = (DefaultTableModel) jTable2.getModel();
        loadPieceworkEmployeeTally();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Button_Exit = new javax.swing.JButton();
        Panel_Count = new javax.swing.JPanel();
        lbl_Regular = new javax.swing.JLabel();
        Employeestotal = new javax.swing.JLabel();
        lbl_Piecework = new javax.swing.JLabel();
        Regular_total = new javax.swing.JLabel();
        Piecework_total = new javax.swing.JLabel();
        BoldRegular = new javax.swing.JLabel();
        BoldPiecework = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        Button_employee = new javax.swing.JButton();
        Button_piecework = new javax.swing.JButton();
        Button_regular = new javax.swing.JButton();
        Button_payfactor = new javax.swing.JButton();
        Button_cashadvance = new javax.swing.JButton();
        Button_overtimeeligible = new javax.swing.JButton();
        Button_payslip = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Button_Exit.setText("EXIT");
        Button_Exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_ExitActionPerformed(evt);
            }
        });

        Panel_Count.setBackground(new java.awt.Color(0, 51, 51));

        lbl_Regular.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lbl_Regular.setText("Regular:");

        Employeestotal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Employeestotal.setText("EMPLOYEES TOTAL");

        lbl_Piecework.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lbl_Piecework.setText("Piecework:");

        Regular_total.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Regular_total.setText("00");

        Piecework_total.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Piecework_total.setText("00");

        BoldRegular.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        BoldRegular.setText("Regular");

        BoldPiecework.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        BoldPiecework.setText("Piecework");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Employee ID", "Employee Name", "Hours Worked", "Overtime", "Date"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Employee ID", "Employee Name", "Small", "Medium", "Large", "Date"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        javax.swing.GroupLayout Panel_CountLayout = new javax.swing.GroupLayout(Panel_Count);
        Panel_Count.setLayout(Panel_CountLayout);
        Panel_CountLayout.setHorizontalGroup(
            Panel_CountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel_CountLayout.createSequentialGroup()
                .addGroup(Panel_CountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Panel_CountLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(Panel_CountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Employeestotal)
                            .addGroup(Panel_CountLayout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(lbl_Regular)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Regular_total, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(Panel_CountLayout.createSequentialGroup()
                                .addComponent(lbl_Piecework)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Piecework_total, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(Panel_CountLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(Panel_CountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(BoldRegular, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BoldPiecework)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1154, Short.MAX_VALUE)
                            .addComponent(jScrollPane1))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        Panel_CountLayout.setVerticalGroup(
            Panel_CountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel_CountLayout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(Employeestotal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(Panel_CountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_Regular)
                    .addComponent(Regular_total))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(Panel_CountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_Piecework)
                    .addComponent(Piecework_total))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BoldRegular)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(BoldPiecework)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        Button_employee.setText("Employee");
        Button_employee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_employeeActionPerformed(evt);
            }
        });

        Button_piecework.setText("PieceWork");
        Button_piecework.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_pieceworkActionPerformed(evt);
            }
        });

        Button_regular.setText("Regular");
        Button_regular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_regularActionPerformed(evt);
            }
        });

        Button_payfactor.setText("Pay Factors");
        Button_payfactor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_payfactorActionPerformed(evt);
            }
        });

        Button_cashadvance.setText("Cash Advance");
        Button_cashadvance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_cashadvanceActionPerformed(evt);
            }
        });

        Button_overtimeeligible.setText("Overtime Eligible");
        Button_overtimeeligible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_overtimeeligibleActionPerformed(evt);
            }
        });

        Button_payslip.setText("Payslip");
        Button_payslip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_payslipActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(Button_employee)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Button_piecework)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Button_regular)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Button_payfactor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Button_cashadvance)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Button_overtimeeligible)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Button_payslip)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 464, Short.MAX_VALUE)
                .addComponent(Button_Exit))
            .addComponent(Panel_Count, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Button_Exit)
                    .addComponent(Button_employee)
                    .addComponent(Button_piecework)
                    .addComponent(Button_regular)
                    .addComponent(Button_payfactor)
                    .addComponent(Button_cashadvance)
                    .addComponent(Button_overtimeeligible)
                    .addComponent(Button_payslip))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Panel_Count, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Button_employeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_employeeActionPerformed
        InstructionsKt.redirectToEmployeeCRUD(this);
    }//GEN-LAST:event_Button_employeeActionPerformed

    private void Button_pieceworkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_pieceworkActionPerformed
        InstructionsKt.redirectToPiecework(this);
    }//GEN-LAST:event_Button_pieceworkActionPerformed

    private void Button_regularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_regularActionPerformed
        InstructionsKt.redirectToRegular(this);
    }//GEN-LAST:event_Button_regularActionPerformed

    private void Button_payfactorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_payfactorActionPerformed
        InstructionsKt.redirectToPayFactor(this);
    }//GEN-LAST:event_Button_payfactorActionPerformed

    private void Button_cashadvanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_cashadvanceActionPerformed
        InstructionsKt.redirectToCashAdvance(this);
    }//GEN-LAST:event_Button_cashadvanceActionPerformed

    private void Button_overtimeeligibleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_overtimeeligibleActionPerformed
        InstructionsKt.redirectToOvertimeEligible(this);
    }//GEN-LAST:event_Button_overtimeeligibleActionPerformed

    private void Button_payslipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_payslipActionPerformed
        InstructionsKt.redirectToPayslip(this);
    }//GEN-LAST:event_Button_payslipActionPerformed

    private void Button_ExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_ExitActionPerformed
        InstructionsKt.redirectToAdminSignIn(this);
    }//GEN-LAST:event_Button_ExitActionPerformed


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
            java.util.logging.Logger.getLogger(DashBoard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DashBoard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DashBoard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DashBoard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DashBoard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BoldPiecework;
    private javax.swing.JLabel BoldRegular;
    private javax.swing.JButton Button_Exit;
    private javax.swing.JButton Button_cashadvance;
    private javax.swing.JButton Button_employee;
    private javax.swing.JButton Button_overtimeeligible;
    private javax.swing.JButton Button_payfactor;
    private javax.swing.JButton Button_payslip;
    private javax.swing.JButton Button_piecework;
    private javax.swing.JButton Button_regular;
    private javax.swing.JLabel Employeestotal;
    private javax.swing.JPanel Panel_Count;
    private javax.swing.JLabel Piecework_total;
    private javax.swing.JLabel Regular_total;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JLabel lbl_Piecework;
    private javax.swing.JLabel lbl_Regular;
    // End of variables declaration//GEN-END:variables
}
