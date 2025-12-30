package com.unimib.assignment3.POJO;

import jakarta.persistence.*;

@Entity(name="team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idTeam;

    public Long getIdTeam() {
        return idTeam;
    }

    public void setIdTeam(Long idTeam) {
        this.idTeam = idTeam;
    }
}
