package nilaimahasiswa.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nilaimahasiswa.dao.StudentDAO;
import nilaimahasiswa.model.Student;

/**
 * Bertanggung jawab untuk logika bisnis mahasiswa.
 * Validasi dilakukan di sini, bukan di View atau DAO.
 */
public class StudentController {

    private final StudentDAO dao;
    public static final int DATA_PER_HALAMAN = 10;

    public StudentController() throws SQLException {
        this.dao = new StudentDAO();
    }

    /**
     * Tambah mahasiswa baru.
     * Controller cek: field kosong, format NIM, NIM sudah ada.
     */
    public void tambah(String nim, String nama, 
                       String prodi, String angkatan) throws Exception {
        validasiInput(nim, nama, prodi, angkatan);

        if (dao.isNimExists(nim)) {
            throw new Exception("NIM " + nim + " sudah terdaftar!");
        }

        dao.insert(new Student(nama, nim, prodi, angkatan));
    }

    /**
     * Update data mahasiswa.
     * NIM tidak bisa diubah — hanya nama, prodi, angkatan.
     */
    public void update(String nim, String nama,
                       String prodi, String angkatan) throws Exception {
        validasiInput(nim, nama, prodi, angkatan);

        if (!dao.isNimExists(nim)) {
            throw new Exception("Mahasiswa dengan NIM " + nim + " tidak ditemukan!");
        }

        dao.update(new Student(nama, nim, prodi, angkatan));
    }

    /**
     * Hapus mahasiswa berdasarkan NIM.
     */
    public void hapus(String nim) throws Exception {
        if (nim == null || nim.trim().isEmpty()) {
            throw new Exception("Pilih mahasiswa yang akan dihapus!");
        }
        dao.delete(nim);
    }

    /**
     * Ambil semua data atau hasil pencarian.
     * Kalau keyword kosong → ambil semua.
     */
    public List<Student> cari(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return dao.findAll();
        }
        return dao.findByKeyword(keyword.trim());
    }

    /**
     * Ambil data sesuai halaman untuk pagination.
     */
    public List<Student> getHalaman(int halaman) throws SQLException {
        return dao.findPaged(halaman, DATA_PER_HALAMAN);
    }

    public int getTotalHalaman() throws SQLException {
        return (int) Math.ceil((double) dao.count() / DATA_PER_HALAMAN);
    }

    public int getTotalData() throws SQLException {
        return dao.count();
    }

    /**
     * Validasi input — tanggung jawab controller, bukan DAO atau View.
     */
    private void validasiInput(String nim, String nama,
                                String prodi, String angkatan) throws Exception {
        if (nim.trim().isEmpty()) throw new Exception("NIM tidak boleh kosong!");
        if (nama.trim().isEmpty()) throw new Exception("Nama tidak boleh kosong!");
        if (prodi.trim().isEmpty()) throw new Exception("Prodi tidak boleh kosong!");
        if (angkatan.trim().isEmpty()) 
            throw new Exception("Angkatan tidak boleh kosong!");
        if (nim.trim().length() < 5) 
            throw new Exception("NIM minimal 5 karakter!");
        if (!angkatan.trim().matches("\\d{4}")) 
            throw new Exception("Angkatan harus 4 digit angka! Contoh: 2023");
    }
}