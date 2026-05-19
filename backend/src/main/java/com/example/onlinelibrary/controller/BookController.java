package com.example.onlinelibrary.controller;

import com.example.onlinelibrary.dto.AssignBookRequest;
import com.example.onlinelibrary.dto.BookRequest;
import com.example.onlinelibrary.dto.BookResponse;
import com.example.onlinelibrary.model.User;
import com.example.onlinelibrary.service.BookService;
import com.example.onlinelibrary.service.CurrentUserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public List<BookResponse> findAll() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public BookResponse findById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @GetMapping("/user/{userId}")
    public List<BookResponse> findByUser(@PathVariable Long userId, Authentication authentication) {
        User currentUser = currentUserService.get(authentication);
        return bookService.findByUser(userId, currentUser);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(@Valid @RequestBody BookRequest request) {
        return bookService.create(request);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse createWithThumbnail(
            @RequestParam String title,
            @RequestParam String author,
            @RequestParam Integer year,
            @RequestParam(required = false) MultipartFile thumbnail) {
        return bookService.create(title, author, year, thumbnail);
    }

    @PutMapping("/{id}")
    public BookResponse update(@PathVariable Long id, @Valid @RequestBody BookRequest request, Authentication authentication) {
        User currentUser = currentUserService.get(authentication);
        return bookService.update(id, request, currentUser);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BookResponse updateWithThumbnail(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String author,
            @RequestParam Integer year,
            @RequestParam(required = false) MultipartFile thumbnail,
            Authentication authentication) {
        User currentUser = currentUserService.get(authentication);
        return bookService.update(id, title, author, year, thumbnail, currentUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bookService.delete(id);
    }

    @PostMapping("/{id}/assign")
    public BookResponse assign(@PathVariable Long id, @RequestBody AssignBookRequest request, Authentication authentication) {
        User currentUser = currentUserService.get(authentication);
        return bookService.assign(id, request, currentUser);
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsv() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("books.csv").build());
        return new ResponseEntity<>(bookService.exportCsv(), headers, HttpStatus.OK);
    }
}
