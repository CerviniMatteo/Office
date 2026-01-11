package com.unimib.assignment3.service;

import com.unimib.assignment3.POJO.Dipendente;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.repository.DipendenteRepository;
import com.unimib.assignment3.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private DipendenteRepository dipendenteRepository;

    // Creazione e salvataggio task tramite Repository
    @Transactional
    public Task createTask(TaskState initialState) {
        TaskState state = initialState != null ? initialState : TaskState.DAINIZIARE;
        Task task = new Task(state);


        if (state == TaskState.INIZIATO) {
            task.setStartDate(LocalDate.now());
        } else if (state == TaskState.FINITO) {
            task.setStartDate(LocalDate.now());
            task.setEndDate(LocalDate.now());
        }

        return taskRepository.saveAndFlush(task);
    }

    // Assegnazione dipendente a task con validazione
    @Transactional
    public Task assignEmployeeToTask(Long taskId, Long dipendenteId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        Optional<Dipendente> dipOpt = dipendenteRepository.findById(dipendenteId);

        if (taskOpt.isEmpty()) {
            throw new IllegalArgumentException("Task non trovato con id: " + taskId);
        }
        if (dipOpt.isEmpty()) {
            throw new IllegalArgumentException("Dipendente non trovato con id: " + dipendenteId);
        }

        Task task = taskOpt.get();
        Dipendente dipendente = dipOpt.get();

        if (task.getTaskState() == TaskState.FINITO) {
            throw new IllegalStateException("Impossibile assegnare dipendenti a task già completati");
        }

        if (task.hasEmployee(dipendente)) {
            throw new IllegalStateException("Dipendente già assegnato a questo task");
        }

        task.assignEmployee(dipendente);

        dipendenteRepository.saveAndFlush(dipendente);
        return taskRepository.saveAndFlush(task);
    }



    // Rimozione dipendente da task
    @Transactional
    public Task removeEmployeeFromTask(Long taskId, Long dipendenteId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        Optional<Dipendente> dipOpt = dipendenteRepository.findById(dipendenteId);

        if (taskOpt.isEmpty() || dipOpt.isEmpty()) {
            throw new IllegalArgumentException("Task o Dipendente non trovato");
        }

        Task task = taskOpt.get();
        Dipendente dipendente = dipOpt.get();

        task.removeEmployee(dipendente);
        return taskRepository.saveAndFlush(task);
    }

    @Transactional
    public Task changeTaskState(Long taskId, TaskState nuovoStato) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task non trovato con id: " + taskId));

        TaskState statoCorrente = task.getTaskState();

        if (statoCorrente == nuovoStato) return task;

        switch (statoCorrente) {
            case DAINIZIARE:
                if (nuovoStato != TaskState.INIZIATO) {
                    throw new IllegalStateException("Da DAINIZIARE si può passare solo a INIZIATO.");
                }
                task.setStartDate(LocalDate.now());
                break;

            case INIZIATO:
                if (nuovoStato != TaskState.FINITO) {
                    throw new IllegalStateException("Da INIZIATO si può passare solo a FINITO. Usa reset() per ricominciare.");
                }
                task.setEndDate(LocalDate.now());
                break;

            case FINITO:
                throw new IllegalStateException("Il task è FINITO e non può più cambiare stato. Usa reset() per ricominciare.");
        }

        task.setTaskState(nuovoStato);
        return taskRepository.saveAndFlush(task);
    }

    @Transactional
    public Task resetTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task non trovato con id: " + taskId));

        task.setTaskState(TaskState.DAINIZIARE);

        task.setEndDate(null);
        task.setStartDate(null);

        return taskRepository.saveAndFlush(task);
    }

    // Metodi di ricerca
    public List<Task> getTasksByState(TaskState stato) {
        return taskRepository.findByTaskState(stato);
    }

    public List<Task> getTasksByEmployee(Dipendente dipendente) {
        return taskRepository.findTasksByDipendente(dipendente);
    }

    public List<Task> getUnassignedTasks() {
        return taskRepository.findTasksWithoutDipendenti();
    }

    public long countTasksByState(TaskState stato) {
        return taskRepository.countByTaskState(stato);
    }

    public List<Task> getComplexTasks(int sogliaDipendenti) {
        return taskRepository.findTasksWithMoreThanNDipendenti(sogliaDipendenti);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Transactional
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public boolean isEmployeeAssigned(Long taskId, Long dipendenteId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        Optional<Dipendente> dipOpt = dipendenteRepository.findById(dipendenteId);

        if (taskOpt.isEmpty() || dipOpt.isEmpty()) {
            return false;
        }

        return taskOpt.get().hasEmployee(dipOpt.get());
    }

    public List<Task> getTasksByStateWithEmployees(TaskState stato) {
        return taskRepository.findTasksByStateWithDipendenti(stato);
    }

    public Integer getEmployeeCountPerTask(Long taskId) {
        return taskRepository.countDipendentiByTaskId(taskId);
    }

    public List<Task> getTasksByStateAndEmployeeCount(TaskState stato, int numDipendenti) {
        return taskRepository.findTasksByStateAndDipendentiCount(stato, numDipendenti);
    }

    public List<Task> getTasksByTeam(Long idTeam) {
        return taskRepository.findTasksByTeamId(idTeam);
    }

}