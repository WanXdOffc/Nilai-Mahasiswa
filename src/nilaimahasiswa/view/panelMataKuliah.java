/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package nilaimahasiswa.view;

import java.util.List;
import nilaimahasiswa.controller.CourseController;
import nilaimahasiswa.model.Course;

public class panelMataKuliah extends javax.swing.JPanel {

    private CourseController controller;
    private int currentPage = 1;
    private String selectedCode = "";
    private boolean editMode = false;
    private boolean searchMode = false;

    public panelMataKuliah() {
        initComponents();
        try {
            controller = new CourseController();
            CourseController.DATA_PER_HALAMAN = 7;
            initialize();
        } catch (Exception e) {
            showError("Failed to connect to database: " + e.getMessage());
        }
    }

    // ============================================================
    // INITIALIZATION
    // ============================================================

    private void initialize() throws Exception {
        setupTable();
        setupTableListener();
        setupSearchListener();
        setupPlaceholders();
        loadData();
    }

    private void setupTable() {
        String[] columns = {"Code", "Course Name", "Credits (SKS)", "Semester"};
        javax.swing.table.DefaultTableModel model =
            new javax.swing.table.DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };
        jTableDataMatkul.setModel(model);
        jTableDataMatkul.setSelectionMode(
            javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableDataMatkul.getTableHeader().setReorderingAllowed(false);
        jTableDataMatkul.setRowHeight(28);

        jTableDataMatkul.getColumnModel().getColumn(0).setPreferredWidth(100);
        jTableDataMatkul.getColumnModel().getColumn(1).setPreferredWidth(220);
        jTableDataMatkul.getColumnModel().getColumn(2).setPreferredWidth(120);
        jTableDataMatkul.getColumnModel().getColumn(3).setPreferredWidth(100);

        applyRowRenderer();
    }

    private void applyRowRenderer() {
        jTableDataMatkul.setDefaultRenderer(Object.class,
            new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public java.awt.Component getTableCellRendererComponent(
                    javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                    super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);
                    if (!isSelected) {
                        setBackground(row % 2 == 0
                            ? java.awt.Color.WHITE
                            : new java.awt.Color(245, 247, 250));
                    }
                    setBorder(javax.swing.BorderFactory
                        .createEmptyBorder(0, 8, 0, 8));
                    return this;
                }
            });
    }

    private void setupTableListener() {
        jTableDataMatkul.getSelectionModel()
            .addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    fillFormFromTable();
                }
            });
    }

    private void setupSearchListener() {
        jTextFieldSearch.getDocument().addDocumentListener(
            new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    runSearch();
                }
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    runSearch();
                }
                public void changedUpdate(javax.swing.event.DocumentEvent e) {}
            }
        );
    }

    private void setupPlaceholders() {
        jTextFieldCodeMatkul.putClientProperty(
            "JTextField.placeholderText", "e.g: MK001");
        jTextFieldName.putClientProperty(
            "JTextField.placeholderText", "e.g: Object Oriented Programming");
        jTextFieldSKS.putClientProperty(
            "JTextField.placeholderText", "e.g: 3");
        jTextFieldSemester.putClientProperty(
            "JTextField.placeholderText", "e.g: 2");
        jTextFieldSearch.putClientProperty(
            "JTextField.placeholderText", "Search by code or name...");
    }

    // ============================================================
    // LOAD DATA
    // ============================================================

    private void loadData() throws Exception {
        javax.swing.table.DefaultTableModel model =
            (javax.swing.table.DefaultTableModel) jTableDataMatkul.getModel();
        model.setRowCount(0);

        List<Course> list = controller.getHalaman(currentPage);
        for (Course c : list) {
            model.addRow(new Object[]{
                c.getCode(),
                c.getCourseName(),
                c.getSKS(),
                c.getSemester()
            });
        }
        updatePageInfo();
    }

    private void updatePageInfo() throws Exception {
        int total     = controller.getTotalData();
        int totalPage = controller.getTotalHalaman();

        jLabelTotal.setText("Total: " + total + " data");
        jLabelPage.setText("Page " + currentPage + " of " + totalPage);

        jButtonNext.setEnabled(!searchMode && currentPage < totalPage);
        jButtonBack.setEnabled(!searchMode && currentPage > 1);
    }

    // ============================================================
    // FORM
    // ============================================================

    private void fillFormFromTable() {
        int row = jTableDataMatkul.getSelectedRow();
        if (row < 0) return;

        selectedCode = jTableDataMatkul.getValueAt(row, 0).toString();
        jTextFieldCodeMatkul.setText(selectedCode);
        jTextFieldName.setText(
            jTableDataMatkul.getValueAt(row, 1).toString());
        jTextFieldSKS.setText(
            jTableDataMatkul.getValueAt(row, 2).toString());
        jTextFieldSemester.setText(
            jTableDataMatkul.getValueAt(row, 3).toString());

        jTextFieldCodeMatkul.setEditable(false);
        editMode = true;
    }

    private void resetForm() {
        jTextFieldCodeMatkul.setText("");
        jTextFieldName.setText("");
        jTextFieldSKS.setText("");
        jTextFieldSemester.setText("");
        jTextFieldCodeMatkul.setEditable(true);
        selectedCode = "";
        editMode     = false;
        searchMode   = false;
        jTableDataMatkul.clearSelection();
    }
    
    public void clearSelection() {
        resetForm();
    }

    // ============================================================
    // CRUD
    // ============================================================

    private void save() {
        String code = jTextFieldCodeMatkul.getText().trim();
        String name = jTextFieldName.getText().trim();

        // Validate SKS and Semester are numbers
        int sks, semester;
        try {
            sks      = Integer.parseInt(jTextFieldSKS.getText().trim());
            semester = Integer.parseInt(jTextFieldSemester.getText().trim());
        } catch (NumberFormatException e) {
            showError("SKS and Semester must be numbers!");
            return;
        }

        try {
            if (editMode) {
                controller.update(code, name, sks, semester);
                showSuccess("Course data updated successfully!");
            } else {
                controller.tambah(code, name, sks, semester);
                showSuccess("Course data saved successfully!");
            }
            resetForm();
            currentPage = 1;
            loadData();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void delete() {
        if (selectedCode.isEmpty()) {
            showError("Please select a course to delete!");
            return;
        }

        int confirm = javax.swing.JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete:\n" +
            "Code : " + selectedCode + "\n" +
            "Name : " + jTextFieldName.getText(),
            "Confirm Delete",
            javax.swing.JOptionPane.YES_NO_OPTION,
            javax.swing.JOptionPane.WARNING_MESSAGE
        );

        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            try {
                controller.hapus(selectedCode);
                showSuccess("Course deleted successfully!");
                resetForm();
                currentPage = 1;
                loadData();
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }
    }

    // ============================================================
    // SEARCH
    // ============================================================

    private void runSearch() {
        String keyword = jTextFieldSearch.getText().trim();

        try {
            javax.swing.table.DefaultTableModel model =
                (javax.swing.table.DefaultTableModel) jTableDataMatkul.getModel();
            model.setRowCount(0);

            if (keyword.isEmpty()) {
                searchMode  = false;
                currentPage = 1;
                loadData();
                return;
            }

            searchMode = true;
            List<Course> results = controller.cari(keyword);

            for (Course c : results) {
                model.addRow(new Object[]{
                    c.getCode(),
                    c.getCourseName(),
                    c.getSKS(),
                    c.getSemester()
                });
            }

            jLabelTotal.setText("Found: " + results.size() + " data");
            jLabelPage.setText("Search mode");
            jButtonNext.setEnabled(false);
            jButtonBack.setEnabled(false);

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    // ============================================================
    // PAGINATION
    // ============================================================

    private void nextPage() {
        try {
            currentPage++;
            loadData();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void prevPage() {
        try {
            currentPage--;
            loadData();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private void showSuccess(String message) {
        javax.swing.JOptionPane.showMessageDialog(
            this, message, "Success",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        javax.swing.JOptionPane.showMessageDialog(
            this, message, "Error",
            javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanelFormDataMahasiswa = new javax.swing.JPanel();
        jPanelFormDataMahasiswa.putClientProperty("component.arc", 16);
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldCodeMatkul = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jButtonSave = new javax.swing.JButton();
        jTextFieldSKS = new javax.swing.JTextField();
        jTextFieldName = new javax.swing.JTextField();
        jTextFieldSemester = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jButtonDelete = new javax.swing.JButton();
        jPanelTabel1 = new javax.swing.JPanel();
        jPanelFormDataMahasiswa.putClientProperty("component.arc", 16);
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableDataMatkul = new javax.swing.JTable();
        jLabelPage = new javax.swing.JLabel();
        jButtonNext = new javax.swing.JButton();
        jButtonBack = new javax.swing.JButton();
        jTextFieldSearch = new javax.swing.JTextField();
        jLabelTotal = new javax.swing.JLabel();

        setBackground(new java.awt.Color(245, 245, 245));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Lucida Console", 1, 24)); // NOI18N
        jLabel1.setText("Data Mata Kuliah");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Kelola kode, nama, dan SKS mata kuliah");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, -1));

        jPanelFormDataMahasiswa.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Form Data Mata kuliah");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Kode");

        jTextFieldCodeMatkul.setBackground(new java.awt.Color(0, 51, 153));
        jTextFieldCodeMatkul.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldCodeMatkul.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 4, true));
        jTextFieldCodeMatkul.addActionListener(this::jTextFieldCodeMatkulActionPerformed);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Semester");

        jButtonSave.setBackground(new java.awt.Color(0, 102, 204));
        jButtonSave.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonSave.setText("Simpan");
        jButtonSave.addActionListener(this::jButtonSaveActionPerformed);

        jTextFieldSKS.setBackground(new java.awt.Color(0, 51, 153));
        jTextFieldSKS.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldSKS.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 4, true));
        jTextFieldSKS.addActionListener(this::jTextFieldSKSActionPerformed);

        jTextFieldName.setBackground(new java.awt.Color(0, 51, 153));
        jTextFieldName.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldName.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 4, true));
        jTextFieldName.addActionListener(this::jTextFieldNameActionPerformed);

        jTextFieldSemester.setBackground(new java.awt.Color(0, 51, 153));
        jTextFieldSemester.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldSemester.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 4, true));
        jTextFieldSemester.addActionListener(this::jTextFieldSemesterActionPerformed);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("SKS");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Nama");

        jButtonDelete.setText("Delete");
        jButtonDelete.addActionListener(this::jButtonDeleteActionPerformed);

        javax.swing.GroupLayout jPanelFormDataMahasiswaLayout = new javax.swing.GroupLayout(jPanelFormDataMahasiswa);
        jPanelFormDataMahasiswa.setLayout(jPanelFormDataMahasiswaLayout);
        jPanelFormDataMahasiswaLayout.setHorizontalGroup(
            jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFormDataMahasiswaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelFormDataMahasiswaLayout.createSequentialGroup()
                        .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jTextFieldSKS, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                                .addComponent(jTextFieldCodeMatkul))
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTextFieldName, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                                .addComponent(jTextFieldSemester)))
                        .addGap(18, 18, 18))
                    .addGroup(jPanelFormDataMahasiswaLayout.createSequentialGroup()
                        .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel3)
                            .addGroup(jPanelFormDataMahasiswaLayout.createSequentialGroup()
                                .addComponent(jButtonSave, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonDelete))
                            .addGroup(jPanelFormDataMahasiswaLayout.createSequentialGroup()
                                .addGap(339, 339, 339)
                                .addComponent(jLabel10)))
                        .addContainerGap(275, Short.MAX_VALUE))))
        );
        jPanelFormDataMahasiswaLayout.setVerticalGroup(
            jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFormDataMahasiswaLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelFormDataMahasiswaLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldCodeMatkul, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelFormDataMahasiswaLayout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(21, 21, 21)
                .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldSKS, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldSemester, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonSave, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(29, 29, 29))
        );

        add(jPanelFormDataMahasiswa, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 670, 260));

        jPanelTabel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setBackground(new java.awt.Color(0, 0, 0));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Data Mata Kuliah");

        jTableDataMatkul.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Code", "Nama", "SKS", "Semester"
            }
        ));
        jScrollPane1.setViewportView(jTableDataMatkul);

        jLabelPage.setText("1 - 10");

        jButtonNext.setText("Next");
        jButtonNext.addActionListener(this::jButtonNextActionPerformed);

        jButtonBack.setText("Back");
        jButtonBack.addActionListener(this::jButtonBackActionPerformed);

        javax.swing.GroupLayout jPanelTabel1Layout = new javax.swing.GroupLayout(jPanelTabel1);
        jPanelTabel1.setLayout(jPanelTabel1Layout);
        jPanelTabel1Layout.setHorizontalGroup(
            jPanelTabel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTabel1Layout.createSequentialGroup()
                .addGroup(jPanelTabel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTabel1Layout.createSequentialGroup()
                        .addGap(231, 231, 231)
                        .addComponent(jButtonBack)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelPage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonNext))
                    .addGroup(jPanelTabel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanelTabel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 631, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanelTabel1Layout.setVerticalGroup(
            jPanelTabel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTabel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelTabel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelPage)
                    .addComponent(jButtonNext)
                    .addComponent(jButtonBack))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanelTabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 380, 670, -1));

        jTextFieldSearch.addActionListener(this::jTextFieldSearchActionPerformed);
        add(jTextFieldSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 340, 570, 30));

        jLabelTotal.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabelTotal.setText("Total : 120");
        add(jLabelTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 340, -1, 30));
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldCodeMatkulActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldCodeMatkulActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldCodeMatkulActionPerformed

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        save();
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jTextFieldSKSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSKSActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldSKSActionPerformed

    private void jTextFieldNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldNameActionPerformed

    private void jTextFieldSemesterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSemesterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldSemesterActionPerformed

    private void jTextFieldSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldSearchActionPerformed

    private void jButtonNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextActionPerformed
        nextPage();
    }//GEN-LAST:event_jButtonNextActionPerformed

    private void jButtonBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBackActionPerformed
        prevPage();
    }//GEN-LAST:event_jButtonBackActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        delete();
    }//GEN-LAST:event_jButtonDeleteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBack;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonNext;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabelPage;
    private javax.swing.JLabel jLabelTotal;
    private javax.swing.JPanel jPanelFormDataMahasiswa;
    private javax.swing.JPanel jPanelTabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableDataMatkul;
    private javax.swing.JTextField jTextFieldCodeMatkul;
    private javax.swing.JTextField jTextFieldName;
    private javax.swing.JTextField jTextFieldSKS;
    private javax.swing.JTextField jTextFieldSearch;
    private javax.swing.JTextField jTextFieldSemester;
    // End of variables declaration//GEN-END:variables
}
