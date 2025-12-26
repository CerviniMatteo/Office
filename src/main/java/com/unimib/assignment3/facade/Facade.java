package com.unimib.assignment3.facade;

import com.unimib.assignment3.POJO.Studente;
import com.unimib.assignment3.service.StudenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Facade {

    @Autowired
    StudenteService studenteService;

    public  Studente saveStudente(Studente studente){
        return  studenteService.saveWithUniqueEmail(studente);
    }
}
