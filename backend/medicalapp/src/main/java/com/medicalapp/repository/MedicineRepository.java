package com.medicalapp.repository;

import com.medicalapp.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, Integer> {
    List<Medicine> findTop50ByOrderByName();
    List<Medicine> findTop50ByNameContainingIgnoreCaseOrderByName(String name);
}