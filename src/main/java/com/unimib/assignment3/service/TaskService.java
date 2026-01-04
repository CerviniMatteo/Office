package com.unimib.assignment3.service;

import com.unimib.assignment3.POJO.Dipendente;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.repository.DipendenteRepository;
import com.unimib.assignment3.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Task task = new Task(initialState != null ? initialState : TaskState.DAINIZIARE);
        return taskRepository.saveAndFlush(task);
    }

    // Assegnazione dipendente a task con validazione
    @Transactional
    public Task assegnaDipendenteATask(Long taskId, Long dipendenteId) {
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

        // Validazione: non assegnare a task già finiti
        if (task.getTaskState() == TaskState.FINITO) {
            throw new IllegalStateException("Impossibile assegnare dipendenti a task già completati");
        }

        // Validazione: dipendente già assegnato
        if (task.hasDipendente(dipendente)) {
            throw new IllegalStateException("Dipendente già assegnato a questo task");
        }

        task.assegnaDipendente(dipendente);

        dipendenteRepository.saveAndFlush(dipendente);
        return taskRepository.saveAndFlush(task);
    }

    // Rimozione dipendente da task
    @Transactional
    public Task rimuoviDipendenteDaTask(Long taskId, Long dipendenteId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        Optional<Dipendente> dipOpt = dipendenteRepository.findById(dipendenteId);

        if (taskOpt.isEmpty() || dipOpt.isEmpty()) {
            throw new IllegalArgumentException("Task o Dipendente non trovato");
        }

        Task task = taskOpt.get();
        Dipendente dipendente = dipOpt.get();

        task.rimuoviDipendente(dipendente);
        return taskRepository.saveAndFlush(task);
    }

    // Cambio stato task con validazione
    @Transactional
    public Task cambiaStatoTask(Long taskId, TaskState nuovoStato) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);

        if (taskOpt.isEmpty()) {
            throw new IllegalArgumentException("Task non trovato con id: " + taskId);
        }

        Task task = taskOpt.get();
        TaskState statoCorrente = task.getTaskState();

        // Validazione transizioni di stato
        if (statoCorrente == TaskState.FINITO && nuovoStato != TaskState.FINITO) {
            throw new IllegalStateException("Impossibile cambiare stato di un task già completato");
        }

        if (statoCorrente == TaskState.DAINIZIARE && nuovoStato == TaskState.FINITO) {
            throw new IllegalStateException("Un task non può passare direttamente da DAINIZIARE a FINITO");
        }

        task.setTaskState(nuovoStato);
        return taskRepository.saveAndFlush(task);
    }

    // Metodi di ricerca
    public List<Task> getTasksByStato(TaskState stato) {
        return taskRepository.findByTaskState(stato);
    }

    public List<Task> getTasksByDipendente(Dipendente dipendente) {
        return taskRepository.findTasksByDipendente(dipendente);
    }

    public List<Task> getTasksNonAssegnati() {
        return taskRepository.findTasksWithoutDipendenti();
    }

    public long countTasksByStato(TaskState stato) {
        return taskRepository.countByTaskState(stato);
    }

    public List<Task> getTasksComplessi(int sogliaDipendenti) {
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

    // Metodo per verificare se un dipendente è assegnato a un task
    public boolean isDipendenteAssegnato(Long taskId, Long dipendenteId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        Optional<Dipendente> dipOpt = dipendenteRepository.findById(dipendenteId);

        if (taskOpt.isEmpty() || dipOpt.isEmpty()) {
            return false;
        }

        return taskOpt.get().hasDipendente(dipOpt.get());
    }
}