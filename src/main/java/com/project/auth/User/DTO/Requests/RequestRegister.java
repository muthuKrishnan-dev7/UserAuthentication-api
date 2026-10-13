package com.project.auth.User.DTO.Requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestRegister {

	 @NotBlank(message = "First name is required")
	    @Size(min = 2, max = 30, message = "First name must be 2-30 characters")
	    @Pattern(
	        regexp = "^[A-Za-z]+$",
	        message = "First name should contain only letters"
	    )
	    private String firstname;

	    @NotBlank(message = "Last name is required")
	    @Size(min = 1, max = 30, message = "Last name must be 1-30 characters")
	    @Pattern(
	        regexp = "^[A-Za-z]+$",
	        message = "Last name should contain only letters"
	    )
	    private String lastname;

	    @NotBlank(message = "Email is required")
	    @Email(message = "Enter a valid email")
	    private String email;

	    @NotBlank(message = "Phone number is required")
	    @Pattern(
	        regexp = "^[6-9][0-9]{9}$",
	        message = "Enter a valid 10-digit phone number"
	    )
	    private String phone;

	    @NotBlank(message = "Password is required")
	    @Size(min = 8, max = 20, message = "Password must be 8-20 characters")
	    @Pattern(
	        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$",
	        message = "Password must contain uppercase, lowercase, number, and special character"
	    )
	    private String password;
	
}
