package com.unimib.assignment3.service;

import com.unimib.assignment3.POJO.Supervisor;
import com.unimib.assignment3.POJO.Worker;
import com.unimib.assignment3.constants.CommonConstants;
import com.unimib.assignment3.constants.SupervisorConstants;
import com.unimib.assignment3.enums.WorkerRole;
import com.unimib.assignment3.repository.SupervisorRepository;
import com.unimib.assignment3.repository.WorkerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static com.unimib.assignment3.POJO.Worker.generateEmail;
import static com.unimib.assignment3.constants.CommonConstants.*;
import static com.unimib.assignment3.constants.EmployeeConstants.NOT_A_MANAGER;
import static com.unimib.assignment3.constants.EmployeeConstants.SALARY_MUST_BE_AT_LEAST_ROLE_MINIMUM;
import static com.unimib.assignment3.constants.SupervisorConstants.*;
import static com.unimib.assignment3.constants.TeamConstants.*;
import static com.unimib.assignment3.constants.WorkerConstants.NULL_WORKER_ID;
import static com.unimib.assignment3.constants.WorkerConstants.WORKER_NOT_FOUND;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

/**
 * Service class for managing {@link Supervisor} entities.
 * <p>
 * Provides CRUD operations, assignment and removal of subordinates, and
 * ensures that cyclic supervisor-subordinate relationships are prevented.
 */
@Service
public class SupervisorService{
    @Autowired
    private SupervisorRepository supervisorRepository;
    @Autowired
    private WorkerRepository workerRepository;

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
     * @param workerRole the supervisor's role (must not be null)
     * @return a new Supervisor instance
     * @throws IllegalArgumentException if the name, surname, or role is null
     */
    public Supervisor createSupervisor(@NonNull String name, @NonNull String surname, @NonNull WorkerRole workerRole) {
        checkRole(workerRole);
        assertNotNull(name, NULL_NAME);
        assertNotNull(surname, NULL_SURNAME);
        return new Supervisor(name, surname, workerRole);
    }

    /**
     * Creates a new supervisor with the given name, surname, salary, and role.
     *
     * @param name          the supervisor's name (must not be null)
     * @param surname       the supervisor's surname (must not be null)
     * @param monthlySalary the supervisor's monthly salary
     * @param workerRole  the supervisor's role (must not be null)
     * @return a new Supervisor instance
     * @throws IllegalArgumentException if the name, surname, or role is null
     */
    public Supervisor createSupervisor(@NonNull String name, @NonNull String surname, double monthlySalary, @NonNull WorkerRole workerRole) {
        checkRole(workerRole);
        checkSalary(monthlySalary, workerRole);
        assertNotNull(name, NULL_NAME);
        assertNotNull(surname, NULL_SURNAME);
        return new Supervisor(name, surname, monthlySalary, workerRole);
    }

    /**
     * Creates a new supervisor with full details including supervisor, subordinates, and supervised teams.
     *
     * @param name             the supervisor's name (must not be null)
     * @param surname          the supervisor's surname (must not be null)
     * @param monthlySalary    the supervisor's monthly salary
     * @param workerRole     the supervisor's role (must not be null)
     * @param supervisor       the supervisor's supervisor (must not be null)
     * @param subordinates     the list of subordinates (must not be null)
     * @return a new Supervisor instance
     * @throws IllegalArgumentException if any required argument is null
     */
    public Supervisor createSupervisor(@NonNull String name, @NonNull String surname, double monthlySalary, @NonNull WorkerRole workerRole, Supervisor supervisor, List<Supervisor> subordinates) {
        checkRole(workerRole);
        checkSalary(monthlySalary, workerRole);
        assertNotNull(name, NULL_NAME);
        assertNotNull(surname, NULL_SURNAME);
        assertNotNull(supervisor.getWorkerId(), SUPERVISOR_CANNOT_BE_NULL);
        assertNotNull(subordinates, NULL_SUBORDINATES);
        return new Supervisor(name, surname, monthlySalary, workerRole, supervisor, subordinates);
    }

    protected void checkSalary(double monthlySalary, WorkerRole workerRole) {
        if(monthlySalary < 0) {
            throw new IllegalArgumentException(SALARY_MUST_BE_POSITIVE);
        }
        if(Double.compare(monthlySalary, workerRole.getMonthlySalary()) < 0) {
            throw new IllegalArgumentException(SALARY_MUST_BE_AT_LEAST_ROLE_MINIMUM + workerRole);
        }
    }

    /**
     * Validates that the role is at least {@link WorkerRole#SW_ARCHITECT}.
     *
     * @param workerRole the role to check
     * @throws IllegalArgumentException if the role is below SW_ARCHITECT
     */
    protected void checkRole(WorkerRole workerRole) {
        if(workerRole.compareTo(WorkerRole.SW_ARCHITECT) < 0) {
            throw new IllegalArgumentException(SupervisorConstants.SUPERVISOR_AT_LEAST_SW_ARCHITECT);
        }
    }

    /**
     * Ensure the worker's email is unique by appending a counter if necessary.
     * @param supervisor the worker whose email needs to be checked
     * @throws IllegalArgumentException if the worker is null
     */
    private void checkUniqueEmail(Supervisor supervisor) {
        assertNotNull(supervisor, NULL_SUPERVISOR);
        int emailCounter = supervisorRepository.countEmailsStartingWithEmailPrefix(supervisor.getName());
        if(emailCounter != 0) {
            supervisor.setEmail(generateEmail(supervisor.getName()+emailCounter, supervisor.getSurname()));
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
     * @param workerId the ID of the supervisor to delete (must not be null)
     * @throws IllegalArgumentException if the supervisorId is null
     */
    public void deleteWorkerById(@NonNull Long workerId) {
        assertNotNull(workerId, NULL_WORKER_ID);
        workerRepository.deleteById(workerId);
    }

    /**
     * Fire (delete) a single worker.
     * Only a manager can perform this action.
     *
     * @param managerId  the manager's ID
     * @param workerId the worker's ID to fire
     * @throws IllegalArgumentException if the ids are null, if the manager is not authorized
     * and if the worker does not exist
     */
    public void fireWorker(@NonNull Long managerId,@NonNull Long workerId) {
        checkManagerRole(managerId);
        deleteWorkerById(workerId);
    }

    /**
     * Fire multiple workers at once.
     * Only a manager can perform this action.
     *
     * @param managerId the manager's ID
     * @param workers list of workers to fire
     * @throws IllegalArgumentException if the ids are null, if the manager is not authorized
     * and if any of the workers do not exist or the workers list is null
     */
    public void fireWorkers(@NonNull Long managerId,@NonNull List<? extends Worker> workers) {
        checkManagerRole(managerId);
        workers.forEach(worker -> deleteWorkerById(worker.getWorkerId()));
    }

    /**
     * Find workers with a specific monthly salary.
     * Only accessible by managers.
     *
     * @param managerId    the manager's ID
     * @param monthlySalary the target salary
     * @throws IllegalArgumentException if the managerId is null and if the manager is not authorized
     * and if the manager does not exist
     * @return list of workers with that salary
     */
    public List<? extends Worker> findWorkersByMonthlySalary(@NonNull Long managerId, double monthlySalary) {
        checkManagerRole(managerId);
        return workerRepository.findWorkerByMonthlySalary(monthlySalary);
    }

    /**
     * Find workers by salary and sort by role ascending.
     * Only accessible by managers.
     *
     * @param managerId    the manager's ID
     * @param monthlySalary the target salary
     * @throws IllegalArgumentException if the managerId is null and if the manager is not authorized
     * and if the manager does not exist
     * @return list of workers with that salary ordered by role ascending
     */
    public List<? extends Worker> findWorkersByMonthlySalaryOrderByWorkerRoleAsc(@NonNull Long managerId, double monthlySalary) {
        checkManagerRole(managerId);
        return workerRepository.findWorkerByMonthlySalaryOrderByWorkerRoleAsc(monthlySalary);
    }

    /**
     * Find workers by salary and sort by role ascending.
     * Only accessible by managers.
     *
     * @param managerId    the manager's ID
     * @param monthlySalary the target salary
     * @throws IllegalArgumentException if the managerId is null and if the manager is not authorized
     * and if the manager does not exist
     * @return list of workers with that salary ordered by role descending
     */
    public List<? extends Worker> findWorkersByMonthlySalaryOrderByWorkerRoleDesc(@NonNull Long managerId, double monthlySalary) {
        checkManagerRole(managerId);
        return workerRepository.findWorkerByMonthlySalaryOrderByWorkerRoleDesc(monthlySalary);
    }

    /**
     * Find workers by role.
     * Only accessible by managers.
     *
     * @param managerId   the manager's ID
     * @param workerRole the target role
     *
     * @throws IllegalArgumentException if the managerId is null and if the manager is not authorized
     * and if the manager does not exist, and if the workerRole is null
     * @return list of workers with that role
     */
    public List<? extends Worker> findWorkersByWorkerRole(@NonNull Long managerId, @NonNull WorkerRole workerRole) {
        checkManagerRole(managerId);
        Objects.requireNonNull(workerRole, CommonConstants.NULL_WORKER_ROLE);
        return workerRepository.findWorkerByWorkerRole(workerRole);
    }

    /**
     * Find workers by role.
     * Only accessible by managers.
     *
     * @param managerId   the manager's ID
     * @param workerRole the target role
     *
     * @throws IllegalArgumentException if the managerId is null and if the manager is not authorized
     * and if the manager does not exist, and if the workerRole is null
     * @return list of workers with that role ordered by salary ascending
     */
    public List<? extends Worker> findWorkersByWorkerRoleOrderByMonthlySalaryAsc(@NonNull Long managerId, @NonNull WorkerRole workerRole) {
        checkManagerRole(managerId);
        Objects.requireNonNull(workerRole, CommonConstants.NULL_WORKER_ROLE);
        return workerRepository.findWorkerByWorkerRoleOrderByMonthlySalaryAsc(workerRole);
    }

    /**
     * Find workers by role.
     * Only accessible by managers.
     *
     * @param managerId   the manager's ID
     * @param workerRole the target role
     *
     * @throws IllegalArgumentException if the managerId is null and if the manager is not authorized
     * and if the manager does not exist, and if the workerRole is null
     * @return list of workers with that role ordered by salary descending
     */
    public List<? extends Worker> findWorkersByWorkerRoleOrderByMonthlySalaryDesc(@NonNull Long managerId, @NonNull WorkerRole workerRole) {
        checkManagerRole(managerId);
        assertNotNull(workerRole, NULL_WORKER_ROLE);
        return workerRepository.findWorkerByWorkerRoleOrderByMonthlySalaryDesc(workerRole);
    }

    /**
     * Update an worker's monthly salary.
     * Only a manager can perform this operation.
     *
     * @param managerId    the manager's ID
     * @param workerId   the worker's ID
     * @param monthlySalary new monthly salary
     * @throws IllegalArgumentException if the ids are null, if the manager is not authorized and if the worker does not exist
     */
    public void updateMonthlySalaryById(@NonNull Long managerId,@NonNull Long workerId, double monthlySalary) {
        checkManagerRole(managerId);
        Worker worker = getWorkerOrThrow(workerId);
        worker.setMonthlySalary(monthlySalary);
    }

    /**
     * Update an worker's role.
     * Only a manager can perform this operation.
     *
     * @param managerId    the manager's ID
     * @param workerId   the worker's ID
     * @param workerRole the new role
     * @throws IllegalArgumentException if the ids are null, if the manager is not authorized, if the worker does not exist
     * and if the workerRole is null, and if the workerRole is null
     */
    public void updateWorkerRoleById(@NonNull Long managerId,@NonNull Long workerId,@NonNull WorkerRole workerRole) {
        checkManagerRole(managerId);
        assertNotNull(workerRole, CommonConstants.NULL_WORKER_ROLE);
        Worker worker = getWorkerOrThrow(workerId);
        worker.setWorkerRole(workerRole);
    }
    /**
     * Check that the given worker is a manager.
     *
     * @param managerId the worker ID to check
     * @throws IllegalArgumentException if the worker is not a manager
     */
    private void checkManagerRole(Long managerId) {
        assertNotNull(managerId, NULL_SUPERVISOR_ID);
        Worker manager;
        try {
            manager = getSupervisorOrThrow(managerId);
        }catch (EntityNotFoundException e) {
            manager = getWorkerOrThrow(managerId);
        }
        if (!manager.getWorkerRole().equals(WorkerRole.MANAGER)) {
            throw new IllegalArgumentException(NOT_A_MANAGER);
        }
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
     * Helper method to retrieve a worker by ID or throw an exception if not found.
     *
     * @param workerId the ID of the supervisor (must not be null)
     * @return the supervisor
     * @throws EntityNotFoundException if the supervisor does not exist
     */
    private Worker getWorkerOrThrow(Long workerId) {
        assertNotNull(workerId, NULL_WORKER_ID);
        return workerRepository.findById(workerId)
                .orElseThrow(() -> new EntityNotFoundException(WORKER_NOT_FOUND));
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
