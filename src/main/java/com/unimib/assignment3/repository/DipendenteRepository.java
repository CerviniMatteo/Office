package com.unimib.assignment3.repository;

import com.unimib.assignment3.POJO.Dipendente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DipendenteRepository extends JpaRepository<Dipendente, Long> {
}
