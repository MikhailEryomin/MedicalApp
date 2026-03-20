// src/main/java/com/medicalapp/service/MedicineService.java
package com.medicalapp.service;

import com.medicalapp.dto.MedicineResponse;
import com.medicalapp.entity.Medicine;
import com.medicalapp.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicineService {

    private final MedicineRepository medicineRepository;

    @Transactional(readOnly = true)
    public List<MedicineResponse> search(String search) {
        List<Medicine> medicines;
        if (search != null && !search.isEmpty()) {
            medicines = medicineRepository.findTop50ByNameContainingIgnoreCaseOrderByName(search);
        } else {
            medicines = medicineRepository.findTop50ByOrderByName();
        }
        return medicines.stream().map(m -> MedicineResponse.builder()
                .id(m.getId())
                .name(m.getName())
                .form(m.getForm().name())
                .defaultDosage(m.getDefaultDosage())
                .build()
        ).collect(Collectors.toList());
    }
}