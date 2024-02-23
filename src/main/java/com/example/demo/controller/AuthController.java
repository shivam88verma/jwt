package com.example.demo.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.JwtTokenUtil;
import com.example.demo.entity.LoginRequest;
import com.example.demo.entity.User;
import com.example.demo.repo.UsersRepo;
import com.example.demo.service.EmailService;

@RestController
class AuthController {

	// Mock user repository (You should replace this with your actual user
	// repository)
	private UsersRepo userRepository;
	

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	public AuthController(UsersRepo userRepository) {
		this.userRepository = userRepository;
	}

	@PostMapping("/signup")
	public String signUp(@RequestBody User user) {
		// Your sign up logic here (e.g., save user to database)
		userRepository.save(user);
		return "User signed up successfully!";
	}

	@GetMapping("/getuserdetails")
	public ResponseEntity<?> getuserdetails(@RequestParam String username) {
		User user = userRepository.findByUsername(username);
		if (user != null) {
			return new ResponseEntity<>(user, HttpStatus.OK);
		}
		return new ResponseEntity<>("Invalid User", HttpStatus.BAD_REQUEST);
	}

	@PostMapping("/login")
	public String login(@RequestBody LoginRequest loginRequest) {
		// Your login logic here (e.g., validate credentials)
		User user = userRepository.findByUsername(loginRequest.getUsername());
		if (user != null && user.getPassword().equals(loginRequest.getPassword())) {

			String otp = generateOTP(6);

			boolean flag = emailService.sendEmail(user.getEmail(), "OTP Validation", "Your OTP Is :- " + otp);
			if (flag) {
				user.setLoginotp(otp);
				userRepository.save(user);
				return "Otp Sent Successfully On " + user.getEmail();
			}

			return "Internal Server Error";
		} else {
			return "Invalid username or password";
		}
	}

	public static String generateOTP(int length) {
		// You can customize the characters used in the OTP generation if needed
		String numbers = "0123456789";
		StringBuilder otp = new StringBuilder();
		Random random = new Random();

		// Generate OTP of given length
		for (int i = 0; i < length; i++) {
			otp.append(numbers.charAt(random.nextInt(numbers.length())));
		}

		return otp.toString();
	}

	@PostMapping("/validateotp")
	public String validateotp(@RequestBody LoginRequest loginRequest) {
		// Your login logic here (e.g., validate credentials)
		User user = userRepository.findByUsername(loginRequest.getUsername());
		if (user != null && user.getLoginotp().equals(loginRequest.getOtp())) {
			// Generate JWT token
			String token = jwtTokenUtil.generateToken(user);

			return token;
		} else {
			return "Invalid OTP Please Try Again.";
		}
	}
	
}