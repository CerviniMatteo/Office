package com.unimib.assignment3.service;

import com.unimib.assignment3.POJO.Studente;
import com.unimib.assignment3.repository.StudenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudenteService {

    @Autowired
    private final StudenteRepository studenteRepository;

    public StudenteService(StudenteRepository studenteRepository) {
        this.studenteRepository = studenteRepository;
    }

    @Transactional
    public Studente saveWithUniqueEmail(Studente studente) {
        String emailPattern = studente.getEmailIstituzionale().substring(0, studente.getEmailIstituzionale().indexOf("@"));

        List<Studente> duplicati = findAll().stream()
                .filter(s -> s.getEmailIstituzionale().substring(0, studente.getEmailIstituzionale().indexOf("@")).equals(emailPattern))
                .toList();

        int numeroDuplicati = duplicati.size();

        if (numeroDuplicati > 0) {
            String baseEmail = studente.getEmailIstituzionale().substring(0,
                    studente.getEmailIstituzionale().indexOf("@"));
            String dominio = studente.getEmailIstituzionale().substring(
                    studente.getEmailIstituzionale().indexOf("@"));
            studente.setEmailIstituzionale(baseEmail + numeroDuplicati + dominio);
        }

        return studenteRepository.saveAndFlush(studente);
    }

    public List<Studente> findAll() {
        return studenteRepository.findAll();
    }
}
