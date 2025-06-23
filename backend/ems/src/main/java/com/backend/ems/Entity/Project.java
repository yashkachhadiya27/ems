package com.backend.ems.Entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

@Entity
@Data
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String projectName;
    private LocalDate projectJoinDate;
    private LocalDate projectEndDate;
    private int reportingToId;
    @Column(columnDefinition = "TEXT[]")
    private String[] technologies;
    @ManyToMany(mappedBy = "projects", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Register> users;

}
