/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nilaimahasiswa.model;

/**
 *
 * @author iketu
 */
public class Course {
    private String code;
    private String courseName;
    private int sks;
    private int semester;

    public Course(String code, String courseName, int sks, int semester) {
        this.code = code;
        this.courseName = courseName;
        this.sks = sks;
        this.semester = semester;
    }

    public Course getCourse() {
        return this;
    }

    @Override
    public String toString() {
        return this.courseName;
    }

    public String getCode() {
        return code;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getSKS() {
        return sks;
    }
}