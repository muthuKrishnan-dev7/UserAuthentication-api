package com.project.auth.DTO;

public record OtpRequestDTO(
        String email,
        String OTP
) {
}
