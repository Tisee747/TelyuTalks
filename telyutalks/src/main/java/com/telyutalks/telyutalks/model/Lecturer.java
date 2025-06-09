package com.telyutalks.telyutalks.model;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("LECTURER") // When this is saved, the 'role' column will be 'LECTURER'
public class Lecturer extends User {

    @Column(unique = true)
    private String nidn; // NIDN or NIDK

    // Getters and Setters for lecturer-specific fields
    public String getNidn() { return nidn; }
    public void setNidn(String nidn) { this.nidn = nidn; }
}
