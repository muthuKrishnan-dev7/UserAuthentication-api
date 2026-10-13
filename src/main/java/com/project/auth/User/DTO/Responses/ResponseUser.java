package com.project.auth.User.DTO.Responses;

import java.time.LocalDateTime;

public record ResponseUser(
        String firstname,
        String lastname,
        String email,
        String phone,
        Boolean emailVerified,
        LocalDateTime createdAt
) {}
