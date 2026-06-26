/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package nilaimahasiswa.view;

import java.util.List;
import nilaimahasiswa.controller.StudentController;
import nilaimahasiswa.model.Student;
/**
 *
 * @author iketu
 */
public class panelMahasiswa extends javax.swing.JPanel {

    private StudentController controller;
    private int halamanSaatIni = 1;
    private String nimDipilih = "";
    private boolean modeEdit = false;
    private boolean sedangCari = false;

    public panelMahasiswa() {
        initComponents();
        try {
            controller = new StudentController();
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
        isiItemComboBox();
        muatData();
    }

    private void setupKolomTabel() {
        String[] kolom = {"NIM", "Nama", "Program Studi", "Angkatan"};
        javax.swing.table.DefaultTableModel model =
            new javax.swing.table.DefaultTableModel(kolom, 0) {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };
        jTableDataMahasiswa.setModel(model);
        jTableDataMahasiswa.setSelectionMode(
            javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableDataMahasiswa.getTableHeader().setReorderingAllowed(false);

        // Tinggi baris
        jTableDataMahasiswa.setRowHeight(28);

        // Lebar kolom
        jTableDataMahasiswa.getColumnModel().getColumn(0).setPreferredWidth(90);
        jTableDataMahasiswa.getColumnModel().getColumn(1).setPreferredWidth(200);
        jTableDataMahasiswa.getColumnModel().getColumn(2).setPreferredWidth(160);
        jTableDataMahasiswa.getColumnModel().getColumn(3).setPreferredWidth(80);

        // Warna baris selang-seling
        jTableDataMahasiswa.setDefaultRenderer(Object.class,
            new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public java.awt.Component getTableCellRendererComponent(
                    javax.swing.JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {
                    super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);
                    if (!isSelected) {
                        setBackground(row % 2 == 0
                            ? java.awt.Color.WHITE
                            : new java.awt.Color(245, 247, 250));
                    }
                    setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 8));
                    return this;
                }
            });
    }

    private void setupListenerTabel() {
        jTableDataMahasiswa.getSelectionModel()
            .addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    isiFormDariTabel();
                }
            });
    }

    private void setupSearchListener() {
        // Setiap ketikan langsung filter data
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
        jTextFieldNIM.putClientProperty(
            "JTextField.placeholderText", "cth: 2023001");
        jTextFieldNama.putClientProperty(
            "JTextField.placeholderText", "cth: Andi Pratama");
        jTextFieldAngkatan.putClientProperty(
            "JTextField.placeholderText", "cth: 2023");
        jTextFieldSearch.putClientProperty(
            "JTextField.placeholderText", "Masukkan NIM atau Nama...");
    }

    private void isiItemComboBox() {
        jComboBoxProdi.removeAllItems();
        jComboBoxProdi.addItem("Ilmu Komputer");
        jComboBoxProdi.addItem("Sistem Informasi");
        jComboBoxProdi.addItem("Teknik Informatika");
        jComboBoxProdi.setSelectedIndex(0);
    }

    // ============================================================
    // MUAT DATA & UPDATE LABEL
    // ============================================================

    private void muatData() throws Exception {
        javax.swing.table.DefaultTableModel model =
            (javax.swing.table.DefaultTableModel) jTableDataMahasiswa.getModel();
        model.setRowCount(0);

        List<Student> list = controller.getHalaman(halamanSaatIni);
        for (Student s : list) {
            model.addRow(new Object[]{
                s.getNim(), s.getName(),
                s.getStudyProgram(), s.getAngkatan()
            });
        }
        updateLabelInfo();
    }

    private void updateLabelInfo() throws Exception {
        int total    = controller.getTotalData();
        int totalHal = controller.getTotalHalaman();
        int awal     = total == 0 ? 0 :
            (halamanSaatIni - 1) * StudentController.DATA_PER_HALAMAN + 1;
        int akhir    = Math.min(
            halamanSaatIni * StudentController.DATA_PER_HALAMAN, total);

        // Label total data
        jLabelTotal.setText("Total : " + total + " data");

        // Label halaman
        jLabelHalaman.setText("Halaman " + halamanSaatIni + " dari " + totalHal);

        // Enable/disable tombol pagination
        // Sembunyikan pagination saat sedang mencari
        jButtonNext.setEnabled(!sedangCari && halamanSaatIni < totalHal);
        jButtonBack.setEnabled(!sedangCari && halamanSaatIni > 1);
    }

    // ============================================================
    // ISI FORM DARI TABEL
    // ============================================================

    private void isiFormDariTabel() {
        int baris = jTableDataMahasiswa.getSelectedRow();
        if (baris < 0) return;

        nimDipilih = jTableDataMahasiswa.getValueAt(baris, 0).toString();

        jTextFieldNIM.setText(nimDipilih);
        jTextFieldNama.setText(
            jTableDataMahasiswa.getValueAt(baris, 1).toString());
        jComboBoxProdi.setSelectedItem(
            jTableDataMahasiswa.getValueAt(baris, 2).toString());
        jTextFieldAngkatan.setText(
            jTableDataMahasiswa.getValueAt(baris, 3).toString());

        jTextFieldNIM.setEditable(false);
        modeEdit = true;
    }

    private void resetForm() {
        jTextFieldNIM.setText("");
        jTextFieldNama.setText("");
        jTextFieldAngkatan.setText("");
        jComboBoxProdi.setSelectedIndex(0);
        jTextFieldNIM.setEditable(true);
        nimDipilih   = "";
        modeEdit     = false;
        sedangCari   = false;
        jTableDataMahasiswa.clearSelection();
    }

    // ============================================================
    // CRUD
    // ============================================================

    private void simpan() {
        String nim      = jTextFieldNIM.getText().trim();
        String nama     = jTextFieldNama.getText().trim();
        String prodi    = jComboBoxProdi.getSelectedItem().toString();
        String angkatan = jTextFieldAngkatan.getText().trim();

        try {
            if (modeEdit) {
                controller.update(nim, nama, prodi, angkatan);
                tampilPesan("Data mahasiswa berhasil diupdate!");
            } else {
                controller.tambah(nim, nama, prodi, angkatan);
                tampilPesan("Data mahasiswa berhasil disimpan!");
            }
            resetForm();
            halamanSaatIni = 1;
            muatData();
        } catch (Exception e) {
            tampilError(e.getMessage());
        }
    }

    private void hapus() {
        if (nimDipilih.isEmpty()) {
            tampilError("Pilih mahasiswa yang akan dihapus!");
            return;
        }

        int konfirm = javax.swing.JOptionPane.showConfirmDialog(
            this,
            "Yakin hapus mahasiswa:\n" +
            "NIM  : " + nimDipilih + "\n" +
            "Nama : " + jTextFieldNama.getText(),
            "Konfirmasi Hapus",
            javax.swing.JOptionPane.YES_NO_OPTION,
            javax.swing.JOptionPane.WARNING_MESSAGE
        );

        if (konfirm == javax.swing.JOptionPane.YES_OPTION) {
            try {
                controller.hapus(nimDipilih);
                tampilPesan("Mahasiswa berhasil dihapus!");
                resetForm();
                halamanSaatIni = 1;
                muatData();
            } catch (Exception e) {
                tampilError(e.getMessage());
            }
        }
    }

    // ============================================================
    // PENCARIAN — real-time saat mengetik
    // ============================================================

    private void jalankanCari() {
        String keyword = jTextFieldSearch.getText().trim();

        try {
            javax.swing.table.DefaultTableModel model =
                (javax.swing.table.DefaultTableModel) jTableDataMahasiswa.getModel();
            model.setRowCount(0);

            if (keyword.isEmpty()) {
                sedangCari     = false;
                halamanSaatIni = 1;
                muatData();
                return;
            }

            sedangCari = true;
            List<Student> hasil = controller.cari(keyword);

            for (Student s : hasil) {
                model.addRow(new Object[]{
                    s.getNim(), s.getName(),
                    s.getStudyProgram(), s.getAngkatan()
                });
            }

            // Update label saat pencarian
            jLabelTotal.setText("Ditemukan : " + hasil.size() + " data");
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
        jTextFieldNIM = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jButtonSave = new javax.swing.JButton();
        jTextFieldNama = new javax.swing.JTextField();
        jTextFieldAngkatan = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jComboBoxProdi = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jButtonDelete = new javax.swing.JButton();
        jPanelTabel1 = new javax.swing.JPanel();
        jPanelFormDataMahasiswa.putClientProperty("component.arc", 16);
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableDataMahasiswa = new javax.swing.JTable();
        jLabelHalaman = new javax.swing.JLabel();
        jButtonNext = new javax.swing.JButton();
        jButtonBack = new javax.swing.JButton();
        jTextFieldSearch = new javax.swing.JTextField();
        jLabelTotal = new javax.swing.JLabel();

        setBackground(new java.awt.Color(245, 245, 245));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Lucida Console", 1, 24)); // NOI18N
        jLabel1.setText("Data Mahasiswa");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Kelola data mahasiswa — tambah, edit, hapus, cari");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, -1));

        jPanelFormDataMahasiswa.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Form Data Mahasiswa");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Nim");

        jTextFieldNIM.setBackground(new java.awt.Color(0, 51, 153));
        jTextFieldNIM.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldNIM.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 4, true));
        jTextFieldNIM.addActionListener(this::jTextFieldNIMActionPerformed);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Angkatan");

        jButtonSave.setBackground(new java.awt.Color(0, 102, 204));
        jButtonSave.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonSave.setText("Simpan");
        jButtonSave.addActionListener(this::jButtonSaveActionPerformed);

        jTextFieldNama.setBackground(new java.awt.Color(0, 51, 153));
        jTextFieldNama.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldNama.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 4, true));
        jTextFieldNama.addActionListener(this::jTextFieldNamaActionPerformed);

        jTextFieldAngkatan.setBackground(new java.awt.Color(0, 51, 153));
        jTextFieldAngkatan.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldAngkatan.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 4, true));
        jTextFieldAngkatan.addActionListener(this::jTextFieldAngkatanActionPerformed);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Program studi");

        jComboBoxProdi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

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
                    .addGroup(jPanelFormDataMahasiswaLayout.createSequentialGroup()
                        .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTextFieldNIM, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                                .addComponent(jComboBoxProdi, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTextFieldNama, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                                .addComponent(jTextFieldAngkatan)))
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
                        .addComponent(jTextFieldNIM, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelFormDataMahasiswaLayout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldNama, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(21, 21, 21)
                .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldAngkatan, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxProdi, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSave, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29))
        );

        add(jPanelFormDataMahasiswa, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 670, 260));

        jPanelTabel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setBackground(new java.awt.Color(0, 0, 0));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Data Mahasiswa");

        jTableDataMahasiswa.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NIM", "Nama", "Prodi", "Angkatan"
            }
        ));
        jScrollPane1.setViewportView(jTableDataMahasiswa);

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
                        .addContainerGap()
                        .addComponent(jLabel4))
                    .addGroup(jPanelTabel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 631, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelTabel1Layout.createSequentialGroup()
                        .addGap(231, 231, 231)
                        .addComponent(jButtonBack)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelHalaman)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonNext)))
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
        jLabelTotal.setText("Total : 128");
        add(jLabelTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 340, -1, 30));
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldNIMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldNIMActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldNIMActionPerformed

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        simpan();
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jTextFieldNamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldNamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldNamaActionPerformed

    private void jTextFieldAngkatanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldAngkatanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldAngkatanActionPerformed

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
    private javax.swing.JComboBox<String> jComboBoxProdi;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabelHalaman;
    private javax.swing.JLabel jLabelTotal;
    private javax.swing.JPanel jPanelFormDataMahasiswa;
    private javax.swing.JPanel jPanelTabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableDataMahasiswa;
    private javax.swing.JTextField jTextFieldAngkatan;
    private javax.swing.JTextField jTextFieldNIM;
    private javax.swing.JTextField jTextFieldNama;
    private javax.swing.JTextField jTextFieldSearch;
    // End of variables declaration//GEN-END:variables
}
