package com.pm.patientservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PatientsRequestDTO {
	@NotBlank(message = "Name is required")
	@Size(max = 100, message = "Name cannot be more than 100 characters")
	private String name;

	@NotBlank(message = "Email is required")
	@Email(message = "Email should be valid")
	private String email;

	@NotBlank(message = "Address is required")
	private String address;

	@NotBlank(message = "Date of Birth is required")
	private String dateOfBirth;

	@NotBlank(message = "Registered Date is rquired")
	private String registeredDate;
}
