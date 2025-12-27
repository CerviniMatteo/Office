package com.unimib.assignment3.facade;

import com.unimib.assignment3.POJO.Professore;
import com.unimib.assignment3.POJO.Studente;
import com.unimib.assignment3.repository.ProfessoreRepository;
import com.unimib.assignment3.service.StudenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Facade {

    @Autowired
    StudenteService studenteService;
    @Autowired
    ProfessoreRepository professoreRepository;

    public  Studente saveStudente(Studente studente){
        return  studenteService.saveWithUniqueEmail(studente);
    }

    public Professore saveProfessore(Professore professore){
        return professoreRepository.saveAndFlush(professore);
    }

    public Professore findByIdProfessore(Long idProfessore){
        return professoreRepository.findById(idProfessore).orElseThrow();
    }

    public Professore aggiungiAllievo(Professore mentore, Professore allieve){
        mentore.aggiungiAllievo(allieve);
        return professoreRepository.saveAndFlush(mentore);
    }
}
