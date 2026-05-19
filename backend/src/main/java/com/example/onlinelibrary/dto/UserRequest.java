package com.example.onlinelibrary.dto;

import com.example.onlinelibrary.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank
    private String username;

    private String password;

    @NotNull
    private Role role;
}
