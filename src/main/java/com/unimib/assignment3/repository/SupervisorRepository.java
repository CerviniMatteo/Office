package com.unimib.assignment3.repository;

import com.unimib.assignment3.POJO.Supervisor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository interface for managing {@link Supervisor} entities.
 * Extends JpaRepository to provide CRUD operations and custom queries.
 */
public interface SupervisorRepository extends JpaRepository<Supervisor, Long> {

    /**
     * Retrieves all supervisors who do not have a supervisor themselves.
     *
     * @return a list of supervisors with no supervisor assigned.
     */
    List<Supervisor> findBySupervisorIsNull();

    /**
     * Retrieves all supervisors who do not have any subordinates.
     *
     * @return a list of supervisors with no subordinates.
     */
    @Query("SELECT s FROM supervisor s WHERE s.subordinates IS EMPTY")
    List<Supervisor> findSupervisorWithoutSubordinates();

    /**
     * Retrieves all supervisors who do not supervise any teams.
     *
     * @return a list of supervisors with no supervised teams.
     */
    @Query("SELECT s FROM supervisor s WHERE s.supervisedTeams IS EMPTY")
    List<Supervisor> findSupervisorWithoutSupervisedTeam();
}
