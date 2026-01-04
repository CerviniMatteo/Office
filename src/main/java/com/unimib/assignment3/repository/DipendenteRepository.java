package com.unimib.assignment3.repository;

import com.unimib.assignment3.POJO.Dipendente;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.Grado;
import com.unimib.assignment3.enums.TaskState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DipendenteRepository extends JpaRepository<Dipendente, Long> {
    // Query per trovare dipendenti con quello stipendio
    List<Dipendente> findDipendenteByStipendio(Double stipendio);

    // Query per trovare dipendenti con quel grado
    List<Dipendente> findDipendenteByGrado(Grado grado);

    // Query per trovare tutte le task in quello stato per quel dipdendente
    @Query("SELECT t FROM dipendente d JOIN d.tasks t WHERE d.id = :dipendenteId AND t.taskState = :taskState")
    List<Task> findTasksByDipendenteAndState(@Param("dipendenteId") Long dipendenteId, @Param("taskState") TaskState taskState);
}
