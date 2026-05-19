package com.example.onlinelibrary.service;

import com.example.onlinelibrary.dto.UserRequest;
import com.example.onlinelibrary.dto.UserResponse;
import com.example.onlinelibrary.model.Book;
import com.example.onlinelibrary.model.User;
import com.example.onlinelibrary.repository.BookRepository;
import com.example.onlinelibrary.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;
    private final MapperService mapperService;

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(mapperService::toUserResponse)
                .toList();
    }

    public UserResponse findById(Long id) {
        return mapperService.toUserResponse(getUser(id));
    }

    public UserResponse create(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        return mapperService.toUserResponse(userRepository.save(user));
    }

    public UserResponse update(Long id, UserRequest request) {
        User user = getUser(id);
        userRepository.findByUsername(request.getUsername())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Username already exists");
                });
        user.setUsername(request.getUsername());
        user.setRole(request.getRole());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return mapperService.toUserResponse(userRepository.save(user));
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found");
        }
        List<Book> assignedBooks = bookRepository.findByAssignedToId(id);
        assignedBooks.forEach(book -> book.setAssignedTo(null));
        bookRepository.saveAll(assignedBooks);
        userRepository.deleteById(id);
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
