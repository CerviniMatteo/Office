package com.unimib.assignment3.repository;

import com.unimib.assignment3.POJO.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
