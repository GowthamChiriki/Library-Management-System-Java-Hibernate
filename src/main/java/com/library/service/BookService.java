package com.library.service;

import com.library.dao.BookCopyDAO;
import com.library.dao.BookDAO;
import com.library.model.Book;
import com.library.model.BookCopy;

import java.util.List;

/**
 * Book management: add/update/delete/search and add copies.
 */
public class BookService {
    private final BookDAO bookDAO = new BookDAO();
    private final BookCopyDAO copyDAO = new BookCopyDAO();

    public Book addBook(String title, String author, String isbn, String category, int copies) {
        Book b = new Book(title, author, isbn, category);
        for (int i = 0; i < copies; i++) {
            BookCopy c = new BookCopy();
            c.setStatus(com.library.model.BookCopyStatus.AVAILABLE);
            b.addCopy(c);
        }
        bookDAO.save(b);
        return b;
    }

    public void updateBook(Book book) {
        bookDAO.update(book);
    }

    public void deleteBook(Long id) {
        Book b = bookDAO.findById(id);
        if (b != null) bookDAO.delete(b);
    }

    public Book findById(Long id) { return bookDAO.findById(id); }
    public Book findByIsbn(String isbn) { return bookDAO.findByIsbn(isbn); }
    public List<Book> searchByTitle(String t) { return bookDAO.searchByTitle(t); }
    public List<Book> searchByAuthor(String a) { return bookDAO.searchByAuthor(a); }
    public List<Book> listAll() { return bookDAO.findAll(); }

    public BookCopy addCopyToBook(Long bookId) {
        Book b = bookDAO.findById(bookId);
        if (b == null) throw new RuntimeException("Book not found");
        BookCopy c = new BookCopy();
        c.setBook(b);
        c.setStatus(com.library.model.BookCopyStatus.AVAILABLE);
        b.addCopy(c);
        bookDAO.update(b); // cascade saves copy due to CascadeType.ALL
        return c;
    }

    public List<BookCopy> listCopies(Long bookId) { return copyDAO.listCopiesByBookId(bookId); }
}
