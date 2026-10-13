package com.project.auth.User.DTO.Requests;

public record RequestForgotPass(
        String email,
        String newPassword,
        String confirmPassword
) {
}
