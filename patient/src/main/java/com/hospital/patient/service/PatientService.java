package com.hospital.patient.service;

import com.hospital.patient.dto.PatientRequestDTO;
import com.hospital.patient.dto.PatientResponseDTO;
import com.hospital.patient.exception.EmailExistsException;
import com.hospital.patient.exception.PatientNotFoundException;
import com.hospital.patient.grpc.BillingServiceGrpcClient;
import com.hospital.patient.kafka.KafkaProducer;
import com.hospital.patient.model.Patient;
import com.hospital.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    public List<PatientResponseDTO> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        return patients.stream()
                .map(patient -> modelMapper.map(patient, PatientResponseDTO.class))
                .collect(Collectors.toList());
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailExistsException("A patient with this email address already exists in the database: " + patientRequestDTO.getEmail());
        }
        Patient patient = modelMapper.map(patientRequestDTO, Patient.class);
        Patient saved = patientRepository.save(patient);

        billingServiceGrpcClient.createBillingAccount(saved.getId().toString(), saved.getName(), saved.getEmail());
        kafkaProducer.sendEvent(saved);

        return modelMapper.map(saved, PatientResponseDTO.class);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException("Patient with id " + id + " not found"));
        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id )) {
            throw new EmailExistsException("A patient with this email address already exists in the database: " + patientRequestDTO.getEmail());
        }
        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient saved = patientRepository.save(patient);
        return modelMapper.map(saved, PatientResponseDTO.class);
    }

    public void deletePatient(UUID id) {
        patientRepository.deleteById(id);
    }
}
