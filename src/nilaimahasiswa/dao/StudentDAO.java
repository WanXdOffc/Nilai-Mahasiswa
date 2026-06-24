package nilaimahasiswa.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import nilaimahasiswa.model.Student;
import nilaimahasiswa.utils.DBConnection;

public class StudentDAO {

    private final Connection connection;

    public StudentDAO() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    public int insert(Student s) throws SQLException {
        String sql = "INSERT INTO mahasiswa VALUES (?,?,?,?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, s.getNim());
        stmt.setString(2, s.getName());
        stmt.setString(3, s.getStudyProgram());
        stmt.setString(4, s.getAngkatan());
        return stmt.executeUpdate();
    }

    public int update(Student s) throws SQLException {
        String sql = "UPDATE mahasiswa SET nama=?, prodi=?, angkatan=? WHERE nim=?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, s.getName());
        stmt.setString(2, s.getStudyProgram());
        stmt.setString(3, s.getAngkatan());
        stmt.setString(4, s.getNim());
        return stmt.executeUpdate();
    }

    public int delete(String nim) throws SQLException {
        String sql = "DELETE FROM mahasiswa WHERE nim=?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, nim);
        return stmt.executeUpdate();
    }

    public List<Student> findAll() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM mahasiswa ORDER BY nim";
        ResultSet rs = connection.createStatement().executeQuery(sql);
        while (rs.next()) list.add(map(rs));
        return list;
    }

    public List<Student> findByKeyword(String keyword) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM mahasiswa WHERE nim LIKE ? OR nama LIKE ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        String like = "%" + keyword + "%";
        stmt.setString(1, like);
        stmt.setString(2, like);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) list.add(map(rs));
        return list;
    }

    public List<Student> findPaged(int page, int limit) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM mahasiswa ORDER BY nim LIMIT ? OFFSET ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, limit);
        stmt.setInt(2, (page - 1) * limit);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) list.add(map(rs));
        return list;
    }

    public boolean isNimExists(String nim) throws SQLException {
        String sql = "SELECT COUNT(*) FROM mahasiswa WHERE nim=?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, nim);
        ResultSet rs = stmt.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }

    public int count() throws SQLException {
        ResultSet rs = connection.createStatement()
            .executeQuery("SELECT COUNT(*) FROM mahasiswa");
        return rs.next() ? rs.getInt(1) : 0;
    }

    private Student map(ResultSet rs) throws SQLException {
        return new Student(
            rs.getString("nama"),
            rs.getString("nim"),
            rs.getString("prodi"),
            rs.getString("angkatan")
        );
    }
}