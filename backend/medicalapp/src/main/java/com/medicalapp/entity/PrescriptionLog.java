package com.medicalapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "prescription_logs")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PrescriptionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @Column(name = "taken_at", nullable = false)
    private LocalDateTime takenAt;
}