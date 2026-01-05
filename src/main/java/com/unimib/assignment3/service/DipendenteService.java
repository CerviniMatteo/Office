package com.unimib.assignment3.service;

import com.unimib.assignment3.POJO.Dipendente;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.Grado;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.repository.DipendenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.unimib.assignment3.constants.DipendenteConstants.notAManager;
import static com.unimib.assignment3.constants.DipendenteConstants.nullManager;

@Service
public class DipendenteService {

    @Autowired
    DipendenteRepository dipendenteRepository;

    public Dipendente saveDipendente(Dipendente dipendente) {
        return dipendenteRepository.save(dipendente);
    }

    public List<Dipendente> saveAllDipendenti(List<Dipendente> dipendenti) {
        return dipendenteRepository.saveAll(dipendenti);
    }

    public Optional<Dipendente> trovaPerId(Long id) {
        return dipendenteRepository.findById(id);
    }

    public Dipendente getReferenceById(Long id) {
        return dipendenteRepository.getReferenceById(id);
    }

    public List<Dipendente> trovaTutti() {
        return dipendenteRepository.findAll();
    }

    public boolean esistePerId(Long id) {
        return dipendenteRepository.existsById(id);
    }

    public long contaTutti() {
        return dipendenteRepository.count();
    }

    public void eliminaPerId(Long id) {
        dipendenteRepository.deleteById(id);
    }

    public void elimina(Dipendente dipendente) {
        dipendenteRepository.delete(dipendente);
    }

    public void eliminaTutti(List<Dipendente> dipendenti) {
        dipendenteRepository.deleteAll(dipendenti);
    }

    public void eliminaTutti() {
        dipendenteRepository.deleteAll();
    }

    public void flush() {
        dipendenteRepository.flush(); // forza il commit delle modifiche
    }


    public long contaSupervisori() {
        return dipendenteRepository.count();
    }

    public List<Dipendente> findDipendenteByStipendio(Dipendente dipendente, Double stipendio) {
        if (dipendente == null) throw new IllegalArgumentException(nullManager);
        if (dipendente.getGrado() != Grado.MANAGER) throw new IllegalArgumentException(notAManager);
        return dipendenteRepository.findDipendenteByStipendio(stipendio);
    }

    public List<Dipendente> findDipendenteByGrado(Grado grado) {
        return dipendenteRepository.findDipendenteByGrado(grado);
    }

    public List<Task> findTasksByDipendenteId(Long dipendenteId) {
        return dipendenteRepository.findTasksById( dipendenteId);
    }

    public List<Task> findTasksByDipendenteAndState(Long dipendenteId, TaskState taskState) {
        return dipendenteRepository.findTasksByDipendenteAndState(dipendenteId, taskState);
    }
}
