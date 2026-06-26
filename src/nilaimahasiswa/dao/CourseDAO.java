package nilaimahasiswa.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import nilaimahasiswa.model.Course;
import nilaimahasiswa.utils.DBConnection;

public class CourseDAO {

    private final Connection connection;

    public CourseDAO() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    public int insert(Course c) throws SQLException {
        String sql = "INSERT INTO mata_kuliah VALUES (?,?,?,?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, c.getCode());
        stmt.setString(2, c.getCourseName());
        stmt.setInt(3, c.getSKS());
        stmt.setInt(4, c.getSemester());
        return stmt.executeUpdate();
    }

    public int update(Course c) throws SQLException {
        String sql = "UPDATE mata_kuliah SET nama=?, sks=?, semester=? WHERE kode=?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, c.getCourseName());
        stmt.setInt(2, c.getSKS());
        stmt.setInt(3, c.getSemester());
        stmt.setString(4, c.getCode());
        return stmt.executeUpdate();
    }

    public int delete(String kode) throws SQLException {
        String sql = "DELETE FROM mata_kuliah WHERE kode=?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, kode);
        return stmt.executeUpdate();
    }

    public List<Course> findAll() throws SQLException {
        List<Course> list = new ArrayList<>();
        ResultSet rs = connection.createStatement()
            .executeQuery("SELECT * FROM mata_kuliah ORDER BY kode");
        while (rs.next()) list.add(map(rs));
        return list;
    }

    public List<Course> findByKeyword(String keyword) throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM mata_kuliah WHERE kode LIKE ? OR nama LIKE ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        String like = "%" + keyword + "%";
        stmt.setString(1, like);
        stmt.setString(2, like);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) list.add(map(rs));
        return list;
    }

    public List<Course> findPaged(int page, int limit) throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM mata_kuliah ORDER BY kode LIMIT ? OFFSET ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, limit);
        stmt.setInt(2, (page - 1) * limit);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) list.add(map(rs));
        return list;
    }

    public boolean isKodeExists(String kode) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
            "SELECT COUNT(*) FROM mata_kuliah WHERE kode=?");
        stmt.setString(1, kode);
        ResultSet rs = stmt.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }

    public int count() throws SQLException {
        ResultSet rs = connection.createStatement()
            .executeQuery("SELECT COUNT(*) FROM mata_kuliah");
        return rs.next() ? rs.getInt(1) : 0;
    }

    private Course map(ResultSet rs) throws SQLException {
        return new Course(
            rs.getString("kode"),
            rs.getString("nama"),
            rs.getInt("sks"),
            rs.getInt("semester")
        );
    }
}