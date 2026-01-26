package com.unimib.assignment3.service;

import com.unimib.assignment3.POJO.Supervisor;
import com.unimib.assignment3.constants.SupervisorConstants;
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
import static com.unimib.assignment3.constants.TeamConstants.*;

/**
 * Service class for managing {@link Supervisor} entities.
 * <p>
 * Provides CRUD operations, assignment and removal of subordinates, and
 * ensures that cyclic supervisor-subordinate relationships are prevented.
 */
@Service
public class SupervisorService extends EmployeeService {

    @Autowired
    private SupervisorRepository supervisorRepository;

    /**
     * Saves a supervisor to the database.
     * <p>
     * Checks for email uniqueness before saving.
     *
     * @param supervisor the supervisor to save (must not be null)
     * @return the saved supervisor entity
     * @throws IllegalArgumentException if the supervisor is null or the email is not unique
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
     * Creates a new supervisor with the given name and surname.
     *
     * @param name    the supervisor's name (must not be null)
     * @param surname the supervisor's surname (must not be null)
     * @return a new Supervisor instance
     * @throws IllegalArgumentException if the name or surname is null
     */
    public Supervisor createSupervisor(@NonNull String name, @NonNull String surname) {
        assertNotNull(name, NULL_NAME);
        assertNotNull(surname, NULL_SURNAME);
        return new Supervisor(name, surname);
    }

    /**
     * Creates a new supervisor with the given name, surname, and role.
     *
     * @param name         the supervisor's name (must not be null)
     * @param surname      the supervisor's surname (must not be null)
     * @param employeeRole the supervisor's role (must not be null)
     * @return a new Supervisor instance
     * @throws IllegalArgumentException if the name, surname, or role is null
     */
    public Supervisor createSupervisor(@NonNull String name, @NonNull String surname, @NonNull EmployeeRole employeeRole) {
        checkRole(employeeRole);
        assertNotNull(name, NULL_NAME);
        assertNotNull(surname, NULL_SURNAME);
        return new Supervisor(name, surname, employeeRole);
    }

    /**
     * Creates a new supervisor with the given name, surname, salary, and role.
     *
     * @param name          the supervisor's name (must not be null)
     * @param surname       the supervisor's surname (must not be null)
     * @param monthlySalary the supervisor's monthly salary
     * @param employeeRole  the supervisor's role (must not be null)
     * @return a new Supervisor instance
     * @throws IllegalArgumentException if the name, surname, or role is null
     */
    public Supervisor createSupervisor(@NonNull String name, @NonNull String surname, double monthlySalary, @NonNull EmployeeRole employeeRole) {
        checkRole(employeeRole);
        checkSalary(monthlySalary, employeeRole);
        assertNotNull(name, NULL_NAME);
        assertNotNull(surname, NULL_SURNAME);
        return new Supervisor(name, surname, monthlySalary, employeeRole);
    }

    /**
     * Creates a new supervisor with full details including supervisor, subordinates, and supervised teams.
     *
     * @param name             the supervisor's name (must not be null)
     * @param surname          the supervisor's surname (must not be null)
     * @param monthlySalary    the supervisor's monthly salary
     * @param employeeRole     the supervisor's role (must not be null)
     * @param supervisor       the supervisor's supervisor (must not be null)
     * @param subordinates     the list of subordinates (must not be null)
     * @return a new Supervisor instance
     * @throws IllegalArgumentException if any required argument is null
     */
    public Supervisor createSupervisor(@NonNull String name, @NonNull String surname, double monthlySalary, @NonNull EmployeeRole employeeRole, Supervisor supervisor, List<Supervisor> subordinates) {
        checkRole(employeeRole);
        checkSalary(monthlySalary, employeeRole);
        assertNotNull(name, NULL_NAME);
        assertNotNull(surname, NULL_SURNAME);
        assertNotNull(supervisor.getPersonId(), SUPERVISOR_CANNOT_BE_NULL);
        assertNotNull(subordinates, NULL_SUBORDINATES);
        return new Supervisor(name, surname, monthlySalary, employeeRole, supervisor, subordinates);
    }

    /**
     * Validates that the role is at least {@link EmployeeRole#SW_ARCHITECT}.
     *
     * @param employeeRole the role to check
     * @throws IllegalArgumentException if the role is below SW_ARCHITECT
     */
    protected void checkRole(EmployeeRole employeeRole) {
        if(employeeRole.compareTo(EmployeeRole.SW_ARCHITECT) < 0) {
            throw new IllegalArgumentException(SupervisorConstants.SUPERVISOR_AT_LEAST_SW_ARCHITECT);
        }
    }

    /**
     * Finds a supervisor by ID.
     *
     * @param supervisorId the supervisor's ID (must not be null)
     * @return an Optional containing the supervisor if found
     * @throws EntityNotFoundException if the supervisor does not exist
     */
    public Optional<Supervisor> findSupervisorById(@NonNull Long supervisorId) {
        return Optional.of(getSupervisorOrThrow(supervisorId));
    }

    /**
     * Retrieves all supervisors.
     *
     * @return a list of all supervisors
     */
    public List<Supervisor> findAllSupervisors() {
        return supervisorRepository.findAll();
    }

    /**
     * Deletes a supervisor by ID.
     *
     * @param supervisorId the ID of the supervisor to delete (must not be null)
     * @throws IllegalArgumentException if the supervisorId is null
     */
    public void deleteSupervisorById(@NonNull Long supervisorId) {
        assertNotNull(supervisorId, NULL_SUPERVISOR_ID);
        supervisorRepository.deleteById(supervisorId);
    }

    /**
     * Assigns a subordinate to a supervisor.
     * <p>
     * Prevents cyclic supervisor-subordinate relationships. If a cycle is detected,
     * the assignment is reverted and an {@link IllegalStateException} is thrown.
     *
     * @param supervisorId  the supervisor's ID (must not be null)
     * @param subordinateId the subordinate's ID (must not be null)
     * @throws IllegalStateException if the assignment would create a cyclic hierarchy
     */
    public void assignSubordinate(@NonNull Long supervisorId,@NonNull Long subordinateId) {
        Supervisor supervisor = getSupervisorOrThrow(supervisorId);
        Supervisor subordinate = getSupervisorOrThrow(subordinateId);

        supervisor.addSubordinate(subordinate);

        if (subordinate.getSupervisor() != null && supervisor.getSupervisor() != null) {
            if (createsSupervisorsLoop(supervisor, subordinate)) {
                removeSubordinate(supervisorId, subordinateId);
                throw new IllegalStateException(CANNOT_HAVE_LOOP_SUBORDINATION);
            }
        }
    }

    /**
     * Removes a subordinate from a supervisor.
     *
     * @param supervisorId  the supervisor's ID (must not be null)
     * @param subordinateId the subordinate's ID (must not be null)
     */
    public void removeSubordinate(@NonNull Long supervisorId,@NonNull Long subordinateId) {
        Supervisor supervisor = getSupervisorOrThrow(supervisorId);
        Supervisor subordinate = getSupervisorOrThrow(subordinateId);

        supervisor.removeSubordinate(subordinate);
    }

    /**
     * Finds supervisors who do not have a supervisor.
     * <p>
     * These are considered the top-level supervisors in the hierarchy.
     *
     * @return a list of top-level supervisors
     */
    public List<Supervisor> findSupervisorsWithoutSupervisor() {
        return supervisorRepository.findBySupervisorIsNull();
    }

    /**
     * Finds supervisors who do not have any subordinates.
     * <p>
     * Useful to determine potential candidates to assign new subordinates.
     *
     * @return a list of supervisors without subordinates
     */
    public List<Supervisor> findSupervisorsWithoutSubordinates() {
        return supervisorRepository.findSupervisorWithoutSubordinates();
    }

    /**
     * Finds supervisors who do not supervise any teams.
     *
     * @return a list of supervisors without supervised teams
     */
    public List<Supervisor> findSupervisorsWithoutSupervisedTeam() {
        return supervisorRepository.findSupervisorWithoutSupervisedTeam();
    }

    /**
     * Helper method to retrieve a supervisor by ID or throw an exception if not found.
     *
     * @param supervisorId the ID of the supervisor (must not be null)
     * @return the supervisor
     * @throws EntityNotFoundException if the supervisor does not exist
     */
    private Supervisor getSupervisorOrThrow(Long supervisorId) {
        assertNotNull(supervisorId, NULL_SUPERVISOR_ID);
        return supervisorRepository.findById(supervisorId)
                .orElseThrow(() -> new EntityNotFoundException(SUPERVISOR_NOT_FOUND));
    }

    /**
     * Checks whether assigning a subordinate would create a loop in the supervisor hierarchy.
     *
     * @param supervisor  the supervisor
     * @param subordinate the subordinate
     * @return true if a cyclic relationship would be created
     */
    private boolean createsSupervisorsLoop(Supervisor supervisor, Supervisor subordinate) {
        Supervisor current = supervisor;
        while (current != null) {
            if (current.equals(subordinate)) {
                return true;
            }
            current = current.getSupervisor();
        }
        return false;
    }
}
