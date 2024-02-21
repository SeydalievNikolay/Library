package org.seydaliev.Service.impl;

import org.seydaliev.Service.BookService;
import org.seydaliev.exception.GlobalExceptionHandler;
import org.seydaliev.exception.ResourceNotFoundException;
import org.seydaliev.model.Book;
import org.seydaliev.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookRepository bookRepository;

    public Page<Book> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable must not be null");
        }
        try {
            return bookRepository.findAll(pageable);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Book", "id", pageable);
        }
    }

    public Book findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Book id must not be null");
        }
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
    }

    public Book save(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book must not be null");
        }
        try {
            return bookRepository.save(book);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while saving the book", e);
        }
    }

    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Book id must not be null");
        }
        try {
            bookRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while deleting the book", e);
        }
    }
}
