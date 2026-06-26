package nilaimahasiswa.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import nilaimahasiswa.model.Lecturer;
import nilaimahasiswa.utils.DBConnection;

public class LecturerDAO {

    private final Connection connection;

    public LecturerDAO() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    public int insert(Lecturer l) throws SQLException {
        String sql = "INSERT INTO dosen VALUES (?,?,?,?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, l.getNidn());
        stmt.setString(2, l.getName());
        stmt.setString(3, l.getExpertise());
        stmt.setString(4, l.getNoHp());
        return stmt.executeUpdate();
    }

    public int update(Lecturer l) throws SQLException {
        String sql = "UPDATE dosen SET nama=?, keahlian=?, no_hp=? WHERE nidn=?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, l.getName());
        stmt.setString(2, l.getExpertise());
        stmt.setString(3, l.getNoHp());
        stmt.setString(4, l.getNidn());
        return stmt.executeUpdate();
    }

    public int delete(String nidn) throws SQLException {
        String sql = "DELETE FROM dosen WHERE nidn=?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, nidn);
        return stmt.executeUpdate();
    }

    public List<Lecturer> findAll() throws SQLException {
        List<Lecturer> list = new ArrayList<>();
        ResultSet rs = connection.createStatement()
            .executeQuery("SELECT * FROM dosen ORDER BY nama");
        while (rs.next()) list.add(map(rs));
        return list;
    }

    public List<Lecturer> findByKeyword(String keyword) throws SQLException {
        List<Lecturer> list = new ArrayList<>();
        String sql = "SELECT * FROM dosen WHERE nidn LIKE ? OR nama LIKE ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        String like = "%" + keyword + "%";
        stmt.setString(1, like);
        stmt.setString(2, like);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) list.add(map(rs));
        return list;
    }

    public List<Lecturer> findPaged(int page, int limit) throws SQLException {
        List<Lecturer> list = new ArrayList<>();
        String sql = "SELECT * FROM dosen ORDER BY nama LIMIT ? OFFSET ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, limit);
        stmt.setInt(2, (page - 1) * limit);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) list.add(map(rs));
        return list;
    }

    public boolean isNidnExists(String nidn) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
            "SELECT COUNT(*) FROM dosen WHERE nidn=?");
        stmt.setString(1, nidn);
        ResultSet rs = stmt.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }

    public int count() throws SQLException {
        ResultSet rs = connection.createStatement()
            .executeQuery("SELECT COUNT(*) FROM dosen");
        return rs.next() ? rs.getInt(1) : 0;
    }

    private Lecturer map(ResultSet rs) throws SQLException {
        return new Lecturer(
            rs.getString("nidn"),
            rs.getString("nama"),
            rs.getString("nidn"),
            rs.getString("keahlian"),
            rs.getString("no_hp")
        );
    }
}