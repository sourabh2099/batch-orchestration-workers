package com.example.demo.orchestrator.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class AdverseEvent {

    @Id
    private Long id;
    @Column(name = "reported_event_term")
    private String reporterEventTerm;
    @Column(name = "subject_id")
    private String subjectId;
    @Column(name = "ae_onset_date")
    private String aeOnsetDate;
}
