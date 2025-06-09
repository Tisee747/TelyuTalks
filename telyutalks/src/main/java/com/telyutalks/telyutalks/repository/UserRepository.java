package com.telyutalks.telyutalks.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.telyutalks.telyutalks.model.Lecturer;
import com.telyutalks.telyutalks.model.Student;
import com.telyutalks.telyutalks.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    List<User> findByNamaContainingIgnoreCaseOrEmailContainingIgnoreCase(String nama, String email);

    @Query("SELECT count(u) FROM User u WHERE TYPE(u) = :role")
    long countByRole(@Param("role") Class<? extends User> role);

    default long countByRole(String roleName) {
        if ("STUDENT".equalsIgnoreCase(roleName)) {
            return countByRole(Student.class);
        } else if ("LECTURER".equalsIgnoreCase(roleName)) {
            return countByRole(Lecturer.class);
        }
        return 0;
    }
}
