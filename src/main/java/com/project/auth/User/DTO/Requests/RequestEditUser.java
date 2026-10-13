package com.project.auth.User.DTO.Requests;

public record RequestEditUser(
        String firstName,
        String lastName,
        String phone
) {
}
