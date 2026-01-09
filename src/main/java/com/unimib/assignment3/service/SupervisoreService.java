package com.unimib.assignment3.service;

import com.unimib.assignment3.POJO.Supervisore;
import com.unimib.assignment3.repository.SupervisorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import static com.unimib.assignment3.constants.EmployeeConstants.NULL_EMPLOYEE;
import static com.unimib.assignment3.constants.SupervisorConstants.*;

/**
 * Service class for managing Supervisore entities.
 * <p>
 * Provides CRUD operations, assigning/removing subordinates, and checks to prevent
 * cyclic supervisor-subordinate relationships.
 */
@Service
public class SupervisoreService extends DipendenteService{

    @Autowired
    private SupervisorRepository supervisorRepository;

    /**
     * Save a supervisor entity to the repository.
     *
     * @param supervisor the supervisor to save
     * @return the saved supervisor
     */
    public Supervisore saveSupervisor(Supervisore supervisor) {
        return supervisorRepository.save(supervisor);
    }

    /**
     * Find a supervisor by ID.
     *
     * @param supervisorId the ID of the supervisor
     * @return Optional containing the supervisor if found
     */
    public Optional<Supervisore> findSupervisorById(Long supervisorId) {
        return supervisorRepository.findById(supervisorId);
    }

    /**
     * Retrieve all supervisors.
     *
     * @return a list of all supervisors
     */
    public List<Supervisore> findAllSupervisors() {
        return supervisorRepository.findAll();
    }

    /**
     * Delete a supervisor by ID.
     *
     * @param supervisorId the ID of the supervisor to delete
     */
    public void deleteSupervisorById(Long supervisorId) {
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
    public void assignSubordinate(Long supervisorId, Long subordinateId) {
        Supervisore supervisor = getSupervisorOrThrow(supervisorId);
        Supervisore subordinate = getSupervisorOrThrow(subordinateId);

        // Add subordinate using helper method
        supervisor.addSubordinate(subordinate);

        // Check for loops in the hierarchy
        if (subordinate.getSupervisore() != null && supervisor.getSupervisore() != null) {
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
     */
    public void removeSubordinate(Long supervisorId, Long subordinateId) {
        Supervisore supervisor = getSupervisorOrThrow(supervisorId);
        Supervisore subordinate = getSupervisorOrThrow(subordinateId);

        supervisor.removeSubordinate(subordinate);
    }

    /**
     * Find supervisors who do not have any supervisors.
     * As it is the top of the hierarchy.
     * Needed to give top role.
     *
     * @return a list of supervisors without subordinates
     */
    public List<Supervisore> findSupervisorsWithoutSupervisor() {
        return supervisorRepository.findBySupervisoreIsNull();
    }

    /**
     * Find supervisors who do not have any subordinates.
     * In order to know to whom assign new subordinates.
     *
     * @return a list of supervisors without subordinates
     */
    public List<Supervisore> findSupervisorsWithoutSubordinates() {
        return supervisorRepository.findSupervisorWithoutSubordinates();
    }

    /**
     * Find supervisors who do not have any supervised team.
     * In order to know to whom assign a new team.
     *
     * @return a list of supervisors without subordinates
     */
    public List<Supervisore> findSupervisorsWithoutSupervisionedTeam() {
        return supervisorRepository.findSupervisorWithoutSubordinates();
    }

    /**
     * Helper method to retrieve a supervisor or throw an exception if not found.
     *
     * @param supervisorId the ID of the supervisor
     * @return the supervisor
     * @throws IllegalArgumentException if the supervisor does not exist
     */
    private Supervisore getSupervisorOrThrow(Long supervisorId) {
        return supervisorRepository.findById(supervisorId)
                .orElseThrow(() -> new IllegalArgumentException(NULL_EMPLOYEE));
    }

    /**
     * Check whether assigning a subordinate creates a loop in the hierarchy.
     *
     * @param supervisor  the supervisor
     * @param subordinate the subordinate
     * @return true if a loop would be created
     */
    private boolean createsSupervisorsLoop(Supervisore supervisor, Supervisore subordinate) {
        Supervisore current = supervisor;
        while (current != null) {
            if (current.equals(subordinate)) {
                return true; // loop detected
            }
            current = current.getSupervisore();
        }
        return false;
    }

}
