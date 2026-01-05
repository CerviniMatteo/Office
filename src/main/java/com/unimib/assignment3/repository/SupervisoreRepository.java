package com.unimib.assignment3.repository;

import com.unimib.assignment3.POJO.Supervisore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SupervisoreRepository extends JpaRepository<Supervisore, Long> {
    //Cerco i supervisori supervisionati da un supervisore specifico.
    List<Supervisore> findBySupervisore_Id(Long idSupervisore);

    //Cerco il supervisore senza supervisori
    List<Supervisore> findBySupervisoreIsNull();

    //Cerco supervisori che supervisionano almeno un team
    @Query("SELECT DISTINCT s FROM supervisore s JOIN s.teamSupervisionato t")
    List<Supervisore> findSupervisoriConTeam();

    //Cerco supervisore di un team specifico
    @Query("SELECT t.supervisore FROM team t WHERE t.idTeam = :teamId")
    Supervisore findSupervisoreByTeamId(@Param("teamId") Long teamId);
    //Cerco tutti i supervisori che hanno almeno un supervisore supervisionato
    @Query("SELECT DISTINCT s FROM supervisore s WHERE SIZE(s.supervisoriSupervisionati) > 0")
    List<Supervisore> findSupervisoriConSubordinati();


    //Conto quanti supervisori supervisiona un supervisore
    @Query("SELECT COUNT(s) FROM supervisore s WHERE s.supervisore.id = :id")
    long countBySupervisore_Id(@Param("id") Long supervisoreId);}
