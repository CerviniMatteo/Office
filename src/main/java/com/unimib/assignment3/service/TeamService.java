package com.unimib.assignment3.service;

import com.unimib.assignment3.POJO.Dipendente;
import com.unimib.assignment3.POJO.Supervisore;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.POJO.Team;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.constants.teamConstants;
import com.unimib.assignment3.repository.DipendenteRepository;
import com.unimib.assignment3.repository.SupervisoreRepository;
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
    private SupervisoreRepository supervisoreRepository;
    @Autowired
    private DipendenteRepository dipendenteRepository;
    @Autowired
    private TeamRepository teamRepository;

    @Transactional
    public Team createTeam(Supervisore supervisore){
        if(supervisore == null) {
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        }
        return new Team(supervisore);
    }
    @Transactional
    public Team createTeam(List<Dipendente> dipendenti, Supervisore supervisore){
        if(dipendenti.isEmpty()){
            throw new IllegalArgumentException(teamConstants.LIST_IS_EMPTY);
        } else if(supervisore == null){
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        }
        List<Dipendente> dipendentiList = new ArrayList<>(dipendenti);
        return new Team(dipendentiList, supervisore);
    }
    @Transactional
    public Team createTeam(List<Dipendente> dipendenti, Supervisore supervisore, List<Task> tasks){
        if(dipendenti.isEmpty() || tasks.isEmpty()){
            throw new IllegalArgumentException(teamConstants.LIST_IS_EMPTY);
        } else if(supervisore == null){
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        }
        List<Dipendente> dipendentiList = new ArrayList<>(dipendenti);
        List<Task> tasksList = new ArrayList<>(tasks);
        return new Team(dipendentiList, supervisore, tasksList);
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
        team.removeAllDipendenti();

        // Remove all tasks from the team
        team.removeAllTasks();

        // Remove the team from its supervisor
        Supervisore supervisore = team.getSupervisore();
        supervisore.removeTeamSupervisionato(team);

        // Finally, delete the team
        teamRepository.delete(team);
    }
    // for now dont need getIdTeam

    // other methods as needed
    public List<Dipendente> getDipendentiInTeam(Team team){
        if(team == null) {
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        }
        return team.getDipendenti();
    }
    @Transactional
    public void addDipendenteToTeam(Team newTeam, Dipendente dipendente){
        if(newTeam == null || dipendente == null) {
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        }
        Team oldTeam = dipendente.getDipendenteTeam();
        // if the dipendente is already in a team, remove him from the old team
        if(oldTeam != null) {
            oldTeam.removeDipendente(dipendente);
        }
        newTeam.addDipendente(dipendente);
    }
    @Transactional
    public void removeAllDipendentiFromTeam(Team team){
        if(team == null) {
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        }
        team.removeAllDipendenti();
    }
    @Transactional
    public void removeDipendenteFromTeam(Team team, Dipendente dipendente){
        if(team == null || dipendente == null) {
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        } else if(!team.getDipendenti().contains(dipendente)){
            throw new IllegalArgumentException(teamConstants.ENTITY_NOT_FOUND);
        }
        team.removeDipendente(dipendente);
    }

    public Supervisore getTeamSupervisore(Team team){
        if(team == null) {
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        }
        return team.getSupervisore();
    }
    @Transactional
    public void setTeamSupervisore(Team team, Supervisore supervisore){
        if(team == null || supervisore == null) {
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        }
        team.setSupervisore(supervisore);
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
        Team oldTeam = task.getTaskTeam();
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
        return teamRepository.findByIdTeam(id);
    }
    @Transactional
    public void deleteTeamById(Long id) {
        if(getTeamById(id).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.ENTITY_NOT_FOUND);
        }
        Team team = getTeamById(id).get();

        // Remove the association of the team from its employees
        team.removeAllDipendenti();

        // Remove all tasks from the team
        team.removeAllTasks();

        // Remove the team from its supervisor
        Supervisore supervisore = team.getSupervisore();
        supervisore.removeTeamSupervisionato(team);

        teamRepository.deleteById(id);
    }
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }
    public List<Team> getTeamsBySupervisore_Id(Long idSupervisore) {
        if(supervisoreRepository.findById(idSupervisore).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.ENTITY_NOT_FOUND);
        }
        return teamRepository.findBySupervisore_Id(idSupervisore);
    }
    public Team getTeamByDipendente_Id(Long idDipendente) {
        if(dipendenteRepository.findById(idDipendente).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.ENTITY_NOT_FOUND);
        }
        return teamRepository.findByDipendenti_Id(idDipendente);
    }
    public Team getTeamByTask_Id(Long idTask) {
        if(taskRepository.findById(idTask).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.ENTITY_NOT_FOUND);
        }
        return teamRepository.findByTasks_IdTask(idTask);
    }
    public List<Task> getTasksByTeamId(Long idTeam) {
        if(getTeamById(idTeam).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.ENTITY_NOT_FOUND);
        }
        return teamRepository.findTasksByIdTeam(idTeam);
    }
    public Supervisore getSupervisoreByTeamId(Long idTeam) {
        if(getTeamById(idTeam).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.ENTITY_NOT_FOUND);
        }
        return teamRepository.findSupervisoreByIdTeam(idTeam);
    }
    public List<Dipendente> getDipendentiByTeamId(Long idTeam) {
        if(getTeamById(idTeam).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.ENTITY_NOT_FOUND);
        }
        return teamRepository.findDipendentiByIdTeam(idTeam);
    }

    public List<Task> getTasksInTeamIdByTaskState(Long idTeam, TaskState taskState) {
        if(getTeamById(idTeam).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.ENTITY_NOT_FOUND);
        } else if(taskState == null) {
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        }
        return teamRepository.findTasksInTeamIdByTaskState(idTeam, taskState);
    }
    public List<Dipendente> getDipendentiInTeamIdWithSalaryGreaterThan(Long idTeam, Double salary) {
        if(getTeamById(idTeam).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.ENTITY_NOT_FOUND);
        } else if (salary <= 0) {
            throw new IllegalArgumentException(teamConstants.THIS_VALUE_MUST_BE_POSITIVE);
        }
        return teamRepository.findDipendentiInTeamIdWithSalaryGreaterThan(idTeam, salary);
    }
    public List<Dipendente> getDipendentiInTeamIdWithSalaryLessThan(Long idTeam, Double salary) {
        if (getTeamById(idTeam).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.ENTITY_NOT_FOUND);
        } else if (salary <= 0) {
            throw new IllegalArgumentException(teamConstants.THIS_VALUE_MUST_BE_POSITIVE);
        }
        return teamRepository.findDipendentiInTeamIdWithSalaryLessThan(idTeam, salary);
    }
    public List<Dipendente> getDipendentiInTeamIdWithGrado(Long idTeam, EmployeeRole employeeRole) {
        if(getTeamById(idTeam).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.ENTITY_NOT_FOUND);
        } else if(employeeRole == null) {
            throw new IllegalArgumentException(teamConstants.ATTRIBUTE_CANNOT_BE_NULL);
        }
        return teamRepository.findDipendentiInTeamIdWithGrado(idTeam, employeeRole);
    }
}
