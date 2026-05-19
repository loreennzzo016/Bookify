package com.example.onlinelibrary.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private Integer year;
    private String thumbnail;
    private UserResponse assignedTo;
}
