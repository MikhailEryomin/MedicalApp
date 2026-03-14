package com.medicalapp.controller;

import com.medicalapp.dto.MedicineResponse;
import com.medicalapp.service.MedicineService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
@Tag(name = "Medicines")
public class MedicineController {

    private final MedicineService medicineService;

    @GetMapping
    public ResponseEntity<List<MedicineResponse>> searchMedicines(
            @RequestParam(defaultValue = "") String search) {
        return ResponseEntity.ok(medicineService.search(search));
    }
}