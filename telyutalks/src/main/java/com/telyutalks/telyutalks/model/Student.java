package com.telyutalks.telyutalks.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("STUDENT") // When this is saved, the 'role' column will be 'STUDENT'
public class Student extends User {

    @Column(unique = true)
    private String nim;

    private String programStudi;
    
    private String angkatan;

    // Getters and Setters for student-specific fields
    public String getNim() { return nim; }
    public void setNim(String nim) { this.nim = nim; }
    public String getProgramStudi() { return programStudi; }
    public void setProgramStudi(String programStudi) { this.programStudi = programStudi; }
    public String getAngkatan() { return angkatan; }
    public void setAngkatan(String angkatan) { this.angkatan = angkatan; }
}