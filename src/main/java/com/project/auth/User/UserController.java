package com.project.auth.User;

import com.project.auth.CommonDTOs.ApiResponse;
import com.project.auth.CommonDTOs.ApiResponse2;
import com.project.auth.Security.JwtUtil;
import com.project.auth.User.DTO.Requests.*;
import com.project.auth.User.DTO.Responses.ResponseRegister;
import com.project.auth.User.DTO.Responses.ResponseRegister2;
import com.project.auth.User.DTO.Responses.ResponseUser;
import com.project.auth.User.DTO.Responses.ResponseUserNameCheck;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

	private final UserService serv;
	private final JwtUtil jwtUtil;

	public UserController(UserService serv, JwtUtil jwtUtil) {
		this.serv = serv;
        this.jwtUtil = jwtUtil;
    }

	@PostMapping("/register")
	public ResponseEntity<ResponseRegister> registerUser(@RequestBody RequestRegister request){
		ResponseRegister response = serv.registerUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/otpVerify")
	public ResponseEntity<ResponseRegister2> emailVerify(@RequestBody RequestOtpVerify request){
		ResponseRegister2 response = serv.emailVerify(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse2> loginUser(@RequestBody @Valid RequestLogin request){
		ApiResponse2 response = serv.loginUser(request);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			jwtUtil.invalidateToken(token);
		}
		return ResponseEntity.ok("Logout successful");
	}

	@PostMapping("/email-check")
	public ResponseEntity<ResponseUserNameCheck> userCheck(@RequestBody UserNameCheck request){
		ResponseUserNameCheck response = serv.userCheck(request);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<ApiResponse> forgotPassword(@RequestBody RequestForgotPass request){
		ApiResponse response = serv.forgotPassword(request);
		return ResponseEntity.ok().body(response);
	}

	@GetMapping("/getUser")
	public ResponseEntity<ResponseUser> getUser(@RequestHeader("Authorization") String authHeader) {
		ResponseUser response = serv.getUser(authHeader);
		return ResponseEntity.ok().body(response);
	}

	@PatchMapping("/editUser")
	public ResponseEntity<ApiResponse> editUser(@RequestBody RequestEditUser request, @RequestHeader("Authorization") String authHeader){
		ApiResponse response = serv.editUser(request, authHeader);
		return ResponseEntity.ok().body(response);
	}

//	@PatchMapping("/edit-details")
//	public ResponseEntity<> editUser(){
//	}


}
