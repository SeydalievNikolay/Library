package org.seydaliev.Service;

import org.seydaliev.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    Page<Book> findAll(Pageable pageable);
    Book findById(Long id);
    Book save(Book book);
    void delete(Long id);
}
