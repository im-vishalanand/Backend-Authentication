package com.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.UserDTO;
import com.backend.exception.AdminException;
import com.backend.exception.UserException;
import com.backend.model.User;
import com.backend.service.JWTService;
import com.backend.service.UserService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
public class UserController {
	@Autowired
	private JWTService jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value("${admin.secretKey}")
	private String secretKey;

	/**
	 * method to login user
	 * 
	 * @param user
	 * @return user
	 * @throws UserException
	 */
	// http://localhost:8082/user
	@GetMapping("/user")
	public ResponseEntity<String> welcomePage() {
		return new ResponseEntity<>("Welcome to the website !", HttpStatus.OK);
	}

	/**
	 * method to sign in user
	 * 
	 * @param user
	 * @return user
	 * @throws UserException
	 */
	// http://localhost:8082/user/signup
	@PostMapping("/user/signup")
	public ResponseEntity<User> userSignUp(@Valid @RequestBody User user) throws UserException {
		
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		return new ResponseEntity<>(userService.addUser(user, false), HttpStatus.ACCEPTED);
	}

	/**
	 * method to sign in admin
	 * 
	 * @param user
	 * @param secretKey
	 * @return user
	 * @throws UserException
	 * @throws AdminException
	 */
	// http://localhost:8082/user/signup/{secretKey}
	@PostMapping("/user/signup")
	public ResponseEntity<User> adminSignUp(@Valid @RequestBody User user, @RequestParam String secretKey)
			throws UserException, AdminException {
		if (!secretKey.equals(this.secretKey))
			throw new AdminException("Wrong Passcode.");

		return new ResponseEntity<>(userService.addUser(user, true), HttpStatus.ACCEPTED);
	}

	/**
	 * method to login user
	 * 
	 * @return string message
	 */
	// http://localhost:8082/user/userLogin
	@GetMapping("/user/userLogin")
	public ResponseEntity<String> userLogin() {
		return new ResponseEntity<>("Welcome User!", HttpStatus.OK);
	}

	/**
	 * method to login admin
	 * 
	 * @return string message
	 */
	// http://localhost:8082/user/adminLogin
	@GetMapping("/user/adminLogin")
	public ResponseEntity<String> adminLogin() {
		return new ResponseEntity<>("Welcome Admin !", HttpStatus.OK);
	}

	/**
	 * method to authenticate user
	 * 
	 * @param authRequest
	 * @return string token
	 */
	// http://localhost:8082/authenticate
	@PostMapping("/authenticate")
	public String authenticateAndGetToken(@Valid @RequestBody UserDTO authRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
		if (authentication.isAuthenticated()) {
			return jwtService.generateToken(authRequest.getEmail());
		} else {
			throw new UsernameNotFoundException("invalid user request !");
		}

	}

}
