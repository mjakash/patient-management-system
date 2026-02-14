package com.pm.authservice.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pm.authservice.dto.LoginRequestDTO;
import com.pm.authservice.util.JwtUtil;

@Service
public class AuthService {

	private final PasswordEncoder passwordEncoder;
	private final UserService userService;
	private final JwtUtil jwtUtil;

	public AuthService(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
	}

	/*
	 * password request -> password -> encoded -> $fsdjfldsfl$^%&*
	 */
	public Optional<String> authenticate(LoginRequestDTO loginRequestDTO) {
		Optional<String> token = userService.findByEmail(loginRequestDTO.getEmail())
				.filter(u -> passwordEncoder.matches(loginRequestDTO.getPassword(), u.getPassword()))
				.map(u -> jwtUtil.generateToken(u.getEmail(), u.getRole()));

		return token;
	}
}
