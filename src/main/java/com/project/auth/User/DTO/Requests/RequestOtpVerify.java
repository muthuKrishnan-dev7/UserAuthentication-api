package com.project.auth.User.DTO.Requests;

public record RequestOtpVerify(
        String email,
        String OTP
) {
}
