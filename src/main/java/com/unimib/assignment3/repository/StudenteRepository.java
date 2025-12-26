package com.unimib.assignment3.repository;

import com.unimib.assignment3.POJO.Studente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudenteRepository extends JpaRepository<Studente, Long> {
}
