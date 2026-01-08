package com.unimib.assignment3.repository;

import com.unimib.assignment3.POJO.Supervisore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SupervisorRepository extends JpaRepository<Supervisore, Long> {

    //Search supervisors without supervisors
    List<Supervisore> findBySupervisoreIsNull();

    // Supervisors without subordinates
    @Query("SELECT s FROM supervisore s WHERE s.supervisoriSupervisionati IS EMPTY")
    List<Supervisore> findSupervisorWithoutSubordinates();

    // Supervisors without teams
    @Query("SELECT s FROM supervisore s WHERE s.teamSupervisionato IS EMPTY")
    List<Supervisore> findSupervisorWithoutSupervisionedTeams();


}
