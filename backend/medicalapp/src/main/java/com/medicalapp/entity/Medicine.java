package com.medicalapp.entity;

import com.medicalapp.entity.enums.MedicineForm;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medicines")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "medicine_form")
    private MedicineForm form;

    @Column(name = "default_dosage", length = 100)
    private String defaultDosage;
}