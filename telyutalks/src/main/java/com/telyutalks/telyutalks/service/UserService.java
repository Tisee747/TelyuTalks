package com.telyutalks.telyutalks.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.telyutalks.telyutalks.model.Admin;
import com.telyutalks.telyutalks.model.Lecturer;
import com.telyutalks.telyutalks.model.Student;
import com.telyutalks.telyutalks.model.User;
import com.telyutalks.telyutalks.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        String role;
        if (user instanceof Admin) {
            role = "ADMIN";
        } else if (user instanceof Student) {
            role = "STUDENT";
        } else if (user instanceof Lecturer) {
            role = "LECTURER";
        } else {
            throw new IllegalStateException("User role not recognized");
        }

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }
    
    public void registerStudent(String username, String nama, String nim, String email, String password, String fakultas, String programStudi, String angkatan) throws Exception {
        if (!email.toLowerCase().endsWith("@student.telkomuniversity.ac.id")) {
            throw new Exception("Invalid student email. Must use a @student.telkomuniversity.ac.id address.");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new Exception("Username is already taken.");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new Exception("Email is already in use.");
        }
        Student student = new Student();
        student.setUsername(username);
        student.setNama(nama);
        student.setNim(nim);
        student.setEmail(email);
        student.setPassword(passwordEncoder.encode(password));
        student.setFakultas(fakultas);
        student.setProgramStudi(programStudi);
        student.setAngkatan(angkatan);
        userRepository.save(student);
    }

    public void registerLecturer(String username, String nama, String nidn, String email, String password, String fakultas) throws Exception {
        System.out.println("[SERVICE] Memproses pendaftaran dosen: " + username);
        if (!email.toLowerCase().endsWith("@telkomuniversity.ac.id")) {
            throw new Exception("Invalid lecturer email. Must use a @telkomuniversity.ac.id address.");
       }
       if (email.toLowerCase().endsWith("@student.telkomuniversity.ac.id")) {
           throw new Exception("Student emails cannot be used to register as a lecturer.");
       }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new Exception("Username is already taken.");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new Exception("Email address is already in use.");
        }
        Lecturer lecturer = new Lecturer();
        lecturer.setUsername(username);
        lecturer.setNama(nama);
        lecturer.setNidn(nidn);
        lecturer.setEmail(email);
        lecturer.setPassword(passwordEncoder.encode(password));
        lecturer.setFakultas(fakultas);
        System.out.println("[SERVICE] Menyimpan dosen baru ke database: " + lecturer.getUsername());
        userRepository.save(lecturer);
        System.out.println("[SERVICE] Dosen berhasil disimpan!");
    }
}
