package com.medicalapp.entity;
import com.medicalapp.crypto.EncryptedStringConverter;
import com.medicalapp.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

//    @Column(nullable = false, unique = true, length = 255)
//    private String email;
    @Convert(converter = EncryptedStringConverter.class)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String email;

    @Column(name = "email_lookup", length = 64, nullable = true)
    private String emailLookup;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "user_role")
    private UserRole role;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Doctor doctor;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Patient patient;
}