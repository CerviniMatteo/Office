package com.unimib.assignment3.facade;

import com.unimib.assignment3.POJO.Dipendente;
import com.unimib.assignment3.POJO.Supervisore;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.POJO.Team;
import com.unimib.assignment3.repository.DipendenteRepository;
import com.unimib.assignment3.repository.SupervisoreRepository;
import com.unimib.assignment3.repository.TaskRepository;
import com.unimib.assignment3.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Facade {

    @Autowired
    DipendenteRepository dipendenteRepository;
    @Autowired
    SupervisoreRepository supervisoreRepository;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    TeamRepository teamRepository;

    public  Dipendente saveDipendente(Dipendente dipendente){
        return  dipendenteRepository.saveAndFlush(dipendente);
    }

    public Supervisore saveSupervisore(Supervisore supervisore){
        return  supervisoreRepository.saveAndFlush(supervisore);
    }

    public Task saveTask(Task task){
        return taskRepository.saveAndFlush(task);
    }

    public Team saveTeam(Team team){
        return teamRepository.saveAndFlush(team);
    }
}
