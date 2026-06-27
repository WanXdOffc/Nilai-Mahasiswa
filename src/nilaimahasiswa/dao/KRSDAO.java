package nilaimahasiswa.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import nilaimahasiswa.model.Course;
import nilaimahasiswa.model.KRS;
import nilaimahasiswa.utils.DBConnection;

public class KRSDAO {

    private final Connection connection;

    public KRSDAO() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    public int insert(KRS krs) throws SQLException {
        String sql = "INSERT INTO krs "
                   + "(nim, kode_mk, nilai_sikap, nilai_uts, nilai_uas, "
                   + "score, grade, semester, tahun_ajaran, nidn_dosen) "
                   + "VALUES (?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, krs.getNim());
        stmt.setString(2, krs.getCourse().getCode());
        stmt.setDouble(3, krs.getNilaiSikap());
        stmt.setDouble(4, krs.getNilaiUTS());
        stmt.setDouble(5, krs.getNilaiUAS());
        stmt.setDouble(6, krs.getScore());
        stmt.setString(7, krs.getGrade()); 
        stmt.setInt(8, krs.getSemester());
        stmt.setString(9, krs.getTahunAjaran());
        stmt.setString(10, krs.getNidnDosen());
        return stmt.executeUpdate();
    }

    public int update(KRS krs) throws SQLException {
        String sql = "UPDATE krs SET nilai_sikap=?, nilai_uts=?, nilai_uas=?, "
                   + "score=?, grade=?, semester=?, tahun_ajaran=?, nidn_dosen=? "
                   + "WHERE id=?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setDouble(1, krs.getNilaiSikap());
        stmt.setDouble(2, krs.getNilaiUTS());
        stmt.setDouble(3, krs.getNilaiUAS());
        stmt.setDouble(4, krs.getScore());
        stmt.setString(5, krs.getGrade()); 
        stmt.setInt(6, krs.getSemester());
        stmt.setString(7, krs.getTahunAjaran());
        stmt.setString(8, krs.getNidnDosen());
        stmt.setInt(9, krs.getId());
        return stmt.executeUpdate();
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM krs WHERE id=?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, id);
        return stmt.executeUpdate();
    }

    public List<KRS> findAll() throws SQLException {
        String sql = "SELECT k.*, "
                   + "mk.nama AS nama_mk, mk.sks, mk.semester AS smt_mk, "
                   + "d.nama AS nama_dosen "
                   + "FROM krs k "
                   + "JOIN mata_kuliah mk ON k.kode_mk = mk.kode "
                   + "LEFT JOIN dosen d ON k.nidn_dosen = d.nidn "
                   + "ORDER BY k.nim";
        return executeQuery(connection.createStatement().executeQuery(sql));
    }

    public List<KRS> findPaged(int page, int limit) throws SQLException {
        String sql = "SELECT k.*, "
                   + "mk.nama AS nama_mk, mk.sks, mk.semester AS smt_mk, "
                   + "d.nama AS nama_dosen "
                   + "FROM krs k "
                   + "JOIN mata_kuliah mk ON k.kode_mk = mk.kode "
                   + "LEFT JOIN dosen d ON k.nidn_dosen = d.nidn "
                   + "ORDER BY k.nim LIMIT ? OFFSET ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, limit);
        stmt.setInt(2, (page - 1) * limit);
        return executeQuery(stmt.executeQuery());
    }

    public List<KRS> findByKeyword(String keyword) throws SQLException {
        String sql = "SELECT k.*, "
                   + "mk.nama AS nama_mk, mk.sks, mk.semester AS smt_mk, "
                   + "d.nama AS nama_dosen "
                   + "FROM krs k "
                   + "JOIN mata_kuliah mk ON k.kode_mk = mk.kode "
                   + "LEFT JOIN dosen d ON k.nidn_dosen = d.nidn "
                   + "JOIN mahasiswa mhs ON k.nim = mhs.nim "
                   + "WHERE k.nim LIKE ? OR mhs.nama LIKE ? OR mk.nama LIKE ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        String like = "%" + keyword + "%";
        stmt.setString(1, like);
        stmt.setString(2, like);
        stmt.setString(3, like);
        return executeQuery(stmt.executeQuery());
    }

    public int count() throws SQLException {
        ResultSet rs = connection.createStatement()
            .executeQuery("SELECT COUNT(*) FROM krs");
        return rs.next() ? rs.getInt(1) : 0;
    }

    // DRY — satu method untuk mapping ResultSet
    private List<KRS> executeQuery(ResultSet rs) throws SQLException {
        List<KRS> list = new ArrayList<>();
        while (rs.next()) list.add(mapToKRS(rs));
        return list;
    }

    private KRS mapToKRS(ResultSet rs) throws SQLException {
        Course course = new Course(
            rs.getString("kode_mk"),
            rs.getString("nama_mk"),
            rs.getInt("sks"),
            rs.getInt("smt_mk")
        );
        return new KRS(
            rs.getInt("id"),
            rs.getString("nim"),
            course,
            rs.getDouble("nilai_sikap"),
            rs.getDouble("nilai_uts"),
            rs.getDouble("nilai_uas"),
            rs.getInt("semester"),
            rs.getString("tahun_ajaran"),
            rs.getString("nidn_dosen")
        );
    }
}