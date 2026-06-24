/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nilaimahasiswa.model;

/**
 *
 * @author iketu
 */
public class Lecturer extends Person {
    private String nidn;
    private String expertise;
    private String noHp;

    public Lecturer(String idCard, String name, String nidn, String expertise, String noHp) {
        super(idCard, name);
        this.nidn = nidn;
        this.expertise = expertise;
        this.noHp = noHp;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    //getter
    public String getNidn() { 
        return nidn; 
    }
    public String getName() { 
        return name; 
    }
    public String getExpertise() { 
        return expertise; 
    }
    public String getNoHp() { 
        return noHp; 
    }

    //setter
    public void setNidn(String nidn) { 
        this.nidn = nidn; 
    }
    public void setName(String name) { 
        this.name = name; 
    }
    public void setExpertise(String expertise) { 
        this.expertise = expertise; 
    }
    public void setNoHp(String noHp) { 
        this.noHp = noHp; 
    }
}