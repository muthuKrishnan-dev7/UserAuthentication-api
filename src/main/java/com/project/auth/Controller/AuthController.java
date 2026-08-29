package com.project.auth.Controller;

import com.project.auth.DTO.*;
import com.project.auth.Service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService serv;

	public AuthController(AuthService serv) {
		this.serv = serv;
	}

	@PostMapping("/register")
	public ResponseEntity<ResponseDTO2> registeUser(@RequestBody RegisterDTO request){
		ResponseDTO2 response = serv.registerUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/otpVerify")
	public ResponseEntity<ResponseDTO1> emailVerify(@RequestBody OtpRequestDTO request){
		ResponseDTO1 response = serv.emailVerify(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);

	}

	@PostMapping("/login")
	public ResponseEntity<ResponseDTO1> loginUser(@RequestBody )
	

}
