package com.telyutalks.telyutalks.model;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("LECTURER") 
public class Lecturer extends User {

    @Column(unique = true)
    private String nidn; 

    public String getNidn() { return nidn; }
    public void setNidn(String nidn) { this.nidn = nidn; }
}
