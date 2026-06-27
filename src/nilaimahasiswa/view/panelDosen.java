/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package nilaimahasiswa.view;

import java.util.List;
import nilaimahasiswa.controller.LecturerController;
import nilaimahasiswa.model.Lecturer;

public class panelDosen extends javax.swing.JPanel {

    private LecturerController controller;
    private int halamanSaatIni = 1;
    private String nidnDipilih = "";
    private boolean modeEdit = false;
    private boolean sedangCari = false;

    public panelDosen() {
        initComponents();
        try {
            controller = new LecturerController();
            LecturerController.DATA_PER_HALAMAN = 7;
            inisialisasi();
        } catch (Exception e) {
            tampilError("Gagal koneksi database: " + e.getMessage());
        }
    }

    // ============================================================
    // SETUP
    // ============================================================

    private void inisialisasi() throws Exception {
        setupKolomTabel();
        setupListenerTabel();
        setupSearchListener();
        setupPlaceholder();
        muatData();
    }

    private void setupKolomTabel() {
        String[] kolom = {"NIDN", "Nama", "Keahlian", "No HP"};
        javax.swing.table.DefaultTableModel model =
            new javax.swing.table.DefaultTableModel(kolom, 0) {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };
        jTableDataDosen.setModel(model);
        jTableDataDosen.setSelectionMode(
            javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableDataDosen.getTableHeader().setReorderingAllowed(false);
        jTableDataDosen.setRowHeight(28);

        jTableDataDosen.getColumnModel().getColumn(0).setPreferredWidth(120);
        jTableDataDosen.getColumnModel().getColumn(1).setPreferredWidth(200);
        jTableDataDosen.getColumnModel().getColumn(2).setPreferredWidth(180);
        jTableDataDosen.getColumnModel().getColumn(3).setPreferredWidth(130);

        // Warna baris selang-seling
        jTableDataDosen.setDefaultRenderer(Object.class,
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

    private void setupListenerTabel() {
        jTableDataDosen.getSelectionModel()
            .addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    isiFormDariTabel();
                }
            });
    }

    private void setupSearchListener() {
        jTextFieldSearch.getDocument().addDocumentListener(
            new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    jalankanCari();
                }
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    jalankanCari();
                }
                public void changedUpdate(javax.swing.event.DocumentEvent e) {}
            }
        );
    }

    private void setupPlaceholder() {
        jTextFieldNIDN.putClientProperty(
            "JTextField.placeholderText", "cth: 0011223344");
        jTextFieldNama.putClientProperty(
            "JTextField.placeholderText", "cth: Dr. Budi Santoso");
        jTextFieldMatkul.putClientProperty(
            "JTextField.placeholderText", "cth: Pemrograman Web");
        jTextFieldNoHP.putClientProperty(
            "JTextField.placeholderText", "cth: 081234567890");
        jTextFieldSearch.putClientProperty(
            "JTextField.placeholderText", "Cari berdasarkan NIDN atau Nama...");
    }

    // ============================================================
    // MUAT DATA
    // ============================================================

    private void muatData() throws Exception {
        javax.swing.table.DefaultTableModel model =
            (javax.swing.table.DefaultTableModel) jTableDataDosen.getModel();
        model.setRowCount(0);

        List<Lecturer> list = controller.getHalaman(halamanSaatIni);
        for (Lecturer l : list) {
            model.addRow(new Object[]{
                l.getNidn(), l.getName(),
                l.getExpertise(), l.getNoHp()
            });
        }
        updateLabelInfo();
    }

    private void updateLabelInfo() throws Exception {
        int total    = controller.getTotalData();
        int totalHal = controller.getTotalHalaman();

        jLabelTotal.setText("Total : " + total + " data");
        jLabelHalaman.setText(
            "Page " + halamanSaatIni + " of " + totalHal);

        jButtonNext.setEnabled(!sedangCari && halamanSaatIni < totalHal);
        jButtonBack.setEnabled(!sedangCari && halamanSaatIni > 1);
    }

    // ============================================================
    // FORM
    // ============================================================

    private void isiFormDariTabel() {
        int baris = jTableDataDosen.getSelectedRow();
        if (baris < 0) return;

        nidnDipilih = jTableDataDosen.getValueAt(baris, 0).toString();
        jTextFieldNIDN.setText(nidnDipilih);
        jTextFieldNama.setText(
            jTableDataDosen.getValueAt(baris, 1).toString());
        jTextFieldMatkul.setText(
            jTableDataDosen.getValueAt(baris, 2).toString());
        jTextFieldNoHP.setText(
            jTableDataDosen.getValueAt(baris, 3).toString());

        jTextFieldNIDN.setEditable(false);
        modeEdit = true;
    }

    private void resetForm() {
        jTextFieldNIDN.setText("");
        jTextFieldNama.setText("");
        jTextFieldMatkul.setText("");
        jTextFieldNoHP.setText("");
        jTextFieldNIDN.setEditable(true);
        nidnDipilih  = "";
        modeEdit     = false;
        sedangCari   = false;
        jTableDataDosen.clearSelection();
    }
    
    public void clearSelection() {
        resetForm();
    }

    // ============================================================
    // CRUD
    // ============================================================

    private void simpan() {
        String nidn     = jTextFieldNIDN.getText().trim();
        String nama     = jTextFieldNama.getText().trim();
        String keahlian = jTextFieldMatkul.getText().trim();
        String noHp     = jTextFieldNoHP.getText().trim();

        try {
            if (modeEdit) {
                controller.update(nidn, nama, keahlian, noHp);
                tampilPesan("Data dosen berhasil diupdate!");
            } else {
                controller.tambah(nidn, nama, keahlian, noHp);
                tampilPesan("Data dosen berhasil disimpan!");
            }
            resetForm();
            halamanSaatIni = 1;
            muatData();
        } catch (Exception e) {
            tampilError(e.getMessage());
        }
    }

    private void hapus() {
        if (nidnDipilih.isEmpty()) {
            tampilError("Pilih dosen yang akan dihapus!");
            return;
        }

        int konfirm = javax.swing.JOptionPane.showConfirmDialog(
            this,
            "Yakin hapus dosen:\n" +
            "NIDN : " + nidnDipilih + "\n" +
            "Nama : " + jTextFieldNama.getText(),
            "Konfirmasi Hapus",
            javax.swing.JOptionPane.YES_NO_OPTION,
            javax.swing.JOptionPane.WARNING_MESSAGE
        );

        if (konfirm == javax.swing.JOptionPane.YES_OPTION) {
            try {
                controller.hapus(nidnDipilih);
                tampilPesan("Dosen berhasil dihapus!");
                resetForm();
                halamanSaatIni = 1;
                muatData();
            } catch (Exception e) {
                tampilError(e.getMessage());
            }
        }
    }

    // ============================================================
    // PENCARIAN
    // ============================================================

    private void jalankanCari() {
        String keyword = jTextFieldSearch.getText().trim();

        try {
            javax.swing.table.DefaultTableModel model =
                (javax.swing.table.DefaultTableModel) jTableDataDosen.getModel();
            model.setRowCount(0);

            if (keyword.isEmpty()) {
                sedangCari     = false;
                halamanSaatIni = 1;
                muatData();
                return;
            }

            sedangCari = true;
            List<Lecturer> hasil = controller.cari(keyword);

            for (Lecturer l : hasil) {
                model.addRow(new Object[]{
                    l.getNidn(), l.getName(),
                    l.getExpertise(), l.getNoHp()
                });
            }

            jLabelTotal.setText("Found : " + hasil.size() + " data");
            jLabelHalaman.setText("Mode pencarian");
            jButtonNext.setEnabled(false);
            jButtonBack.setEnabled(false);

        } catch (Exception e) {
            tampilError(e.getMessage());
        }
    }

    // ============================================================
    // PAGINATION
    // ============================================================

    private void nextHalaman() {
        try {
            halamanSaatIni++;
            muatData();
        } catch (Exception e) {
            tampilError(e.getMessage());
        }
    }

    private void backHalaman() {
        try {
            halamanSaatIni--;
            muatData();
        } catch (Exception e) {
            tampilError(e.getMessage());
        }
    }

    // ============================================================
    // HELPER
    // ============================================================

    private void tampilPesan(String pesan) {
        javax.swing.JOptionPane.showMessageDialog(
            this, pesan, "Sukses",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    private void tampilError(String pesan) {
        javax.swing.JOptionPane.showMessageDialog(
            this, pesan, "Error",
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
        jTextFieldNIDN = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jButtonSave = new javax.swing.JButton();
        jTextFieldMatkul = new javax.swing.JTextField();
        jTextFieldNama = new javax.swing.JTextField();
        jTextFieldNoHP = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jButtonDelete = new javax.swing.JButton();
        jPanelTabel1 = new javax.swing.JPanel();
        jPanelFormDataMahasiswa.putClientProperty("component.arc", 16);
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableDataDosen = new javax.swing.JTable();
        jLabelHalaman = new javax.swing.JLabel();
        jButtonNext = new javax.swing.JButton();
        jButtonBack = new javax.swing.JButton();
        jTextFieldSearch = new javax.swing.JTextField();
        jLabelTotal = new javax.swing.JLabel();

        setBackground(new java.awt.Color(245, 245, 245));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Lucida Console", 1, 24)); // NOI18N
        jLabel1.setText("Data Dosen");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Kelola data dosen pengampu mata kuliah");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, -1));

        jPanelFormDataMahasiswa.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Form Data Dosen");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("NIDN");

        jTextFieldNIDN.setBackground(new java.awt.Color(0, 51, 153));
        jTextFieldNIDN.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldNIDN.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 4, true));
        jTextFieldNIDN.addActionListener(this::jTextFieldNIDNActionPerformed);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Nomer HP");

        jButtonSave.setBackground(new java.awt.Color(0, 102, 204));
        jButtonSave.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonSave.setText("Simpan");
        jButtonSave.addActionListener(this::jButtonSaveActionPerformed);

        jTextFieldMatkul.setBackground(new java.awt.Color(0, 51, 153));
        jTextFieldMatkul.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldMatkul.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 4, true));
        jTextFieldMatkul.addActionListener(this::jTextFieldMatkulActionPerformed);

        jTextFieldNama.setBackground(new java.awt.Color(0, 51, 153));
        jTextFieldNama.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldNama.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 4, true));
        jTextFieldNama.addActionListener(this::jTextFieldNamaActionPerformed);

        jTextFieldNoHP.setBackground(new java.awt.Color(0, 51, 153));
        jTextFieldNoHP.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldNoHP.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 4, true));
        jTextFieldNoHP.addActionListener(this::jTextFieldNoHPActionPerformed);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Mata kuliah");

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
                                .addComponent(jTextFieldMatkul, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                                .addComponent(jTextFieldNIDN))
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTextFieldNama, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                                .addComponent(jTextFieldNoHP)))
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
                        .addComponent(jTextFieldNIDN, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelFormDataMahasiswaLayout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldNama, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(21, 21, 21)
                .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldMatkul, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldNoHP, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSave, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29))
        );

        add(jPanelFormDataMahasiswa, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 670, 260));

        jPanelTabel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setBackground(new java.awt.Color(0, 0, 0));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Data Dosen");

        jTableDataDosen.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NIDN", "Nama", "Mata Kuliah", "No HP"
            }
        ));
        jScrollPane1.setViewportView(jTableDataDosen);

        jLabelHalaman.setText("1 - 10");

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
                        .addComponent(jLabelHalaman)
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
                    .addComponent(jLabelHalaman)
                    .addComponent(jButtonNext)
                    .addComponent(jButtonBack))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanelTabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 380, 670, -1));

        jTextFieldSearch.addActionListener(this::jTextFieldSearchActionPerformed);
        add(jTextFieldSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 340, 570, 30));

        jLabelTotal.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabelTotal.setText("Total : 120");
        add(jLabelTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 340, 70, 30));
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldNIDNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldNIDNActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldNIDNActionPerformed

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        simpan();
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jTextFieldMatkulActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldMatkulActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldMatkulActionPerformed

    private void jTextFieldNamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldNamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldNamaActionPerformed

    private void jTextFieldNoHPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldNoHPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldNoHPActionPerformed

    private void jTextFieldSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldSearchActionPerformed

    private void jButtonNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextActionPerformed
        nextHalaman();
    }//GEN-LAST:event_jButtonNextActionPerformed

    private void jButtonBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBackActionPerformed
        backHalaman();
    }//GEN-LAST:event_jButtonBackActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        hapus();
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
    private javax.swing.JLabel jLabelHalaman;
    private javax.swing.JLabel jLabelTotal;
    private javax.swing.JPanel jPanelFormDataMahasiswa;
    private javax.swing.JPanel jPanelTabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableDataDosen;
    private javax.swing.JTextField jTextFieldMatkul;
    private javax.swing.JTextField jTextFieldNIDN;
    private javax.swing.JTextField jTextFieldNama;
    private javax.swing.JTextField jTextFieldNoHP;
    private javax.swing.JTextField jTextFieldSearch;
    // End of variables declaration//GEN-END:variables
}
