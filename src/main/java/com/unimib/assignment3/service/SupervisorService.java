package com.unimib.assignment3.service;

import com.unimib.assignment3.POJO.Supervisor;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.repository.SupervisorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import static com.unimib.assignment3.constants.CommonConstants.*;
import static com.unimib.assignment3.constants.SupervisorConstants.*;

/**
 * Service class for managing Supervisore entities.
 * <p>
 * Provides CRUD operations, assigning/removing subordinates, and checks to prevent
 * cyclic supervisor-subordinate relationships.
 */
@Service
public class SupervisorService extends EmployeeService {

    @Autowired
    private SupervisorRepository supervisorRepository;

    /**
     * Save a single supervisor to the database and handles email uniqueness.
     *
     * @param supervisor the supervisor to save
     * @throws IllegalArgumentException if email is not unique and if supervisor is null
     * @return the saved supervisor
     */
    public Supervisor saveSupervisor(@NonNull Supervisor supervisor) {
        checkUniqueEmail(supervisor);
        try {
            return supervisorRepository.saveAndFlush(supervisor);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(EMAIL_HAVE_TO_BE_UNIQUE);
        }
    }


    /**
     * Create  a new supervisor with the given name and surname.
     *
     * @param name    the name of the supervisor (must not be null)
     * @param surname the surname of the supervisor (must not be null)
     * @return the created supervisor entity
     * @throws IllegalArgumentException if the name or surname is null
     */
    public Supervisor createSupervisor(@NonNull String name, @NonNull String surname) {
        assertNotNull(name, NULL_NAME);
        assertNotNull(surname, NULL_SURNAME);
        return new Supervisor(name, surname);
    }

    /**
     * Create  a new supervisor with the given name, surname, monthly salary, and employee role.
     *
     * @param name          the name of the supervisor (must not be null)
     * @param surname       the surname of the supervisor (must not be null)
     * @param employeeRole  the role of the supervisor (must not be null)
     * @return the created supervisor entity
     * @throws IllegalArgumentException if the name, surname, or employee role is null
     */
    public Supervisor createSupervisor(@NonNull String name, @NonNull String surname, @NonNull EmployeeRole employeeRole) {
        assertNotNull(name, NULL_NAME);
        assertNotNull(surname, NULL_SURNAME);
        return new Supervisor(name, surname, employeeRole);
    }

    /**
     * Create  a new supervisor with the given name, surname, monthly salary, and employee role.
     *
     * @param name          the name of the supervisor (must not be null)
     * @param surname       the surname of the supervisor (must not be null)
     * @param monthlySalary the monthly salary of the supervisor
     * @param employeeRole  the role of the supervisor (must not be null)
     * @return the created supervisor entity
     * @throws IllegalArgumentException if the name, surname, or employee role is null
     */
    public Supervisor createSupervisor(@NonNull String name, @NonNull String surname, double monthlySalary, @NonNull EmployeeRole employeeRole) {
        assertNotNull(name, NULL_NAME);
        assertNotNull(surname, NULL_SURNAME);
        return new Supervisor(name, surname, monthlySalary, employeeRole);
    }

    /**
     * Find a supervisor by ID.
     *
     * @param supervisorId the ID of the supervisor
     * @return Optional containing the supervisor if found
     * @throws IllegalArgumentException if the supervisor does not exist
     */
    public Optional<Supervisor> findSupervisorById(@NonNull Long supervisorId) {
        return Optional.of(getSupervisorOrThrow(supervisorId));
    }

    /**
     * Retrieve all supervisors.
     *
     * @return a list of all supervisors
     */
    public List<Supervisor> findAllSupervisors() {
        return supervisorRepository.findAll();
    }

    /**
     * Delete a supervisor by ID.
     *
     * @param supervisorId the ID of the supervisor to delete
     * @throws IllegalArgumentException if the supervisorId is null
     */
    public void deleteSupervisorById(@NonNull Long supervisorId) {
        assertNotNull(supervisorId, NULL_SUPERVISOR_ID);
        supervisorRepository.deleteById(supervisorId);
    }

    /**
     * Assign a subordinate to a supervisor.
     * <p>
     * Also checks for loops in the supervisor-subordinate hierarchy. If a loop is detected,
     * the assignment is reverted and an exception is thrown.
     *
     * @param supervisorId  the supervisor's ID
     * @param subordinateId the subordinate's ID
     * @throws IllegalStateException if the assignment creates a cyclic hierarchy
     */
    public void assignSubordinate(@NonNull Long supervisorId,@NonNull Long subordinateId) {
        Supervisor supervisor = getSupervisorOrThrow(supervisorId);
        Supervisor subordinate = getSupervisorOrThrow(subordinateId);

        // Add subordinate using helper method
        supervisor.addSubordinate(subordinate);

        // Check for loops in the hierarchy
        if (subordinate.getSupervisor() != null && supervisor.getSupervisor() != null) {
            if (createsSupervisorsLoop(supervisor, subordinate)) {
                // Undo assignment
                removeSubordinate(supervisorId, subordinateId);
                throw new IllegalStateException(CANNOT_HAVE_LOOP_SUBORDINATION);
            }
        }
    }

    /**
     * Remove a subordinate from a supervisor.
     *
     * @param supervisorId  the supervisor's ID
     * @param subordinateId the subordinate's ID
     * @throws IllegalArgumentException if the supervisor or subordinate does not exist
     */
    public void removeSubordinate(@NonNull Long supervisorId,@NonNull Long subordinateId) {
        Supervisor supervisor = getSupervisorOrThrow(supervisorId);
        Supervisor subordinate = getSupervisorOrThrow(subordinateId);

        supervisor.removeSubordinate(subordinate);
    }

    /**
     * Find supervisors who do not have any supervisors.
     * As it is the top of the hierarchy.
     * Needed to give top role.
     *
     * @return a list of supervisors without subordinates
     */
    public List<Supervisor> findSupervisorsWithoutSupervisor() {
        return supervisorRepository.findBySupervisorIsNull();
    }

    /**
     * Find supervisors who do not have any subordinates.
     * In order to know to whom assign new subordinates.
     *
     * @return a list of supervisors without subordinates
     */
    public List<Supervisor> findSupervisorsWithoutSubordinates() {
        return supervisorRepository.findSupervisorWithoutSubordinates();
    }

    /**
     * Find supervisors who do not have any supervised team.
     * In order to know to whom assign a new team.
     *
     * @return a list of supervisors without subordinates
     */
    public List<Supervisor> findSupervisorsWithoutSupervisedTeam() {
        return supervisorRepository.findSupervisorWithoutSubordinates();
    }


    /**
     * Helper method to retrieve a supervisor or throw an exception if not found.
     *
     * @param supervisorId the ID of the supervisor
     * @return the supervisor
     * @throws IllegalArgumentException if the supervisor does not exist
     */
    private Supervisor getSupervisorOrThrow(Long supervisorId) {
        assertNotNull(supervisorId, NULL_SUPERVISOR_ID);
        return supervisorRepository.findById(supervisorId)
                .orElseThrow(() -> new EntityNotFoundException(NULL_SUPERVISOR));
    }

    /**
     * Check whether assigning a subordinate creates a loop in the hierarchy.
     *
     * @param supervisor  the supervisor
     * @param subordinate the subordinate
     * @return true if a loop would be created
     */
    private boolean createsSupervisorsLoop(Supervisor supervisor, Supervisor subordinate) {
        Supervisor current = supervisor;
        while (current != null) {
            if (current.equals(subordinate)) {
                return true; // loop detected
            }
            current = current.getSupervisor();
        }
        return false;
    }

}
