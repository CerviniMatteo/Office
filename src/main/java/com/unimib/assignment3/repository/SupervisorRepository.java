package com.unimib.assignment3.repository;

import com.unimib.assignment3.POJO.Supervisor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SupervisorRepository extends JpaRepository<Supervisor, Long> {

    //Search supervisors without supervisors
    List<Supervisor> findBySupervisorIsNull();

    // Supervisors without subordinates
    @Query("SELECT s FROM supervisor s WHERE s.subordinates IS EMPTY")
    List<Supervisor> findSupervisorWithoutSubordinates();

    // Supervisors without teams
    @Query("SELECT s FROM supervisor s WHERE s.supervisedTeams IS EMPTY")
    List<Supervisor> findSupervisorWithoutSupervisedTeams();
}
