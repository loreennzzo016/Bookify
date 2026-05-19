package com.example.onlinelibrary.repository;

import com.example.onlinelibrary.model.Book;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByAssignedToId(Long userId);

    boolean existsByTitle(String title);
}
