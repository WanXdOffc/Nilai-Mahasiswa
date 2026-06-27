package nilaimahasiswa.view;

import java.util.List;
import nilaimahasiswa.controller.KRSController;
import nilaimahasiswa.model.Course;
import nilaimahasiswa.model.KRS;
import nilaimahasiswa.model.Lecturer;
import nilaimahasiswa.model.Student;

public class panelInputNilai extends javax.swing.JPanel {

    private KRSController controller;
    private int     currentPage = 1;
    private int     selectedId  = -1;
    private boolean editMode    = false;
    private boolean searchMode  = false;

    public panelInputNilai() {
        initComponents();
        try {
            controller = new KRSController();
            KRSController.DATA_PER_HALAMAN = 7;
            initialize();
        } catch (Exception e) {
            showError("Failed to connect: " + e.getMessage());
        }
    }

    // ============================================================
    // INITIALIZATION
    // ============================================================

    private void initialize() throws Exception {
        setupTable();
        setupTableListener();
        setupScoreListeners();
        loadComboBoxData();
        loadData();
    }

    private void setupTable() {
        // Kolom tabel sesuai header yang sudah kamu buat di GUI
        String[] columns = {"ID", "NIM", "Nama",
                            "Mata Kuliah", "SKS", "Dosen", "Nilai"};
        javax.swing.table.DefaultTableModel model =
            new javax.swing.table.DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };

        // Ambil nama tabel dari Navigator kamu
        // Scroll sampai bawah Navigator untuk lihat nama JTable
        // Sesuaikan nama variable tabel di sini
        getTableComponent().setModel(model);
        getTableComponent().setSelectionMode(
            javax.swing.ListSelectionModel.SINGLE_SELECTION);
        getTableComponent().setRowHeight(28);
        getTableComponent().getTableHeader().setReorderingAllowed(false);

        // Sembunyikan kolom ID
        getTableComponent().getColumnModel().getColumn(0).setMinWidth(0);
        getTableComponent().getColumnModel().getColumn(0).setMaxWidth(0);
        getTableComponent().getColumnModel().getColumn(0).setWidth(0);

        // Lebar kolom
        getTableComponent().getColumnModel().getColumn(1).setPreferredWidth(90);
        getTableComponent().getColumnModel().getColumn(2).setPreferredWidth(160);
        getTableComponent().getColumnModel().getColumn(3).setPreferredWidth(160);
        getTableComponent().getColumnModel().getColumn(4).setPreferredWidth(50);
        getTableComponent().getColumnModel().getColumn(5).setPreferredWidth(150);
        getTableComponent().getColumnModel().getColumn(6).setPreferredWidth(60);

        applyRowRenderer();
    }

    // Helper agar tidak tulis nama tabel berkali-kali
    // GANTI nama jTable sesuai Navigator kamu
    private javax.swing.JTable getTableComponent() {
        return jTableDataInputNilai; // sesuaikan nama ini
    }

    private void applyRowRenderer() {
        // Index kolom Grade di tabel (kolom terakhir = index 6)
        int gradeColumnIndex = 6;

        javax.swing.table.DefaultTableCellRenderer defaultRenderer =
            new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public java.awt.Component getTableCellRendererComponent(
                    javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                    super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);

                    // Warna baris selang-seling
                    if (!isSelected) {
                        setBackground(row % 2 == 0
                            ? java.awt.Color.WHITE
                            : new java.awt.Color(245, 247, 250));
                        setForeground(java.awt.Color.DARK_GRAY);
                    }

                    setBorder(javax.swing.BorderFactory
                        .createEmptyBorder(0, 8, 0, 8));
                    setOpaque(true);
                    return this;
                }
            };

        // Renderer khusus kolom Grade
        javax.swing.table.DefaultTableCellRenderer gradeRenderer =
            new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public java.awt.Component getTableCellRendererComponent(
                    javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                    super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);

                    setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                    setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
                    setBorder(javax.swing.BorderFactory
                        .createEmptyBorder(2, 8, 2, 8));
                    setOpaque(true);

                    if (!isSelected && value != null) {
                        switch (value.toString()) {
                            case "A":
                                setBackground(new java.awt.Color(232, 245, 233));
                                setForeground(new java.awt.Color(27, 94, 32));
                                break;
                            case "B":
                                setBackground(new java.awt.Color(227, 242, 253));
                                setForeground(new java.awt.Color(13, 71, 161));
                                break;
                            case "C":
                                setBackground(new java.awt.Color(255, 243, 224));
                                setForeground(new java.awt.Color(230, 81, 0));
                                break;
                            case "D":
                                setBackground(new java.awt.Color(255, 235, 238));
                                setForeground(new java.awt.Color(183, 28, 28));
                                break;
                            default:
                                setBackground(java.awt.Color.WHITE);
                                setForeground(java.awt.Color.DARK_GRAY);
                                break;
                        }
                    }
                    return this;
                }
            };

        // Terapkan renderer ke semua kolom
        for (int i = 0; i < getTableComponent().getColumnCount(); i++) {
            if (i == gradeColumnIndex) {
                getTableComponent().getColumnModel()
                    .getColumn(i).setCellRenderer(gradeRenderer);
            } else {
                getTableComponent().getColumnModel()
                    .getColumn(i).setCellRenderer(defaultRenderer);
            }
        }
    }

    private void setupTableListener() {
        getTableComponent().getSelectionModel()
            .addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) fillFormFromTable();
            });
    }

    // Hitung grade otomatis setiap nilai diketik
    private void setupScoreListeners() {
        javax.swing.event.DocumentListener listener =
            new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    updateGradeDisplay();
                }
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    updateGradeDisplay();
                }
                public void changedUpdate(javax.swing.event.DocumentEvent e) {}
            };
        jTextFieldSikap.getDocument().addDocumentListener(listener);
        jTextFieldUTS.getDocument().addDocumentListener(listener);
        jTextFieldUAS.getDocument().addDocumentListener(listener);
    }

    private void loadComboBoxData() throws Exception {
        // Isi combo mahasiswa
        jComboBoxStudent.removeAllItems();
        for (Student s : controller.getAllStudents()) {
            jComboBoxStudent.addItem(s); // toString() → nama mahasiswa
        }

        // Isi combo matakuliah
        jComboBoxCourse.removeAllItems();
        for (Course c : controller.getAllCourses()) {
            jComboBoxCourse.addItem(c); // toString() → nama matakuliah
        }

        // Isi combo dosen
        jComboBoxDosen.removeAllItems();
        for (Lecturer l : controller.getAllLecturers()) {
            jComboBoxDosen.addItem(l); // toString() → nama dosen
        }

        // Isi combo semester
        jComboBoxSemester.removeAllItems();
        for (int i = 1; i <= 8; i++) {
            jComboBoxSemester.addItem(i);
        }

        // Listener — update info saat pilih mahasiswa
        jComboBoxStudent.addActionListener(e -> updateStudentInfo());

        // Listener — update info saat pilih matakuliah
        jComboBoxCourse.addActionListener(e -> updateCourseInfo());

        updateStudentInfo();
        updateCourseInfo();
    }

    private void updateStudentInfo() {
        Student s = (Student) jComboBoxStudent.getSelectedItem();
        if (s != null) {
            jLabelInfoStudent.setText(
                "NIM: " + s.getNim() + " | Prodi: " + s.getStudyProgram());
        }
    }

    private void updateCourseInfo() {
        Course c = (Course) jComboBoxCourse.getSelectedItem();
        if (c != null) {
            jLabelInfoCourse.setText(
                "Kode: " + c.getCode() + " | SKS: " + c.getSKS());
        }
    }

    private void updateGradeDisplay() {
        double sikap = parseScore(jTextFieldSikap.getText());
        double uts   = parseScore(jTextFieldUTS.getText());
        double uas   = parseScore(jTextFieldUAS.getText());

        // Hanya update kalau ada nilai yang diisi
        boolean adaNilai = !jTextFieldSikap.getText().trim().isEmpty()
                        || !jTextFieldUTS.getText().trim().isEmpty()
                        || !jTextFieldUAS.getText().trim().isEmpty();

        if (!adaNilai) {
            jLabelGrade.setText("-");
            jLabelGrade.setBackground(new java.awt.Color(240, 240, 240));
            jLabelGrade.setForeground(java.awt.Color.GRAY);
            return;
        }

        String grade = controller.calculateGrade(sikap, uts, uas);
        jLabelGrade.setText(grade);
        jLabelGrade.setOpaque(true);

        switch (grade) {
            case "A":
                jLabelGrade.setBackground(new java.awt.Color(232, 245, 233));
                jLabelGrade.setForeground(new java.awt.Color(27, 94, 32));
                break;
            case "B":
                jLabelGrade.setBackground(new java.awt.Color(227, 242, 253));
                jLabelGrade.setForeground(new java.awt.Color(13, 71, 161));
                break;
            case "C":
                jLabelGrade.setBackground(new java.awt.Color(255, 243, 224));
                jLabelGrade.setForeground(new java.awt.Color(230, 81, 0));
                break;
            case "D":
                jLabelGrade.setBackground(new java.awt.Color(255, 235, 238));
                jLabelGrade.setForeground(new java.awt.Color(183, 28, 28));
                break;
            default:
                jLabelGrade.setBackground(new java.awt.Color(240, 240, 240));
                jLabelGrade.setForeground(java.awt.Color.GRAY);
                break;
        }
        jLabelGrade.repaint();
    }

    // ============================================================
    // LOAD DATA
    // ============================================================

    private void loadData() throws Exception {
        javax.swing.table.DefaultTableModel model =
            (javax.swing.table.DefaultTableModel) getTableComponent().getModel();
        model.setRowCount(0);

        for (KRS k : controller.getPage(currentPage)) {
            model.addRow(new Object[]{
                k.getId(),
                k.getNim(),
                getStudentName(k.getNim()),
                k.getCourse().getCourseName(),
                k.getCourse().getSKS(),
                getLecturerName(k.getNidnDosen()),
                k.getGrade()
            });
        }
        updatePageInfo();
    }

    private void updatePageInfo() throws Exception {
        int total     = controller.getTotalData();
        int totalPage = controller.getTotalPages();

        jLabelTotal.setText("Total : " + total + " data");
        jLabelPage.setText("Page " + currentPage + " of " + totalPage);

        jButtonNext.setEnabled(!searchMode && currentPage < totalPage);
        jButtonBack.setEnabled(!searchMode && currentPage > 1);
    }

    // ============================================================
    // FORM
    // ============================================================

    private void fillFormFromTable() {
        int row = getTableComponent().getSelectedRow();
        if (row < 0) return;

        selectedId = (int) getTableComponent().getValueAt(row, 0);

        // Set combo mahasiswa
        String nim = getTableComponent().getValueAt(row, 1).toString();
        setComboByNim(nim);

        // Set combo matakuliah
        String namaMk = getTableComponent().getValueAt(row, 3).toString();
        setComboByCourseName(namaMk);

        // Set combo dosen
        String namaDosen = getTableComponent().getValueAt(row, 5).toString();
        setComboByLecturerName(namaDosen);

        jComboBoxStudent.setEnabled(false);
        jComboBoxCourse.setEnabled(false);
        editMode = true;
    }

    private void resetForm() {
        jTextFieldSikap.setText("");
        jTextFieldUTS.setText("");
        jTextFieldUAS.setText("");
        jLabelGrade.setText("-");
        jComboBoxStudent.setEnabled(true);
        jComboBoxCourse.setEnabled(true);
        if (jComboBoxStudent.getItemCount() > 0)
            jComboBoxStudent.setSelectedIndex(0);
        if (jComboBoxCourse.getItemCount() > 0)
            jComboBoxCourse.setSelectedIndex(0);
        if (jComboBoxSemester.getItemCount() > 0)
            jComboBoxSemester.setSelectedIndex(0);
        if (jComboBoxDosen.getItemCount() > 0)
            jComboBoxDosen.setSelectedIndex(0);
        selectedId = -1;
        editMode   = false;
        searchMode = false;
        getTableComponent().clearSelection();
    }
    
    public void clearSelection() {
        resetForm();
    }

    // ============================================================
    // CRUD
    // ============================================================

    private void save() {
        Student  student  = (Student)  jComboBoxStudent.getSelectedItem();
        Course   course   = (Course)   jComboBoxCourse.getSelectedItem();
        Lecturer lecturer = (Lecturer) jComboBoxDosen.getSelectedItem();
        int      semester = (int)      jComboBoxSemester.getSelectedItem();

        if (student == null || course == null || lecturer == null) {
            showError("Pilih mahasiswa, mata kuliah, dan dosen!");
            return;
        }

        double sikap, uts, uas;
        try {
            sikap = Double.parseDouble(jTextFieldSikap.getText().trim());
            uts   = Double.parseDouble(jTextFieldUTS.getText().trim());
            uas   = Double.parseDouble(jTextFieldUAS.getText().trim());
        } catch (NumberFormatException e) {
            showError("Nilai harus berupa angka!");
            return;
        }

        try {
            if (editMode) {
                controller.update(selectedId, student.getNim(),
                    course.getCode(), sikap, uts, uas,
                    semester, "2024/2025", lecturer.getNidn());
                showSuccess("Data nilai berhasil diupdate!");
            } else {
                controller.save(student.getNim(), course.getCode(),
                    sikap, uts, uas, semester,
                    "2024/2025", lecturer.getNidn());
                showSuccess("Data nilai berhasil disimpan!");
            }
            resetForm();
            currentPage = 1;
            loadData();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void delete() {
        if (selectedId <= 0) {
            showError("Pilih data yang akan dihapus!");
            return;
        }
        int confirm = javax.swing.JOptionPane.showConfirmDialog(
            this, "Yakin hapus data nilai ini?", "Konfirmasi",
            javax.swing.JOptionPane.YES_NO_OPTION,
            javax.swing.JOptionPane.WARNING_MESSAGE);

        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            try {
                controller.delete(selectedId);
                showSuccess("Data berhasil dihapus!");
                resetForm();
                currentPage = 1;
                loadData();
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }
    }

    // ============================================================
    // PAGINATION
    // ============================================================

    private void nextPage() {
        try { currentPage++; loadData(); }
        catch (Exception e) { showError(e.getMessage()); }
    }

    private void prevPage() {
        try { currentPage--; loadData(); }
        catch (Exception e) { showError(e.getMessage()); }
    }

    // ============================================================
    // HELPER — cari nama dari NIM/NIDN
    // ============================================================

    private String getStudentName(String nim) {
        try {
            for (Student s : controller.getAllStudents()) {
                if (s.getNim().equals(nim)) return s.getName();
            }
        } catch (Exception e) { /* ignore */ }
        return nim;
    }

    private String getLecturerName(String nidn) {
        if (nidn == null) return "-";
        try {
            for (Lecturer l : controller.getAllLecturers()) {
                if (l.getNidn().equals(nidn)) return l.getName();
            }
        } catch (Exception e) { /* ignore */ }
        return nidn;
    }

    private void setComboByNim(String nim) {
        for (int i = 0; i < jComboBoxStudent.getItemCount(); i++) {
            Student s = jComboBoxStudent.getItemAt(i);
            if (s.getNim().equals(nim)) {
                jComboBoxStudent.setSelectedIndex(i);
                return;
            }
        }
    }

    private void setComboByCourseName(String nama) {
        for (int i = 0; i < jComboBoxCourse.getItemCount(); i++) {
            Course c = jComboBoxCourse.getItemAt(i);
            if (c.getCourseName().equals(nama)) {
                jComboBoxCourse.setSelectedIndex(i);
                return;
            }
        }
    }

    private void setComboByLecturerName(String nama) {
        for (int i = 0; i < jComboBoxDosen.getItemCount(); i++) {
            Lecturer l = jComboBoxDosen.getItemAt(i);
            if (l.getName().equals(nama)) {
                jComboBoxDosen.setSelectedIndex(i);
                return;
            }
        }
    }

    private double parseScore(String text) {
        try { return Double.parseDouble(text.trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    private void showSuccess(String message) {
        javax.swing.JOptionPane.showMessageDialog(
            this, message, "Sukses",
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
        jButtonSave = new javax.swing.JButton();
        jTextFieldSikap = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jComboBoxCourse = new javax.swing.JComboBox<>();
        jComboBoxStudent = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JSeparator();
        jLabelInfoStudent = new javax.swing.JLabel();
        jLabelInfoCourse = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jTextFieldUTS = new javax.swing.JTextField();
        jTextFieldUAS = new javax.swing.JTextField();
        jLabelGrade = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jButtonDelete = new javax.swing.JButton();
        jComboBoxSemester = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jComboBoxDosen = new javax.swing.JComboBox<>();
        jButtonClear = new javax.swing.JButton();
        jPanelTabel1 = new javax.swing.JPanel();
        jPanelFormDataMahasiswa.putClientProperty("component.arc", 16);
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableDataInputNilai = new javax.swing.JTable();
        jLabelPage = new javax.swing.JLabel();
        jButtonNext = new javax.swing.JButton();
        jButtonBack = new javax.swing.JButton();
        jTextFieldSearch = new javax.swing.JTextField();
        jLabelTotal = new javax.swing.JLabel();

        setBackground(new java.awt.Color(245, 245, 245));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Lucida Console", 1, 24)); // NOI18N
        jLabel1.setText("Input Nilai Mahasiswa");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Input nilai berdasarkan KRS yang sudah terdaftar");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, -1));

        jPanelFormDataMahasiswa.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Form Data KRS");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Identitas Mahasiswa");

        jButtonSave.setBackground(new java.awt.Color(0, 102, 204));
        jButtonSave.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonSave.setText("Simpan");
        jButtonSave.addActionListener(this::jButtonSaveActionPerformed);

        jTextFieldSikap.setBackground(new java.awt.Color(0, 51, 153));
        jTextFieldSikap.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextFieldSikap.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldSikap.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 4, true));
        jTextFieldSikap.setPreferredSize(new java.awt.Dimension(80, 35));
        jTextFieldSikap.addActionListener(this::jTextFieldSikapActionPerformed);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setText("Nilai Sikap");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Identitas Mata kuliah");

        jComboBoxCourse.setModel(new javax.swing.DefaultComboBoxModel<nilaimahasiswa.model.Course>());

        jComboBoxStudent.setModel(new javax.swing.DefaultComboBoxModel<nilaimahasiswa.model.Student>());
        jComboBoxStudent.addActionListener(this::jComboBoxStudentActionPerformed);

        jLabelInfoStudent.setBackground(new java.awt.Color(204, 255, 255));
        jLabelInfoStudent.setText("NIM: 2023001 | Prodi: Ilmu Komputer");
        jLabelInfoStudent.setOpaque(true);

        jLabelInfoCourse.setBackground(new java.awt.Color(204, 255, 255));
        jLabelInfoCourse.setText("Kode: MK001 | SKS: 3");
        jLabelInfoCourse.setOpaque(true);

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel13.setText("Nilai UTS");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel14.setText("Nilai UAS");

        jTextFieldUTS.setBackground(new java.awt.Color(0, 51, 153));
        jTextFieldUTS.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextFieldUTS.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldUTS.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 4, true));
        jTextFieldUTS.setPreferredSize(new java.awt.Dimension(80, 35));
        jTextFieldUTS.addActionListener(this::jTextFieldUTSActionPerformed);

        jTextFieldUAS.setBackground(new java.awt.Color(0, 51, 153));
        jTextFieldUAS.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextFieldUAS.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldUAS.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 4, true));
        jTextFieldUAS.setPreferredSize(new java.awt.Dimension(80, 35));
        jTextFieldUAS.addActionListener(this::jTextFieldUASActionPerformed);

        jLabelGrade.setBackground(new java.awt.Color(153, 255, 153));
        jLabelGrade.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabelGrade.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelGrade.setText("-");
        jLabelGrade.setOpaque(true);
        jLabelGrade.setPreferredSize(new java.awt.Dimension(80, 35));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel16.setText("Nilai Huruf");

        jButtonDelete.setText("Delete");
        jButtonDelete.addActionListener(this::jButtonDeleteActionPerformed);

        jComboBoxSemester.setModel(new javax.swing.DefaultComboBoxModel<Integer>());
        jComboBoxSemester.addActionListener(this::jComboBoxSemesterActionPerformed);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Dosen");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Semester");

        jComboBoxDosen.setModel(new javax.swing.DefaultComboBoxModel<nilaimahasiswa.model.Lecturer>());

        jButtonClear.setText("Clear");
        jButtonClear.addActionListener(this::jButtonClearActionPerformed);

        javax.swing.GroupLayout jPanelFormDataMahasiswaLayout = new javax.swing.GroupLayout(jPanelFormDataMahasiswa);
        jPanelFormDataMahasiswa.setLayout(jPanelFormDataMahasiswaLayout);
        jPanelFormDataMahasiswaLayout.setHorizontalGroup(
            jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelFormDataMahasiswaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator1))
            .addGroup(jPanelFormDataMahasiswaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(jPanelFormDataMahasiswaLayout.createSequentialGroup()
                        .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jComboBoxStudent, 0, 301, Short.MAX_VALUE)
                                .addComponent(jLabelInfoStudent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel5))
                        .addGap(40, 40, 40)
                        .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jComboBoxCourse, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelInfoCourse, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelFormDataMahasiswaLayout.createSequentialGroup()
                        .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelFormDataMahasiswaLayout.createSequentialGroup()
                                .addComponent(jButtonSave, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonDelete)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonClear))
                            .addGroup(jPanelFormDataMahasiswaLayout.createSequentialGroup()
                                .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldSikap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13)
                                    .addComponent(jTextFieldUTS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldUAS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel14))
                                .addGap(18, 18, 18)
                                .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelGrade, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel16))))
                        .addGap(27, 27, 27)
                        .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel12)
                            .addComponent(jLabel11)
                            .addComponent(jComboBoxDosen, 0, 244, Short.MAX_VALUE)
                            .addComponent(jComboBoxSemester, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanelFormDataMahasiswaLayout.setVerticalGroup(
            jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFormDataMahasiswaLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxStudent, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxCourse, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelInfoCourse, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                    .addComponent(jLabelInfoStudent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelFormDataMahasiswaLayout.createSequentialGroup()
                        .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldSikap, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldUTS, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldUAS, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(28, 28, 28)
                        .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonSave, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonClear, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelFormDataMahasiswaLayout.createSequentialGroup()
                        .addGroup(jPanelFormDataMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelGrade, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxSemester, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxDosen, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        add(jPanelFormDataMahasiswa, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 670, 280));

        jPanelTabel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setBackground(new java.awt.Color(0, 0, 0));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Data Nilai");

        jTableDataInputNilai.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NIM", "Nama", "Mata Kuliah", "SKS", "Dosen", "NIlai"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTableDataInputNilai);

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
                .addGap(4, 4, 4)
                .addGroup(jPanelTabel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTabel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonBack)
                        .addComponent(jLabelPage))
                    .addComponent(jButtonNext))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        add(jPanelTabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 384, 670, 310));

        jTextFieldSearch.addActionListener(this::jTextFieldSearchActionPerformed);
        add(jTextFieldSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 350, 570, 30));

        jLabelTotal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabelTotal.setText("Total : 120");
        add(jLabelTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 350, -1, 30));
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        save();
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jTextFieldSikapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSikapActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldSikapActionPerformed

    private void jTextFieldSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldSearchActionPerformed

    private void jButtonNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextActionPerformed
        nextPage();
    }//GEN-LAST:event_jButtonNextActionPerformed

    private void jButtonBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBackActionPerformed
        prevPage();
    }//GEN-LAST:event_jButtonBackActionPerformed

    private void jTextFieldUTSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldUTSActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldUTSActionPerformed

    private void jTextFieldUASActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldUASActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldUASActionPerformed

    private void jComboBoxStudentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxStudentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxStudentActionPerformed

    private void jComboBoxSemesterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxSemesterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxSemesterActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        delete();
    }//GEN-LAST:event_jButtonDeleteActionPerformed

    private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearActionPerformed
        resetForm();
    }//GEN-LAST:event_jButtonClearActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBack;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonNext;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JComboBox<nilaimahasiswa.model.Course> jComboBoxCourse;
    private javax.swing.JComboBox<nilaimahasiswa.model.Lecturer> jComboBoxDosen;
    private javax.swing.JComboBox<Integer> jComboBoxSemester;
    private javax.swing.JComboBox<nilaimahasiswa.model.Student> jComboBoxStudent;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabelGrade;
    private javax.swing.JLabel jLabelInfoCourse;
    private javax.swing.JLabel jLabelInfoStudent;
    private javax.swing.JLabel jLabelPage;
    private javax.swing.JLabel jLabelTotal;
    private javax.swing.JPanel jPanelFormDataMahasiswa;
    private javax.swing.JPanel jPanelTabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTableDataInputNilai;
    private javax.swing.JTextField jTextFieldSearch;
    private javax.swing.JTextField jTextFieldSikap;
    private javax.swing.JTextField jTextFieldUAS;
    private javax.swing.JTextField jTextFieldUTS;
    // End of variables declaration//GEN-END:variables
}
