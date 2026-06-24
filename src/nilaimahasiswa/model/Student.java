/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nilaimahasiswa.model;

/**
 *
 * @author iketu
 */
import nilaimahasiswa.model.Person;
import nilaimahasiswa.model.KRS;
import java.util.ArrayList;

public class Student extends Person {
    private String nim;
    private String studyProgram;
    private String angkatan;
    private ArrayList<KRS> krsList;
    
    public Student(String name, String nim, String studyProgram, String angkatan) {
        super(nim, name);
        this.nim = nim;
        this.studyProgram = studyProgram;
        this.angkatan = angkatan;
        krsList = new ArrayList<>();
    }

    @Override
    public String toString() {
        return this.name;
    }
    public String getNim() { 
        return nim; 
    }
    public String getName() { 
        return name; 
    }
    public String getStudyProgram() { 
        return studyProgram; 
    }
    public String getAngkatan() { 
        return angkatan; 
    }

    public void setNim(String nim) { 
        this.nim = nim; 
    }
    public void setName(String name) { 
        this.name = name; 
    }
    public void setStudyProgram(String sp) { 
        this.studyProgram = sp; 
    }
    public void setAngkatan(String angkatan) { 
        this.angkatan = angkatan; 
    }
}