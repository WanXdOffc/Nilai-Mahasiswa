package nilaimahasiswa.controller;

import java.sql.SQLException;
import java.util.List;
import nilaimahasiswa.dao.CourseDAO;
import nilaimahasiswa.dao.KRSDAO;
import nilaimahasiswa.dao.StudentDAO;
import nilaimahasiswa.model.Course;
import nilaimahasiswa.model.KRS;
import nilaimahasiswa.model.Student;
import nilaimahasiswa.dao.LecturerDAO;
import nilaimahasiswa.model.Lecturer;

public class KRSController {

    private final KRSDAO    krsDAO;
    private final StudentDAO studentDAO;
    private final CourseDAO  courseDAO;
    private final LecturerDAO lecturerDAO;
    public static int DATA_PER_HALAMAN = 8;

    public KRSController() throws SQLException {
        this.krsDAO      = new KRSDAO();
        this.studentDAO  = new StudentDAO();
        this.courseDAO   = new CourseDAO();
        this.lecturerDAO = new LecturerDAO(); // tambah ini
    }

    public void save(String nim, String kodeMk,
                 double nilaiSikap, double nilaiUTS, double nilaiUAS,
                 int semester, String tahunAjaran,
                 String nidnDosen) throws Exception {
        validateInput(nim, kodeMk, nilaiSikap, nilaiUTS, nilaiUAS,
                      semester, tahunAjaran);
        Course course = findCourse(kodeMk);
        KRS krs = new KRS(0, nim, course, nilaiSikap, nilaiUTS, nilaiUAS,
                          semester, tahunAjaran, nidnDosen);
        krsDAO.insert(krs);
    }

    public void update(int id, String nim, String kodeMk,
                   double nilaiSikap, double nilaiUTS, double nilaiUAS,
                   int semester, String tahunAjaran,
                   String nidnDosen) throws Exception {
        validateInput(nim, kodeMk, nilaiSikap, nilaiUTS, nilaiUAS,
                      semester, tahunAjaran);
        Course course = findCourse(kodeMk);
        KRS krs = new KRS(id, nim, course, nilaiSikap, nilaiUTS, nilaiUAS,
                          semester, tahunAjaran, nidnDosen);
        krsDAO.update(krs);
    }

    public void delete(int id) throws Exception {
        if (id <= 0) throw new Exception("Please select a KRS entry to delete!");
        krsDAO.delete(id);
    }
    
    public List<Lecturer> getAllLecturers() throws SQLException {
        return lecturerDAO.findAll();
    }

    public List<KRS> search(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return krsDAO.findAll();
        }
        return krsDAO.findByKeyword(keyword.trim());
    }

    public List<KRS> getPage(int page) throws SQLException {
        return krsDAO.findPaged(page, DATA_PER_HALAMAN);
    }

    public int getTotalPages() throws SQLException {
        return (int) Math.ceil((double) krsDAO.count() / DATA_PER_HALAMAN);
    }

    public int getTotalData() throws SQLException {
        return krsDAO.count();
    }

    // Untuk isi combo box di panel
    public List<Student> getAllStudents() throws SQLException {
        return studentDAO.findAll();
    }

    public List<Course> getAllCourses() throws SQLException {
        return courseDAO.findAll();
    }

    // Hitung grade secara real-time saat user input nilai
    public String calculateGrade(double sikap, double uts, double uas) {
        double score = KRS.hitungScore(sikap, uts, uas);
        return KRS.hitungGrade(score);
    }

    public double calculateScore(double sikap, double uts, double uas) {
        return KRS.hitungScore(sikap, uts, uas);
    }

    private Course findCourse(String kode) throws Exception {
        for (Course c : courseDAO.findAll()) {
            if (c.getCode().equals(kode)) return c;
        }
        throw new Exception("Course not found!");
    }

    private void validateInput(String nim, String kodeMk,
                               double sikap, double uts, double uas,
                               int semester, String tahunAjaran) throws Exception {
        if (nim.trim().isEmpty())
            throw new Exception("Please select a student!");
        if (kodeMk.trim().isEmpty())
            throw new Exception("Please select a course!");
        if (sikap < 0 || sikap > 100)
            throw new Exception("Attitude score must be between 0 and 100!");
        if (uts < 0 || uts > 100)
            throw new Exception("UTS score must be between 0 and 100!");
        if (uas < 0 || uas > 100)
            throw new Exception("UAS score must be between 0 and 100!");
        if (semester < 1 || semester > 8)
            throw new Exception("Semester must be between 1 and 8!");
        if (tahunAjaran.trim().isEmpty())
            throw new Exception("Academic year cannot be empty!");
    }
}