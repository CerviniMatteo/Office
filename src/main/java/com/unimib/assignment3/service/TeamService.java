package com.unimib.assignment3.service;

import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.POJO.Supervisor;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.POJO.Team;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.constants.teamConstants;
import com.unimib.assignment3.repository.EmployeeRepository;
import com.unimib.assignment3.repository.SupervisorRepository;
import com.unimib.assignment3.repository.TaskRepository;
import com.unimib.assignment3.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class TeamService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private SupervisorRepository supervisorRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private TeamRepository teamRepository;

    @Transactional
    public Team createTeam(Supervisor supervisor){
        if(supervisor == null) {
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        }
        return new Team(supervisor);
    }
    @Transactional
    public Team createTeam(List<Employee> dipendenti, Supervisor supervisor){
        if(dipendenti.isEmpty()){
            throw new IllegalArgumentException(teamConstants.LIST_IS_EMPTY);
        } else if(supervisor == null){
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        }
        List<Employee> dipendentiList = new ArrayList<>(dipendenti);
        return new Team(dipendentiList, supervisor);
    }
    @Transactional
    public Team createTeam(List<Employee> dipendenti, Supervisor supervisor, List<Task> tasks){
        if(dipendenti.isEmpty() || tasks.isEmpty()){
            throw new IllegalArgumentException(teamConstants.LIST_IS_EMPTY);
        } else if(supervisor == null){
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        }
        List<Employee> dipendentiList = new ArrayList<>(dipendenti);
        List<Task> tasksList = new ArrayList<>(tasks);
        return new Team(dipendentiList, supervisor, tasksList);
    }
    public Team saveTeam(Team team){
        if(team == null) {
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        }
        return teamRepository.saveAndFlush(team);
    }
    @Transactional
    public void deleteTeam(Team team){
        if(team == null) {
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        }
        // Remove the association of the team from its employees
        team.removeAllEmployee();

        // Remove all tasks from the team
        team.removeAllTasks();

        // Remove the team from its supervisor
        Supervisor supervisor = team.getSupervisor();
        supervisor.removeTeamSupervisionato(team);

        // Finally, delete the team
        teamRepository.delete(team);
    }
    // for now dont need getIdTeam

    // other methods as needed
    public List<Employee> getDipendentiInTeam(Team team){
        if(team == null) {
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        }
        return team.getEmployees();
    }
    @Transactional
    public void addDipendenteToTeam(Team newTeam, Employee employee){
        if(newTeam == null || employee == null) {
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        }
        Team oldTeam = employee.getEmployeeTeam();
        // if the dipendente is already in a team, remove him from the old team
        if(oldTeam != null) {
            oldTeam.removeEmployee(employee);
        }
        newTeam.addEmployee(employee);
    }
    @Transactional
    public void removeAllDipendentiFromTeam(Team team){
        if(team == null) {
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        }
        team.removeAllEmployee();
    }
    @Transactional
    public void removeDipendenteFromTeam(Team team, Employee employee){
        if(team == null || employee == null) {
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        } else if(!team.getEmployees().contains(employee)){
            throw new IllegalArgumentException(teamConstants.ENTITY_NOT_FOUND);
        }
        team.removeEmployee(employee);
    }

    public Supervisor getTeamSupervisore(Team team){
        if(team == null) {
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        }
        return team.getSupervisor();
    }
    @Transactional
    public void setTeamSupervisore(Team team, Supervisor supervisor){
        if(team == null || supervisor == null) {
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        }
        team.setSupervisor(supervisor);
    }
    public List<Task> getTeamTasks(Team team) {
        if (team == null) {
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        }
        return team.getTasks();
    }
    @Transactional
    public void addTaskToTeam(Team newTeam, Task task) {
        if (newTeam == null || task == null) {
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        }
        Team oldTeam = task.getTeamTask();
        if (oldTeam != null) {
            oldTeam.removeTask(task);
        }
        newTeam.addTask(task);
    }
    @Transactional
    public void removeAllTasksFromTeam(Team team) {
        if (team == null) {
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        }
        team.removeAllTasks();
    }
    @Transactional
    public void removeTaskFromTeam(Team team, Task task) {
        if (team == null || task == null) {
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        } else if (!team.getTasks().contains(task)) {
            throw new IllegalArgumentException(teamConstants.ENTITY_NOT_FOUND);
        }
        team.removeTask(task);
    }

    // Repository functions
    public Optional<Team> getTeamById(Long id) {
        return teamRepository.findByTeamId(id);
    }
    @Transactional
    public void deleteTeamById(Long id) {
        if(getTeamById(id).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.ENTITY_NOT_FOUND);
        }
        Team team = getTeamById(id).get();

        // Remove the association of the team from its employees
        team.removeAllEmployee();

        // Remove all tasks from the team
        team.removeAllTasks();

        // Remove the team from its supervisor
        Supervisor supervisor = team.getSupervisor();
        supervisor.removeTeamSupervisionato(team);

        teamRepository.deleteById(id);
    }
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }
    public List<Team> getTeamsBySupervisore_Id(Long idSupervisore) {
        if(supervisorRepository.findById(idSupervisore).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.ENTITY_NOT_FOUND);
        }
        return teamRepository.findBySupervisorPersonId(idSupervisore);
    }
    public Team getTeamByDipendente_Id(Long idDipendente) {
        if(employeeRepository.findById(idDipendente).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.ENTITY_NOT_FOUND);
        }
        return teamRepository.findByEmployeesPersonId(idDipendente);
    }
    public Team getTeamByTask_Id(Long idTask) {
        if(taskRepository.findById(idTask).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.ENTITY_NOT_FOUND);
        }
        return teamRepository.findByTasksTaskId(idTask);
    }
    public List<Task> getTasksByTeamId(Long idTeam) {
        if(getTeamById(idTeam).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.ENTITY_NOT_FOUND);
        }
        return teamRepository.findTasksByTeamId(idTeam);
    }
    public Supervisor getSupervisoreByTeamId(Long idTeam) {
        if(getTeamById(idTeam).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.ENTITY_NOT_FOUND);
        }
        return teamRepository.findSupervisorByTeamId(idTeam);
    }
    public List<Employee> getDipendentiByTeamId(Long idTeam) {
        if(getTeamById(idTeam).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.ENTITY_NOT_FOUND);
        }
        return teamRepository.findEmployeesByTeamId(idTeam);
    }

    public List<Task> getTasksInTeamIdByTaskState(Long idTeam, TaskState taskState) {
        if(getTeamById(idTeam).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.ENTITY_NOT_FOUND);
        } else if(taskState == null) {
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        }
        return teamRepository.findTasksInTeamIdByTaskState(idTeam, taskState);
    }
    public List<Employee> getDipendentiInTeamIdWithSalaryGreaterThan(Long idTeam, Double salary) {
        if(getTeamById(idTeam).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.ENTITY_NOT_FOUND);
        } else if (salary <= 0) {
            throw new IllegalArgumentException(teamConstants.THIS_VALUE_MUST_BE_POSITIVE);
        }
        return teamRepository.findEmployeesInTeamIdWithSalaryGreaterThan(idTeam, salary);
    }
    public List<Employee> getDipendentiInTeamIdWithSalaryLessThan(Long idTeam, Double salary) {
        if (getTeamById(idTeam).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.ENTITY_NOT_FOUND);
        } else if (salary <= 0) {
            throw new IllegalArgumentException(teamConstants.THIS_VALUE_MUST_BE_POSITIVE);
        }
        return teamRepository.findEmployeesInTeamIdWithSalaryLessThan(idTeam, salary);
    }
    public List<Employee> getDipendentiInTeamIdWithGrado(Long idTeam, EmployeeRole employeeRole) {
        if(getTeamById(idTeam).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.ENTITY_NOT_FOUND);
        } else if(employeeRole == null) {
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        }
        return teamRepository.findEmployeesInTeamIdWithGrado(idTeam, employeeRole);
    }
}
