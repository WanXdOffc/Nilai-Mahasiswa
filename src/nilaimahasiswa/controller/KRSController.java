
package nilaimahasiswa.controller;

import java.sql.SQLException;
import java.util.List;
import nilaimahasiswa.dao.KRSDAO;
import nilaimahasiswa.dao.StudentDAO;
import nilaimahasiswa.dao.CourseDAO;
import nilaimahasiswa.model.Course;
import nilaimahasiswa.model.KRS;
import nilaimahasiswa.model.Student;

public class KRSController {

    private final KRSDAO krsDAO;
    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;
    public static int DATA_PER_HALAMAN = 7;

    public KRSController() throws SQLException {
        this.krsDAO    = new KRSDAO();
        this.studentDAO = new StudentDAO();
        this.courseDAO  = new CourseDAO();
    }

    public void tambah(String nim, String kodeMk,
                       double score, int semester,
                       String tahunAjaran) throws Exception {
        validasi(nim, kodeMk, score, semester, tahunAjaran);

        Course course = getCourse(kodeMk);
        KRS krs = new KRS(0, nim, course, score, semester, tahunAjaran);
        krsDAO.insert(krs);
    }

    public void update(int id, String nim, String kodeMk,
                       double score, int semester,
                       String tahunAjaran) throws Exception {
        validasi(nim, kodeMk, score, semester, tahunAjaran);

        Course course = getCourse(kodeMk);
        KRS krs = new KRS(id, nim, course, score, semester, tahunAjaran);
        krsDAO.update(krs);
    }

    public void hapus(int id) throws Exception {
        if (id <= 0) throw new Exception("Pilih KRS yang akan dihapus!");
        krsDAO.delete(id);
    }

    public List<KRS> cari(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return krsDAO.findAll();
        }
        return krsDAO.findByKeyword(keyword.trim());
    }

    public List<KRS> getHalaman(int halaman) throws SQLException {
        return krsDAO.findPaged(halaman, DATA_PER_HALAMAN);
    }

    public int getTotalHalaman() throws SQLException {
        return (int) Math.ceil((double) krsDAO.count() / DATA_PER_HALAMAN);
    }

    public int getTotalData() throws SQLException {
        return krsDAO.count();
    }

    // Untuk isi ComboBox mahasiswa di panel KRS
    public List<Student> getAllMahasiswa() throws SQLException {
        return studentDAO.findAll();
    }

    // Untuk isi ComboBox mata kuliah di panel KRS
    public List<Course> getAllMataKuliah() throws SQLException {
        return courseDAO.findAll();
    }

    private Course getCourse(String kodeMk) throws Exception {
        List<Course> all = courseDAO.findAll();
        for (Course c : all) {
            if (c.getCode().equals(kodeMk)) return c;
        }
        throw new Exception("Mata kuliah tidak ditemukan!");
    }

    private void validasi(String nim, String kodeMk, double score,
                          int semester, String tahunAjaran) throws Exception {
        if (nim.trim().isEmpty())
            throw new Exception("Pilih mahasiswa terlebih dahulu!");
        if (kodeMk.trim().isEmpty())
            throw new Exception("Pilih mata kuliah terlebih dahulu!");
        if (score < 0 || score > 100)
            throw new Exception("Nilai harus antara 0 sampai 100!");
        if (semester < 1 || semester > 8)
            throw new Exception("Semester harus antara 1 sampai 8!");
        if (tahunAjaran.trim().isEmpty())
            throw new Exception("Tahun ajaran tidak boleh kosong!");
    }
}