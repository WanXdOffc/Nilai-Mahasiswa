
 package nilaimahasiswa.view;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import nilaimahasiswa.utils.DBConnection;

public class panelDashboard extends javax.swing.JPanel {

    private Connection connection;

    // Username disimpan di sini, diisi dari Dashboard saat login
    private static String loggedInUser = "Admin";

    // Dipanggil dari LoginForm setelah login berhasil
    public static void setLoggedInUser(String username) {
        loggedInUser = username;
    }

    public panelDashboard() {
        initComponents();
        try {
            connection = DBConnection.getConnection();
            tampilkanWelcome();
            muatSemuaData();
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Gagal koneksi database: " + e.getMessage());
        }
    }

    // ============================================================
    // WELCOME TEXT — nama user + tanggal + hari
    // ============================================================

    private void tampilkanWelcome() {
        // Nama user
        jLabelWelcome.setText("Selamat datang, " + loggedInUser + "!");

        // Tanggal dan hari ini
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
            "EEEE, dd MMMM yyyy", new Locale("id", "ID"));
        String tanggal = today.format(formatter);

        // Tampilkan di label tanggal
        // Sesuaikan nama label tanggal kamu dari Navigator
        jLabelTanggal.setText(tanggal);
    }

    // ============================================================
    // MUAT SEMUA DATA
    // ============================================================

    private void muatSemuaData() throws SQLException {
        muatStatistikKartu();
        muatDistribusiNilai();
        muatTabelNilaiTerbaru();
    }

    // ============================================================
    // KARTU STATISTIK
    // ============================================================

    private void muatStatistikKartu() throws SQLException {
        jLabelTotalMahasiswa.setText(
            String.valueOf(hitungTotal("SELECT COUNT(*) FROM mahasiswa")));
        jLabelTotalDosen.setText(
            String.valueOf(hitungTotal("SELECT COUNT(*) FROM dosen")));
        jLabelTotalMatkul.setText(
            String.valueOf(hitungTotal("SELECT COUNT(*) FROM mata_kuliah")));
        jLabelTotalKRS.setText(
            String.valueOf(hitungTotal("SELECT COUNT(*) FROM krs")));
    }

    private int hitungTotal(String sql) throws SQLException {
        ResultSet rs = connection.createStatement().executeQuery(sql);
        return rs.next() ? rs.getInt(1) : 0;
    }

    // ============================================================
    // DISTRIBUSI NILAI — ProgressBar
    // ============================================================

    private void muatDistribusiNilai() throws SQLException {
        // Hitung dari kolom grade
        int total  = hitungTotal(
            "SELECT COUNT(*) FROM krs WHERE grade IS NOT NULL AND grade != ''");

        if (total == 0) {
            setProgressBar(jProgressBarNilaiA, jLabelPercentA, 0,
                new java.awt.Color(76, 175, 80));
            setProgressBar(jProgressBarNilaiB, jLabelPercentB, 0,
                new java.awt.Color(33, 150, 243));
            setProgressBar(jProgressBarNilaiC, jLabelPercentC, 0,
                new java.awt.Color(255, 152, 0));
            setProgressBar(jProgressBarNilaiD, jLabelPercentD, 0,
                new java.awt.Color(244, 67, 54));
            return;
        }

        int jmlA = hitungTotal("SELECT COUNT(*) FROM krs WHERE grade='A'");
        int jmlB = hitungTotal("SELECT COUNT(*) FROM krs WHERE grade='B'");
        int jmlC = hitungTotal("SELECT COUNT(*) FROM krs WHERE grade='C'");
        int jmlD = hitungTotal("SELECT COUNT(*) FROM krs WHERE grade='D'");

        int pA = (int) Math.round((double) jmlA / total * 100);
        int pB = (int) Math.round((double) jmlB / total * 100);
        int pC = (int) Math.round((double) jmlC / total * 100);
        int pD = (int) Math.round((double) jmlD / total * 100);

        setProgressBar(jProgressBarNilaiA, jLabelPercentA, pA,
            new java.awt.Color(76, 175, 80));   // hijau
        setProgressBar(jProgressBarNilaiB, jLabelPercentB, pB,
            new java.awt.Color(33, 150, 243));  // biru
        setProgressBar(jProgressBarNilaiC, jLabelPercentC, pC,
            new java.awt.Color(255, 152, 0));   // oranye
        setProgressBar(jProgressBarNilaiD, jLabelPercentD, pD,
            new java.awt.Color(244, 67, 54));   // merah
    }

    /**
     * Mengatur nilai dan warna progress bar secara seragam.
     * DRY — satu method untuk semua grade.
     */
    private void setProgressBar(javax.swing.JProgressBar bar,
                                 javax.swing.JLabel label,
                                 int persen,
                                 java.awt.Color warna) {
        bar.setMinimum(0);
        bar.setMaximum(100);
        bar.setValue(persen);
        bar.setStringPainted(false);
        bar.setForeground(warna);
        label.setText(persen + "%");
    }
    
    
    public void refresh() {
        try {
            muatSemuaData();
        } catch (Exception e) {
            // ignore
        }
    }

    // ============================================================
    // TABEL NILAI TERBARU
    // ============================================================

    private void muatTabelNilaiTerbaru() throws SQLException {
        String[] kolom = {"NIM", "Nama", "Mata Kuliah", "Nilai"};
        javax.swing.table.DefaultTableModel model =
            new javax.swing.table.DefaultTableModel(kolom, 0) {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };

        String sql = "SELECT k.nim, mhs.nama, mk.nama AS nama_mk, k.grade "
                   + "FROM krs k "
                   + "JOIN mahasiswa mhs ON k.nim = mhs.nim "
                   + "JOIN mata_kuliah mk ON k.kode_mk = mk.kode "
                   + "WHERE k.grade IS NOT NULL "
                   + "ORDER BY k.id DESC LIMIT 5";

        ResultSet rs = connection.createStatement().executeQuery(sql);
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("nim"),
                rs.getString("nama"),
                rs.getString("nama_mk"),
                rs.getString("grade")
            });
        }

        jTableNilaiTerbaru.setModel(model);
        jTableNilaiTerbaru.setRowHeight(28);
        jTableNilaiTerbaru.getTableHeader().setReorderingAllowed(false);
        terapkanRenderer();
    }

    // ============================================================
    // RENDERER TABEL — baris selang-seling + grade berwarna
    // ============================================================

    private void terapkanRenderer() {
        javax.swing.table.DefaultTableCellRenderer barisBiasa =
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
                        setForeground(java.awt.Color.DARK_GRAY);
                    }
                    setBorder(javax.swing.BorderFactory
                        .createEmptyBorder(0, 8, 0, 8));
                    setOpaque(true);
                    return this;
                }
            };

        javax.swing.table.DefaultTableCellRenderer rendererGrade =
            new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public java.awt.Component getTableCellRendererComponent(
                    javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                    super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);
                    setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                    setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
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
                        }
                    }
                    return this;
                }
            };

        for (int i = 0; i < jTableNilaiTerbaru.getColumnCount(); i++) {
            jTableNilaiTerbaru.getColumnModel().getColumn(i)
                .setCellRenderer(i == 3 ? rendererGrade : barisBiasa);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        jPanelCardDosen = new javax.swing.JPanel();
        jPanelCardDosen.putClientProperty("component.arc", 16);
        jLabel5 = new javax.swing.JLabel();
        jLabelTotalDosen = new javax.swing.JLabel();
        jPanelCardMahasiswa = new javax.swing.JPanel();
        jPanelCardMahasiswa.putClientProperty("component.arc", 16);
        jLabel4 = new javax.swing.JLabel();
        jLabelTotalMahasiswa = new javax.swing.JLabel();
        jPanelCardMatkul = new javax.swing.JPanel();
        jPanelCardMatkul.putClientProperty("component.arc", 16);
        jLabel6 = new javax.swing.JLabel();
        jLabelTotalMatkul = new javax.swing.JLabel();
        jPanelCardKRS = new javax.swing.JPanel();
        jPanelCardKRS.putClientProperty("component.arc", 16);
        jLabel7 = new javax.swing.JLabel();
        jLabelTotalKRS = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabelWelcome = new javax.swing.JLabel();
        jPanelTabel = new javax.swing.JPanel();
        jPanelTabel.putClientProperty("component.arc", 16);
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableNilaiTerbaru = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jProgressBarNilaiA = new javax.swing.JProgressBar();
        jLabelPercentA = new javax.swing.JLabel();
        jProgressBarNilaiB = new javax.swing.JProgressBar();
        jProgressBarNilaiC = new javax.swing.JProgressBar();
        jProgressBarNilaiD = new javax.swing.JProgressBar();
        jLabelPercentB = new javax.swing.JLabel();
        jLabelPercentC = new javax.swing.JLabel();
        jLabelPercentD = new javax.swing.JLabel();
        jLabelTanggal = new javax.swing.JLabel();

        jScrollPane1.setViewportView(jEditorPane1);

        setBackground(new java.awt.Color(245, 245, 245));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanelCardDosen.setBackground(new java.awt.Color(255, 255, 255));
        jPanelCardDosen.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 4, 0, 0, new java.awt.Color(51, 204, 0)));

        jLabel5.setBackground(new java.awt.Color(234, 234, 234));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(132, 132, 132));
        jLabel5.setText("Total Dosen");

        jLabelTotalDosen.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabelTotalDosen.setText("22");

        javax.swing.GroupLayout jPanelCardDosenLayout = new javax.swing.GroupLayout(jPanelCardDosen);
        jPanelCardDosen.setLayout(jPanelCardDosenLayout);
        jPanelCardDosenLayout.setHorizontalGroup(
            jPanelCardDosenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCardDosenLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanelCardDosenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelTotalDosen)
                    .addComponent(jLabel5))
                .addContainerGap(66, Short.MAX_VALUE))
        );
        jPanelCardDosenLayout.setVerticalGroup(
            jPanelCardDosenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCardDosenLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelTotalDosen, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        add(jPanelCardDosen, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 80, 150, 110));

        jPanelCardMahasiswa.setBackground(new java.awt.Color(255, 255, 255));
        jPanelCardMahasiswa.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 4, 0, 0, new java.awt.Color(51, 204, 255)));

        jLabel4.setBackground(new java.awt.Color(234, 234, 234));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(132, 132, 132));
        jLabel4.setText("Total Mahasiswa");

        jLabelTotalMahasiswa.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabelTotalMahasiswa.setText("124");

        javax.swing.GroupLayout jPanelCardMahasiswaLayout = new javax.swing.GroupLayout(jPanelCardMahasiswa);
        jPanelCardMahasiswa.setLayout(jPanelCardMahasiswaLayout);
        jPanelCardMahasiswaLayout.setHorizontalGroup(
            jPanelCardMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCardMahasiswaLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanelCardMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelTotalMahasiswa)
                    .addComponent(jLabel4))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanelCardMahasiswaLayout.setVerticalGroup(
            jPanelCardMahasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCardMahasiswaLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelTotalMahasiswa, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        add(jPanelCardMahasiswa, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 150, 110));

        jPanelCardMatkul.setBackground(new java.awt.Color(255, 255, 255));
        jPanelCardMatkul.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 4, 0, 0, new java.awt.Color(255, 204, 51)));

        jLabel6.setBackground(new java.awt.Color(234, 234, 234));
        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(132, 132, 132));
        jLabel6.setText("Total Mata Kuliah");

        jLabelTotalMatkul.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabelTotalMatkul.setText("16");

        javax.swing.GroupLayout jPanelCardMatkulLayout = new javax.swing.GroupLayout(jPanelCardMatkul);
        jPanelCardMatkul.setLayout(jPanelCardMatkulLayout);
        jPanelCardMatkulLayout.setHorizontalGroup(
            jPanelCardMatkulLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCardMatkulLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanelCardMatkulLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelTotalMatkul)
                    .addComponent(jLabel6))
                .addContainerGap(33, Short.MAX_VALUE))
        );
        jPanelCardMatkulLayout.setVerticalGroup(
            jPanelCardMatkulLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCardMatkulLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelTotalMatkul, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        add(jPanelCardMatkul, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 80, 150, 110));

        jPanelCardKRS.setBackground(new java.awt.Color(255, 255, 255));
        jPanelCardKRS.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 4, 0, 0, new java.awt.Color(153, 0, 153)));

        jLabel7.setBackground(new java.awt.Color(234, 234, 234));
        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(132, 132, 132));
        jLabel7.setText("Total KRS");

        jLabelTotalKRS.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabelTotalKRS.setText("436");

        javax.swing.GroupLayout jPanelCardKRSLayout = new javax.swing.GroupLayout(jPanelCardKRS);
        jPanelCardKRS.setLayout(jPanelCardKRSLayout);
        jPanelCardKRSLayout.setHorizontalGroup(
            jPanelCardKRSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCardKRSLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanelCardKRSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelTotalKRS)
                    .addComponent(jLabel7))
                .addContainerGap(67, Short.MAX_VALUE))
        );
        jPanelCardKRSLayout.setVerticalGroup(
            jPanelCardKRSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCardKRSLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelTotalKRS, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        add(jPanelCardKRS, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 80, 150, 110));

        jLabel1.setFont(new java.awt.Font("Lucida Console", 1, 24)); // NOI18N
        jLabel1.setText("Dashboard");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        jLabelWelcome.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabelWelcome.setText("Selamat datang, Admin!");
        add(jLabelWelcome, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, -1));

        jPanelTabel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Data Nilai Terbaru");

        jTableNilaiTerbaru.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NIM", "Nama", "Mata Kuliah", "Nilai"
            }
        ));
        jTableNilaiTerbaru.setRowHeight(40);
        jScrollPane3.setViewportView(jTableNilaiTerbaru);

        javax.swing.GroupLayout jPanelTabelLayout = new javax.swing.GroupLayout(jPanelTabel);
        jPanelTabel.setLayout(jPanelTabelLayout);
        jPanelTabelLayout.setHorizontalGroup(
            jPanelTabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTabelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                    .addGroup(jPanelTabelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelTabelLayout.setVerticalGroup(
            jPanelTabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTabelLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        add(jPanelTabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 230, 390, 420));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel12.setBackground(new java.awt.Color(0, 0, 0));
        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Distribusi Nilai");

        jLabel13.setBackground(new java.awt.Color(0, 0, 0));
        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel13.setText("Nilai A");

        jLabel14.setBackground(new java.awt.Color(0, 0, 0));
        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel14.setText("Nilai B");

        jLabel15.setBackground(new java.awt.Color(0, 0, 0));
        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel15.setText("Nilai C");

        jLabel16.setBackground(new java.awt.Color(0, 0, 0));
        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel16.setText("Nilai D");

        jProgressBarNilaiA.setBackground(new java.awt.Color(231, 231, 231));
        jProgressBarNilaiA.setForeground(new java.awt.Color(76, 175, 80));
        jProgressBarNilaiA.setOpaque(true);
        jProgressBarNilaiA.setStringPainted(true);

        jLabelPercentA.setBackground(new java.awt.Color(0, 0, 0));
        jLabelPercentA.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabelPercentA.setText("50%");

        jProgressBarNilaiB.setBackground(new java.awt.Color(229, 229, 229));
        jProgressBarNilaiB.setForeground(new java.awt.Color(33, 150, 243));
        jProgressBarNilaiB.setOpaque(true);
        jProgressBarNilaiB.setStringPainted(true);

        jProgressBarNilaiC.setBackground(new java.awt.Color(229, 229, 229));
        jProgressBarNilaiC.setForeground(new java.awt.Color(255, 152, 0));
        jProgressBarNilaiC.setOpaque(true);
        jProgressBarNilaiC.setStringPainted(true);

        jProgressBarNilaiD.setBackground(new java.awt.Color(229, 229, 229));
        jProgressBarNilaiD.setForeground(new java.awt.Color(244, 67, 54));
        jProgressBarNilaiD.setOpaque(true);
        jProgressBarNilaiD.setStringPainted(true);

        jLabelPercentB.setBackground(new java.awt.Color(0, 0, 0));
        jLabelPercentB.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabelPercentB.setText("50%");

        jLabelPercentC.setBackground(new java.awt.Color(0, 0, 0));
        jLabelPercentC.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabelPercentC.setText("50%");

        jLabelPercentD.setBackground(new java.awt.Color(0, 0, 0));
        jLabelPercentD.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabelPercentD.setText("50%");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jProgressBarNilaiA, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelPercentA))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16)
                            .addComponent(jLabel14)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jProgressBarNilaiC, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelPercentC))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jProgressBarNilaiD, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelPercentD))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jProgressBarNilaiB, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelPercentB)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressBarNilaiA, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPercentA))
                .addGap(18, 18, 18)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelPercentB)
                    .addComponent(jProgressBarNilaiB, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressBarNilaiC, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPercentC))
                .addGap(18, 18, 18)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressBarNilaiD, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPercentD))
                .addContainerGap(32, Short.MAX_VALUE))
        );

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, 270, 270));

        jLabelTanggal.setFont(new java.awt.Font("Serif", 0, 14)); // NOI18N
        jLabelTanggal.setText("Jumat, 26 Juni 2026");
        add(jLabelTanggal, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, -1, 20));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabelPercentA;
    private javax.swing.JLabel jLabelPercentB;
    private javax.swing.JLabel jLabelPercentC;
    private javax.swing.JLabel jLabelPercentD;
    private javax.swing.JLabel jLabelTanggal;
    private javax.swing.JLabel jLabelTotalDosen;
    private javax.swing.JLabel jLabelTotalKRS;
    private javax.swing.JLabel jLabelTotalMahasiswa;
    private javax.swing.JLabel jLabelTotalMatkul;
    private javax.swing.JLabel jLabelWelcome;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelCardDosen;
    private javax.swing.JPanel jPanelCardKRS;
    private javax.swing.JPanel jPanelCardMahasiswa;
    private javax.swing.JPanel jPanelCardMatkul;
    private javax.swing.JPanel jPanelTabel;
    private javax.swing.JProgressBar jProgressBarNilaiA;
    private javax.swing.JProgressBar jProgressBarNilaiB;
    private javax.swing.JProgressBar jProgressBarNilaiC;
    private javax.swing.JProgressBar jProgressBarNilaiD;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTableNilaiTerbaru;
    // End of variables declaration//GEN-END:variables
}
