package com.example.onlinelibrary.dto;

import com.example.onlinelibrary.model.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private Role role;
}
