package com.pm.patientservice.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.dto.PatientsRequestDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;

@Service
public class PatientService {
	private PatientRepository patientRepository;

	public PatientService(PatientRepository patientRepository) {
		this.patientRepository = patientRepository;
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

}
