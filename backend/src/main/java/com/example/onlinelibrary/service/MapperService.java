package com.example.onlinelibrary.service;

import com.example.onlinelibrary.dto.BookResponse;
import com.example.onlinelibrary.dto.UserResponse;
import com.example.onlinelibrary.model.Book;
import com.example.onlinelibrary.model.User;
import org.springframework.stereotype.Service;

@Service
public class MapperService {

    public UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    public BookResponse toBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .year(book.getYear())
                .thumbnail(book.getThumbnail())
                .assignedTo(toUserResponse(book.getAssignedTo()))
                .build();
    }
}
