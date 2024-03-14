import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author keo
 */
public class PayFactors extends javax.swing.JFrame {

    /**
     * Creates new form PayFactors
     */
    public PayFactors() {
        initComponents();
        loadCurrentRates();
        loadDeductionRates();
        initButtonListeners();
    }

    private void loadCurrentRates() {
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Size, Rate FROM PackType WHERE Size IN ('Small', 'Medium', 'Large')")) {

            while (rs.next()) {
                String size = rs.getString("Size");
                float rate = rs.getFloat("Rate");

                switch (size) {
                    case "Small":
                        TField_small.setText(String.valueOf(rate));
                        break;
                    case "Medium":
                        TField_medium.setText(String.valueOf(rate));
                        break;
                    case "Large":
                        TField_large.setText(String.valueOf(rate));
                        break;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading piecework rates.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRates() {
        String updateQuery = "UPDATE PackType SET Rate = ? WHERE Size = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            // Update Small size rate
            pstmt.setFloat(1, Float.parseFloat(TField_small.getText()));
            pstmt.setString(2, "Small");
            pstmt.executeUpdate();

            // Update Medium size rate
            pstmt.setFloat(1, Float.parseFloat(TField_medium.getText()));
            pstmt.setString(2, "Medium");
            pstmt.executeUpdate();

            // Update Large size rate
            pstmt.setFloat(1, Float.parseFloat(TField_large.getText()));
            pstmt.setString(2, "Large");
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Rates updated successfully.", "Update Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating rates.", "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for rates.", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loadDeductionRates() {
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Deduction_Type, Amount FROM Deduction")) {

            while (rs.next()) {
                String type = rs.getString("Deduction_Type");
                float amount = rs.getFloat("Amount");

                switch (type) {
                    case "Pag-Ibig":
                        TField_pagibig.setText(String.valueOf(amount));
                        break;
                    case "Philhealth":
                        TField_philhealth.setText(String.valueOf(amount));
                        break;
                    case "SSS":
                        TField_sss.setText(String.valueOf(amount));
                        break;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading deduction rates.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void updateDeductionRates() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            updateDeductionRate(conn, "Pag-Ibig", Float.parseFloat(TField_pagibig.getText()));
            updateDeductionRate(conn, "Philhealth", Float.parseFloat(TField_philhealth.getText()));
            updateDeductionRate(conn, "SSS", Float.parseFloat(TField_sss.getText()));

            JOptionPane.showMessageDialog(this, "Deduction rates updated successfully.", "Update Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating deduction rates.", "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for deduction rates.", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateDeductionRate(Connection conn, String type, float amount) throws SQLException {
        String updateQuery = "UPDATE Deduction SET Amount = ? WHERE Deduction_Type = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
            pstmt.setFloat(1, amount);
            pstmt.setString(2, type);
            pstmt.executeUpdate();
        }
    }

    private void initButtonListeners() {
        Button_update2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDeductionRates();
            }
        });
        // Initialize other listeners
    }


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
        jLabel2 = new JLabel();
        jLabel12 = new JLabel();
        jLabel13 = new JLabel();
        jLabel14 = new JLabel();
        jLabel15 = new JLabel();
        jLabel16 = new JLabel();
        TField_SNWH = new JTextField();
        TField_RH = new JTextField();
        TField_OSNWH = new JTextField();
        TField_ORH = new JTextField();
        Button_update1 = new JButton();
        Button_cancel1 = new JButton();
        jPanel2 = new JPanel();
        jLabel1 = new JLabel();
        jLabel4 = new JLabel();
        jLabel5 = new JLabel();
        jLabel6 = new JLabel();
        jLabel7 = new JLabel();
        jLabel8 = new JLabel();
        jLabel9 = new JLabel();
        jLabel10 = new JLabel();
        jLabel11 = new JLabel();
        TField_8hr = new JTextField();
        TField_hrrate = new JTextField();
        TField_Overtimerate = new JTextField();
        TField_small = new JTextField();
        TField_medium = new JTextField();
        TField_large = new JTextField();
        Button_update = new JButton();
        Button_cancel = new JButton();
        jPanel3 = new JPanel();
        jLabel3 = new JLabel();
        jLabel17 = new JLabel();
        jLabel18 = new JLabel();
        jLabel19 = new JLabel();
        jLabel20 = new JLabel();
        TField_pagibig = new JTextField();
        TField_philhealth = new JTextField();
        TField_sss = new JTextField();
        Button_update2 = new JButton();
        Button_cancel2 = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var contentPane = getContentPane();

        //---- Button_back ----
        Button_back.setText("<Back");
        Button_back.addActionListener(e -> Button_backActionPerformed(e));

        //======== jPanel1 ========
        {
            jPanel1.setBackground(new Color(0x003333));
            jPanel1.setBorder ( new javax . swing. border .CompoundBorder ( new javax . swing. border .TitledBorder ( new javax . swing. border .EmptyBorder
            ( 0, 0 ,0 , 0) ,  "JF\u006frmDesi\u0067ner Ev\u0061luatio\u006e" , javax. swing .border . TitledBorder. CENTER ,javax . swing. border
            .TitledBorder . BOTTOM, new java. awt .Font ( "Dialo\u0067", java .awt . Font. BOLD ,12 ) ,java . awt
            . Color .red ) ,jPanel1. getBorder () ) ); jPanel1. addPropertyChangeListener( new java. beans .PropertyChangeListener ( ){ @Override public void
            propertyChange (java . beans. PropertyChangeEvent e) { if( "borde\u0072" .equals ( e. getPropertyName () ) )throw new RuntimeException( )
            ;} } );

            //---- jLabel2 ----
            jLabel2.setFont(new Font("Segoe UI", Font.BOLD, 24));
            jLabel2.setText("Additional Pay");

            //---- jLabel12 ----
            jLabel12.setFont(new Font("Segoe UI", Font.BOLD, 14));
            jLabel12.setText("Regular");

            //---- jLabel13 ----
            jLabel13.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            jLabel13.setText("Special Non-Working Holiday:");

            //---- jLabel14 ----
            jLabel14.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            jLabel14.setText("Regular Holiday:");

            //---- jLabel15 ----
            jLabel15.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            jLabel15.setText("Overtime SNW Holiday:");

            //---- jLabel16 ----
            jLabel16.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            jLabel16.setText("Overtime Regular Holiday:");

            //---- TField_SNWH ----
            TField_SNWH.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- TField_RH ----
            TField_RH.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- TField_OSNWH ----
            TField_OSNWH.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- TField_ORH ----
            TField_ORH.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- Button_update1 ----
            Button_update1.setText("Update");

            //---- Button_cancel1 ----
            Button_cancel1.setText("Cancel");

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel1Layout.createParallelGroup()
                            .addComponent(jLabel13)
                            .addComponent(jLabel12)
                            .addComponent(jLabel14, GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel15, GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel16, GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(TField_OSNWH, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                            .addComponent(TField_RH, GroupLayout.Alignment.LEADING)
                            .addComponent(TField_SNWH)
                            .addComponent(TField_ORH))
                        .addGap(35, 35, 35))
                    .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addGap(73, 73, 73))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addComponent(Button_update1)
                        .addGap(18, 18, 18)
                        .addComponent(Button_cancel1)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup()
                            .addComponent(jLabel13)
                            .addComponent(TField_SNWH, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup()
                            .addComponent(jLabel14)
                            .addComponent(TField_RH, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup()
                            .addComponent(jLabel15)
                            .addComponent(TField_OSNWH, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup()
                            .addComponent(jLabel16)
                            .addComponent(TField_ORH, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(Button_update1)
                            .addComponent(Button_cancel1))
                        .addGap(34, 34, 34))
            );
        }

        //======== jPanel2 ========
        {
            jPanel2.setBackground(new Color(0x003333));

            //---- jLabel1 ----
            jLabel1.setFont(new Font("Segoe UI", Font.BOLD, 24));
            jLabel1.setText("Rates");

            //---- jLabel4 ----
            jLabel4.setFont(new Font("Segoe UI", Font.BOLD, 14));
            jLabel4.setText("Regular");

            //---- jLabel5 ----
            jLabel5.setFont(new Font("Segoe UI", Font.BOLD, 14));
            jLabel5.setText("Piecework");

            //---- jLabel6 ----
            jLabel6.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            jLabel6.setText("8 hour work:");

            //---- jLabel7 ----
            jLabel7.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            jLabel7.setText("Hourly rate:");

            //---- jLabel8 ----
            jLabel8.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            jLabel8.setText("Overtime rate:");

            //---- jLabel9 ----
            jLabel9.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            jLabel9.setText("Small:");

            //---- jLabel10 ----
            jLabel10.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            jLabel10.setText("Medium:");

            //---- jLabel11 ----
            jLabel11.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            jLabel11.setText("Large:");

            //---- TField_8hr ----
            TField_8hr.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- TField_hrrate ----
            TField_hrrate.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- TField_Overtimerate ----
            TField_Overtimerate.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- TField_small ----
            TField_small.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            TField_small.addActionListener(e -> TField_smallActionPerformed(e));

            //---- TField_medium ----
            TField_medium.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            TField_medium.addActionListener(e -> TField_mediumActionPerformed(e));

            //---- TField_large ----
            TField_large.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            TField_large.addActionListener(e -> TField_largeActionPerformed(e));

            //---- Button_update ----
            Button_update.setText("Update");

            //---- Button_cancel ----
            Button_cancel.setText("Cancel");
            Button_cancel.addActionListener(e -> Button_cancelActionPerformed(e));

            GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(94, 94, 94))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jPanel2Layout.createParallelGroup()
                            .addComponent(jLabel4)
                            .addComponent(jLabel8)
                            .addComponent(jLabel7, GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel9, GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10, GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel11, GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup()
                            .addComponent(TField_8hr)
                            .addComponent(TField_hrrate)
                            .addComponent(TField_Overtimerate)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(TField_large, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(TField_medium)
                            .addComponent(TField_small))
                        .addGap(54, 54, 54))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(Button_update)
                        .addGap(18, 18, 18)
                        .addComponent(Button_cancel)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup()
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(12, 12, 12)
                        .addComponent(jLabel4)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup()
                            .addComponent(jLabel6)
                            .addComponent(TField_8hr, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup()
                            .addComponent(jLabel7)
                            .addComponent(TField_hrrate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup()
                            .addComponent(jLabel8)
                            .addComponent(TField_Overtimerate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addComponent(jLabel5)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(TField_small, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(TField_medium, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addGroup(jPanel2Layout.createParallelGroup()
                            .addComponent(jLabel11)
                            .addComponent(TField_large, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(38, 38, 38)
                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(Button_update)
                            .addComponent(Button_cancel))
                        .addContainerGap(32, Short.MAX_VALUE))
            );
        }

        //======== jPanel3 ========
        {
            jPanel3.setBackground(new Color(0x003333));

            //---- jLabel3 ----
            jLabel3.setFont(new Font("Segoe UI", Font.BOLD, 24));
            jLabel3.setText("Deduction");

            //---- jLabel17 ----
            jLabel17.setFont(new Font("Segoe UI", Font.BOLD, 14));
            jLabel17.setText("Regular & Piecework");

            //---- jLabel18 ----
            jLabel18.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            jLabel18.setText("Pag-Ibig:");

            //---- jLabel19 ----
            jLabel19.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            jLabel19.setText("Philhealth:");

            //---- jLabel20 ----
            jLabel20.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            jLabel20.setText("SSS:");

            //---- TField_pagibig ----
            TField_pagibig.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- TField_philhealth ----
            TField_philhealth.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- TField_sss ----
            TField_sss.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            //---- Button_update2 ----
            Button_update2.setText("Update");

            //---- Button_cancel2 ----
            Button_cancel2.setText("Cancel");

            GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
            jPanel3.setLayout(jPanel3Layout);
            jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addGap(66, 66, 66))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel3Layout.createParallelGroup()
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel18)
                                .addGap(18, 18, 18)
                                .addComponent(TField_pagibig))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel20)
                                    .addComponent(jLabel19))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup()
                                    .addComponent(TField_philhealth)
                                    .addComponent(TField_sss)))
                            .addComponent(jLabel17)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(Button_update2)
                                .addGap(18, 18, 18)
                                .addComponent(Button_cancel2)))
                        .addGap(52, 52, 52))
            );
            jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup()
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel17)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup()
                            .addComponent(jLabel18)
                            .addComponent(TField_pagibig, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup()
                            .addComponent(jLabel19)
                            .addComponent(TField_philhealth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup()
                            .addComponent(jLabel20)
                            .addComponent(TField_sss, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(199, 199, 199)
                        .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(Button_update2)
                            .addComponent(Button_cancel2))
                        .addGap(0, 0, Short.MAX_VALUE))
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
                    .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(Button_back)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        pack();
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void Button_backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_backActionPerformed
        InstructionsKt.redirectToDashboard(this);
    }//GEN-LAST:event_Button_backActionPerformed

    private void TField_smallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TField_smallActionPerformed
        InstructionsKt.retrieveAndSetSmallSizeRate();
    }//GEN-LAST:event_TField_smallActionPerformed

    private void TField_mediumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TField_mediumActionPerformed
        InstructionsKt.retrieveAndSetMediumSizeRate();
    }//GEN-LAST:event_TField_mediumActionPerformed

    private void TField_largeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TField_largeActionPerformed
        InstructionsKt.retrieveAndSetLargeSizeRate();
    }//GEN-LAST:event_TField_largeActionPerformed

    private void Button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_cancelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Button_cancelActionPerformed

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
            java.util.logging.Logger.getLogger(PayFactors.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PayFactors.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PayFactors.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PayFactors.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PayFactors().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Zevoex
    private JButton Button_back;
    private JPanel jPanel1;
    private JLabel jLabel2;
    private JLabel jLabel12;
    private JLabel jLabel13;
    private JLabel jLabel14;
    private JLabel jLabel15;
    private JLabel jLabel16;
    private JTextField TField_SNWH;
    private JTextField TField_RH;
    private JTextField TField_OSNWH;
    private JTextField TField_ORH;
    private JButton Button_update1;
    private JButton Button_cancel1;
    private JPanel jPanel2;
    private JLabel jLabel1;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JLabel jLabel9;
    private JLabel jLabel10;
    private JLabel jLabel11;
    private JTextField TField_8hr;
    private JTextField TField_hrrate;
    private JTextField TField_Overtimerate;
    private JTextField TField_small;
    private JTextField TField_medium;
    private JTextField TField_large;
    private JButton Button_update;
    private JButton Button_cancel;
    private JPanel jPanel3;
    private JLabel jLabel3;
    private JLabel jLabel17;
    private JLabel jLabel18;
    private JLabel jLabel19;
    private JLabel jLabel20;
    private JTextField TField_pagibig;
    private JTextField TField_philhealth;
    private JTextField TField_sss;
    private JButton Button_update2;
    private JButton Button_cancel2;
    // End of variables declaration//GEN-END:variables
}
