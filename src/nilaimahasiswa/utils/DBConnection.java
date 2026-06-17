package nilaimahasiswa.utils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author iketu
 */
public class DBConnection {
    public static Connection getConnection() throws SQLException{
       String url = "jdbc:mysql://localhost:3306/db_nilai_mahasiswa";
       String user = "root";
       String password = "Bujang#5567";
       
//       return DriverManager.getConnection(url, user, password);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver tidak ditemukan.", e);
        }
   } 
}
