package com.hospital.patient.repository;

import com.hospital.patient.model.Patient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    Boolean existsByEmail(String email);

    Boolean existsByEmailAndIdNot(String email, UUID id);

}
