package com.medicalapp.entity;

import jakarta.persistence.*;
import lombok.*;
import com.medicalapp.crypto.EncryptedStringConverter;
import jakarta.persistence.Convert;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(length = 200)
    private String specialization;

//    @Column(name = "licence_number", length = 100)
//    private String licenceNumber;
    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "licence_number", columnDefinition = "TEXT")
    private String licenceNumber;

    @ManyToMany
    @JoinTable(
            name = "doctor_patient",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "patient_id")
    )
    @Builder.Default
    private List<Patient> patients = new ArrayList<>();

    @OneToMany(mappedBy = "doctor")
    @Builder.Default
    private List<Prescription> prescriptions = new ArrayList<>();
}