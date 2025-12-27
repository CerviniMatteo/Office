package com.unimib.assignment3.repository;

import com.unimib.assignment3.POJO.Corso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorsoRepository extends JpaRepository<Corso, Long> {
    
}
