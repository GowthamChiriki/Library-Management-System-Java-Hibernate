package com.library.model;

import javax.persistence.*;

@Entity
@Table(name = "book_copies")
public class BookCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many BookCopies → One Book, lazy fetch is fine
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookCopyStatus status = BookCopyStatus.AVAILABLE; // default available

    public BookCopy() {}

    public BookCopy(Book book) {
        this.book = book;
        this.status = BookCopyStatus.AVAILABLE;
    }

    // getters and setters
    public Long getId() { return id; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public BookCopyStatus getStatus() { return status; }
    public void setStatus(BookCopyStatus status) { this.status = status; }

    @Override
    public String toString() {
        // Safe: only print book ID to avoid LazyInitializationException
        return "BookCopy{" +
                "id=" + id +
                ", bookId=" + (book != null ? book.getId() : null) +
                ", status=" + status +
                '}';
    }
}
