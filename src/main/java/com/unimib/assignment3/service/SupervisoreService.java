package com.unimib.assignment3.service;

import com.unimib.assignment3.POJO.Supervisore;
import com.unimib.assignment3.repository.SupervisoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SupervisoreService {

    @Autowired SupervisoreRepository supervisoreRepository;

    public Supervisore saveSupervisore(Supervisore supervisore) {
        return supervisoreRepository.save(supervisore);
    }

    public Optional<Supervisore> trovaPerId(Long id) {
        return supervisoreRepository.findById(id);
    }

    public List<Supervisore> trovaTutti() {
        return supervisoreRepository.findAll();
    }

    public boolean esistePerId(Long id) {
        return supervisoreRepository.existsById(id);
    }

    public void eliminaPerId(Long id) {
        supervisoreRepository.deleteById(id);
    }

    public void elimina(Supervisore supervisore) {
        supervisoreRepository.delete(supervisore);
    }

    public long contaSupervisori() {
        return supervisoreRepository.count();
    }

    public List<Supervisore> trovaSupervisoriSupervisionatiPerId(Long supervisoreId) {
        return supervisoreRepository.findBySupervisore_Id(supervisoreId);
    }

    public List<Supervisore> trovaSupervisoriSenzaSupervisore() {
        return supervisoreRepository.findBySupervisoreIsNull();
    }

    public long contaSupervisoriSupervisionati(Long supervisoreId) {
        return supervisoreRepository.countBySupervisore_Id(supervisoreId);
    }

    public boolean esisteSupervisoreConSubordinati(Long supervisoreId) {
        return contaSupervisoriSupervisionati(supervisoreId) > 0;
    }


    public List<Supervisore> trovaSupervisoriConTeam() {
        return supervisoreRepository.findSupervisoriConTeam();
    }

    public List<Supervisore> trovaSupervisoriConSubordinati() {
        return supervisoreRepository.findSupervisoriConSubordinati();
    }

    public Supervisore trovaSupervisorePerTeam(Long teamId) {
        return supervisoreRepository.findSupervisoreByTeamId(teamId);
    }


    public void assegnaSubordinato(Long capoId, Long subordinatoId) {
        Supervisore capo = supervisoreRepository.findById(capoId)
                .orElseThrow(() -> new IllegalArgumentException("Capo non trovato"));
        Supervisore sub = supervisoreRepository.findById(subordinatoId)
                .orElseThrow(() -> new IllegalArgumentException("Subordinato non trovato"));

        capo.getSupervisoriSupervisionati().add(sub);
        sub.setSupervisore(capo);

        supervisoreRepository.saveAndFlush(capo);
        supervisoreRepository.saveAndFlush(sub);
    }

    public void rimuoviSubordinato(Long capoId, Long subordinatoId) {
        Supervisore capo = supervisoreRepository.findById(capoId)
                .orElseThrow(() -> new IllegalArgumentException("Capo non trovato"));
        Supervisore sub = supervisoreRepository.findById(subordinatoId)
                .orElseThrow(() -> new IllegalArgumentException("Subordinato non trovato"));

        capo.getSupervisoriSupervisionati().remove(sub);
        sub.setSupervisore(null);

        supervisoreRepository.saveAndFlush(capo);
        supervisoreRepository.saveAndFlush(sub);
    }
}
