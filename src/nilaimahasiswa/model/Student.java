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
    private ArrayList<KRS> krsList;

    public Student(String idCard, String name, String nim, String studyProgram) {
        super(idCard, name);
        this.nim = nim;
        this.studyProgram = studyProgram;
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

    public void addKRS(KRS krs) {
        krsList.add(krs);
    }
}