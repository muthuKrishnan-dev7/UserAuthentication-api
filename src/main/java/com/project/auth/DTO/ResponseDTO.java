package com.project.auth.DTO;

public record ResponseDTO(
        String firstname,
        String lastname,
        String email,
        String phone,
        String emailVerified,
        String createdAt
) {}
