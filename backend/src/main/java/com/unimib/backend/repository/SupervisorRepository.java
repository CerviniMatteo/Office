package com.unimib.backend.repository;

import com.unimib.backend.POJO.Supervisor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * Retrieves all emails belonging to workers whose local part starts with "name.surname",
     * with or without a trailing numeric suffix (e.g. "mario.rossi@..." or "mario.rossi2@...").
     * The comparison is case-insensitive. The exact match (to exclude unrelated surnames sharing
     * the same prefix, e.g. "rossi" vs "rossini") is performed by the caller in Java.
     *
     * @param name    the exact first name to match.
     * @param surname the exact surname to match.
     * @return list of candidate emails matching the "name.surname" prefix.
     */
    @Query("SELECT w.email FROM worker w WHERE LOWER(w.email) LIKE LOWER(CONCAT(:name, '.', :surname, '%'))")
    List<String> findEmailsByNameAndSurname(@Param("name") String name, @Param("surname") String surname);
}
