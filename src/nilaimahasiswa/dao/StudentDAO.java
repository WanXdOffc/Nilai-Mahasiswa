/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nilaimahasiswa.dao;
package nilaimahasiswa.Student;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nilaimahasiswa.utils.DBConnection;
/**
 *
 * @author iketu
 */
public class StudentDAO {
    private Connection connection;
    public StudentDAO() {
        try {
            connection = DBConnection.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(StudentDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // create User
    public int create(Student student){
        try{
            String sql = "INSERT INTO students (cardID, NIM, name, studiProgram) VALUES(?,?,?,?)";
            PreparedStatement stmt =connection.prepareStatement(sql);
            stmt.setString(1, student.getCardID());
            stmt.setString(2, student.getNim());
            stmt.setString(3, student.getName());
            stmt.setString(4, student.getStudyProgram());
            stmt.executeUpdate();
            return 1;
            
        }catch(SQLException e){
              return 0;
        }
        
    }
     // Select/read Users
    public List<Student> getStudent(){
        List<Student> students = new ArrayList<>();
        
        try{
            String sql = "SELECT * FROM students";
            PreparedStatement stmt =connection.prepareStatement(sql);
            
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                int id = rs.getInt("studentID");
                String cardID = rs.getString("cardID");
                String NIM = rs.getString("NIM");
                String name = rs.getString("name");
                String studiProgram = rs.getString("studiProgram");
                
                students.add(new Student(cardID,name,NIM,studiProgram));
                
            }
            
        }catch(SQLException e){
            e.printStackTrace();
        }
        return students;
    }
    
}
