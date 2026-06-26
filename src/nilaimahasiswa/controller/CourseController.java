package nilaimahasiswa.controller;

import java.sql.SQLException;
import java.util.List;
import nilaimahasiswa.dao.CourseDAO;
import nilaimahasiswa.model.Course;

public class CourseController {

    private final CourseDAO dao;
    public static int DATA_PER_HALAMAN = 7;

    public CourseController() throws SQLException {
        this.dao = new CourseDAO();
    }

    public void tambah(String kode, String nama,
                       int sks, int semester) throws Exception {
        validasi(kode, nama, sks, semester);
        if (dao.isKodeExists(kode)) {
            throw new Exception("Kode " + kode + " sudah terdaftar!");
        }
        dao.insert(new Course(kode, nama, sks, semester));
    }

    public void update(String kode, String nama,
                       int sks, int semester) throws Exception {
        validasi(kode, nama, sks, semester);
        dao.update(new Course(kode, nama, sks, semester));
    }

    public void hapus(String kode) throws Exception {
        if (kode == null || kode.trim().isEmpty()) {
            throw new Exception("Pilih mata kuliah yang akan dihapus!");
        }
        dao.delete(kode);
    }

    public List<Course> cari(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return dao.findAll();
        }
        return dao.findByKeyword(keyword.trim());
    }

    public List<Course> getHalaman(int halaman) throws SQLException {
        return dao.findPaged(halaman, DATA_PER_HALAMAN);
    }

    public int getTotalHalaman() throws SQLException {
        return (int) Math.ceil((double) dao.count() / DATA_PER_HALAMAN);
    }

    public int getTotalData() throws SQLException {
        return dao.count();
    }

    public List<Course> getAllCourse() throws SQLException {
        return dao.findAll();
    }

    private void validasi(String kode, String nama,
                          int sks, int semester) throws Exception {
        if (kode.trim().isEmpty())
            throw new Exception("Kode mata kuliah tidak boleh kosong!");
        if (nama.trim().isEmpty())
            throw new Exception("Nama mata kuliah tidak boleh kosong!");
        if (sks < 1 || sks > 6)
            throw new Exception("SKS harus antara 1 sampai 6!");
        if (semester < 1 || semester > 8)
            throw new Exception("Semester harus antara 1 sampai 8!");
    }
}