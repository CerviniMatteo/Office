package com.unimib.assignment3.repository;

import com.unimib.assignment3.POJO.Worker;
import com.unimib.assignment3.enums.WorkerRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkerRepository extends JpaRepository<Worker, Long> {

    /**
     * Retrieves all workers with a specific monthly salary.
     *
     * @param monthlySalary the monthly salary to filter workers by.
     * @return a list of workers with the given monthly salary.
     */
    List<Worker> findWorkerByMonthlySalary(double monthlySalary);

    /**
     * Retrieves all workers with a specific monthly salary, ordered by role ascending.
     *
     * @param monthlySalary the monthly salary to filter workers by.
     * @return a list of workers ordered by role in ascending order.
     */
    List<Worker> findWorkerByMonthlySalaryOrderByWorkerRoleAsc(double monthlySalary);

    /**
     * Retrieves all workers with a specific monthly salary, ordered by role descending.
     *
     * @param monthlySalary the monthly salary to filter workers by.
     * @return a list of workers ordered by role in descending order.
     */
    List<Worker> findWorkerByMonthlySalaryOrderByWorkerRoleDesc(double monthlySalary);

    /**
     * Retrieves all workers with a specific role.
     *
     * @param workerRole the role to filter workers by.
     * @return a list of workers with the given role.
     */
    List<Worker> findWorkerByWorkerRole(WorkerRole workerRole);

    /**
     * Retrieves all workers with a specific role, ordered by monthly salary ascending.
     *
     * @param workerRole the role to filter workers by.
     * @return a list of workers ordered by monthly salary in ascending order.
     */
    List<Worker> findWorkerByWorkerRoleOrderByMonthlySalaryAsc(WorkerRole workerRole);

    /**
     * Retrieves all workers with a specific role, ordered by monthly salary descending.
     *
     * @param workerRole the role to filter workers by.
     * @return a list of workers ordered by monthly salary in descending order.
     */
    List<Worker> findWorkerByWorkerRoleOrderByMonthlySalaryDesc(WorkerRole workerRole);
}
