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
        String sql = "INSERT INTO krs (nim, kode_mk, score, semester, tahun_ajaran) "
                   + "VALUES (?,?,?,?,?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, krs.getNim());
        stmt.setString(2, krs.getCourse().getCode());
        stmt.setDouble(3, krs.getScore());
        stmt.setInt(4, krs.getSemester());
        stmt.setString(5, krs.getTahunAjaran());
        return stmt.executeUpdate();
    }

    public int update(KRS krs) throws SQLException {
        String sql = "UPDATE krs SET score=?, semester=?, tahun_ajaran=? WHERE id=?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setDouble(1, krs.getScore());
        stmt.setInt(2, krs.getSemester());
        stmt.setString(3, krs.getTahunAjaran());
        stmt.setInt(4, krs.getId());
        return stmt.executeUpdate();
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM krs WHERE id=?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, id);
        return stmt.executeUpdate();
    }

    public List<KRS> findAll() throws SQLException {
        List<KRS> list = new ArrayList<>();
        String sql = "SELECT k.*, m.nama AS nama_mk, m.sks, m.semester AS smt_mk "
                   + "FROM krs k "
                   + "JOIN mata_kuliah m ON k.kode_mk = m.kode "
                   + "ORDER BY k.nim";
        ResultSet rs = connection.createStatement().executeQuery(sql);
        while (rs.next()) list.add(map(rs));
        return list;
    }

    public List<KRS> findByKeyword(String keyword) throws SQLException {
        List<KRS> list = new ArrayList<>();
        String sql = "SELECT k.*, m.nama AS nama_mk, m.sks, m.semester AS smt_mk "
                   + "FROM krs k "
                   + "JOIN mata_kuliah m ON k.kode_mk = m.kode "
                   + "JOIN mahasiswa mhs ON k.nim = mhs.nim "
                   + "WHERE k.nim LIKE ? OR mhs.nama LIKE ? OR m.nama LIKE ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        String like = "%" + keyword + "%";
        stmt.setString(1, like);
        stmt.setString(2, like);
        stmt.setString(3, like);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) list.add(map(rs));
        return list;
    }

    public List<KRS> findPaged(int page, int limit) throws SQLException {
        List<KRS> list = new ArrayList<>();
        String sql = "SELECT k.*, m.nama AS nama_mk, m.sks, m.semester AS smt_mk "
                   + "FROM krs k "
                   + "JOIN mata_kuliah m ON k.kode_mk = m.kode "
                   + "ORDER BY k.nim LIMIT ? OFFSET ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, limit);
        stmt.setInt(2, (page - 1) * limit);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) list.add(map(rs));
        return list;
    }

    public int count() throws SQLException {
        ResultSet rs = connection.createStatement()
            .executeQuery("SELECT COUNT(*) FROM krs");
        return rs.next() ? rs.getInt(1) : 0;
    }

    private KRS map(ResultSet rs) throws SQLException {
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
            rs.getDouble("score"),
            rs.getInt("semester"),
            rs.getString("tahun_ajaran")
        );
    }
}