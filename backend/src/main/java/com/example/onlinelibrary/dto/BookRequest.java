package com.example.onlinelibrary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String author;

    @NotNull
    private Integer year;
}
