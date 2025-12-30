package com.unimib.assignment3.facade;

import com.unimib.assignment3.POJO.Dipendente;
import com.unimib.assignment3.POJO.Supervisore;
import com.unimib.assignment3.repository.DipendenteRepository;
import com.unimib.assignment3.repository.SupervisoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Facade {

    @Autowired
    DipendenteRepository dipendenteRepository;
    @Autowired
    SupervisoreRepository supervisoreRepository;

    public  Dipendente saveDipendente(Dipendente dipendente){
        return  dipendenteRepository.saveAndFlush(dipendente);
    }

    public Supervisore saveSupervisore(Supervisore supervisore){
        return  supervisoreRepository.saveAndFlush(supervisore);
    }
}
