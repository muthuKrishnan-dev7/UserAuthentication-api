package com.project.auth.User.DTO.Requests;

public record RequestLogin(
        String userName,
        String password
) {
}
