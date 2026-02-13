package com.pm.patientservice.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.dto.PatientsRequestDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;

@Service
public class PatientService {
	private final PatientRepository patientRepository;
	private final BillingServiceGrpcClient billingServiceGrpcClient;

	public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient) {
		this.patientRepository = patientRepository;
		this.billingServiceGrpcClient = billingServiceGrpcClient;
	}

	public List<PatientResponseDTO> getPatients() {
		List<Patient> patients = patientRepository.findAll();

		return patients.stream().map(patient -> PatientMapper.toDTO(patient)).toList();
	}

	public PatientResponseDTO createPatient(PatientsRequestDTO patientsRequestDTO) {
		if (patientRepository.existsByEmail(patientsRequestDTO.getEmail()))
			throw new EmailAlreadyExistsException(
					"A Patient With the Same name already exists " + patientsRequestDTO.getEmail());

		Patient newPatient = patientRepository.save(PatientMapper.toModel(patientsRequestDTO));

		billingServiceGrpcClient.createBillingAccount(newPatient.getId().toString(), newPatient.getName(),
				newPatient.getEmail());

		return PatientMapper.toDTO(newPatient);
	}

	public PatientResponseDTO updatePatient(UUID id, PatientsRequestDTO patientsRequestDTO) {
		Patient patient = patientRepository.findById(id)
				.orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + id));

		if (patientRepository.existsByEmailAndIdNot(patientsRequestDTO.getEmail(), id))
			throw new EmailAlreadyExistsException(
					"A Patient With the Same name already exists " + patientsRequestDTO.getEmail());

		patient.setName(patientsRequestDTO.getName());
		patient.setAddress(patientsRequestDTO.getAddress());
		patient.setEmail(patientsRequestDTO.getEmail());
		patient.setDateOfBirth(LocalDate.parse(patientsRequestDTO.getDateOfBirth()));

		Patient updatedPatient = patientRepository.save(patient);
		return PatientMapper.toDTO(updatedPatient);
	}

	public void deletePatient(UUID id) {
		patientRepository.deleteById(id);
	}

}
