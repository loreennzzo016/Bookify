package com.example.onlinelibrary.service;

import com.example.onlinelibrary.dto.AssignBookRequest;
import com.example.onlinelibrary.dto.BookRequest;
import com.example.onlinelibrary.dto.BookResponse;
import com.example.onlinelibrary.model.Book;
import com.example.onlinelibrary.model.Role;
import com.example.onlinelibrary.model.User;
import com.example.onlinelibrary.repository.BookRepository;
import com.example.onlinelibrary.repository.UserRepository;
import com.example.onlinelibrary.utils.CsvUtils;
import com.example.onlinelibrary.utils.ImageUtils;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final MapperService mapperService;
    private final ImageUtils imageUtils;
    private final CsvUtils csvUtils;

    public List<BookResponse> findAll() {
        return bookRepository.findAll().stream()
                .map(mapperService::toBookResponse)
                .toList();
    }

    public BookResponse findById(Long id) {
        return mapperService.toBookResponse(getBook(id));
    }

    public List<BookResponse> findByUser(Long userId) {
        return bookRepository.findByAssignedToId(userId).stream()
                .map(mapperService::toBookResponse)
                .toList();
    }

    public List<BookResponse> findByUser(Long userId, User currentUser) {
        if (currentUser.getRole() == Role.USER && !currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("Users can only list their own books");
        }
        return findByUser(userId);
    }

    public BookResponse create(BookRequest request) {
        return create(request.getTitle(), request.getAuthor(), request.getYear(), null);
    }

    public BookResponse create(String title, String author, Integer year, MultipartFile thumbnail) {
        Book book = Book.builder()
                .title(title)
                .author(author)
                .year(year)
                .thumbnail(imageUtils.toDataUri(thumbnail))
                .build();
        return mapperService.toBookResponse(bookRepository.save(book));
    }

    public BookResponse update(Long id, BookRequest request, User currentUser) {
        return update(id, request.getTitle(), request.getAuthor(), request.getYear(), null, currentUser);
    }

    public BookResponse update(Long id, String title, String author, Integer year, MultipartFile thumbnail, User currentUser) {
        Book book = getBook(id);
        ensureAdminOrAssigned(book, currentUser);
        book.setTitle(title);
        book.setAuthor(author);
        book.setYear(year);
        String thumbnailData = imageUtils.toDataUri(thumbnail);
        if (thumbnailData != null) {
            book.setThumbnail(thumbnailData);
        }
        return mapperService.toBookResponse(bookRepository.save(book));
    }

    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Book not found");
        }
        bookRepository.deleteById(id);
    }

    public BookResponse assign(Long id, AssignBookRequest request, User currentUser) {
        Book book = getBook(id);
        if (currentUser.getRole() == Role.USER) {
            boolean assigningToSelf = request.getUserId() != null && request.getUserId().equals(currentUser.getId());
            boolean unassigningOwnBook = request.getUserId() == null
                    && book.getAssignedTo() != null
                    && book.getAssignedTo().getId().equals(currentUser.getId());
            if (!assigningToSelf && !unassigningOwnBook) {
                throw new AccessDeniedException("Users can only manage their own assignments");
            }
        }
        User user = request.getUserId() == null ? null : userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        book.setAssignedTo(user);
        return mapperService.toBookResponse(bookRepository.save(book));
    }

    public byte[] exportCsv() {
        return csvUtils.booksToCsv(bookRepository.findAll());
    }

    private Book getBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
    }

    private void ensureAdminOrAssigned(Book book, User currentUser) {
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isAssigned = book.getAssignedTo() != null && book.getAssignedTo().getId().equals(currentUser.getId());
        if (!isAdmin && !isAssigned) {
            throw new AccessDeniedException("You can only manage books assigned to you");
        }
    }
}
