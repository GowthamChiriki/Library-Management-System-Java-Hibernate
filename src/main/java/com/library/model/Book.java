package com.library.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String title;

    private String author;

    @Column(unique=true)
    private String isbn;

    private String category;

    // One Book -> Many BookCopies, fetch eagerly to avoid LazyInitializationException
    @OneToMany(mappedBy = "book", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookCopy> copies = new ArrayList<>();

    public Book() {}

    public Book(String title, String author, String isbn, String category) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
    }

    // helpers to manage copies
    public void addCopy(BookCopy copy) {
        copies.add(copy);
        copy.setBook(this);
    }

    public void removeCopy(BookCopy copy) {
        copies.remove(copy);
        copy.setBook(null);
    }

    // getters and setters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public List<BookCopy> getCopies() { return copies; }

    @Override
    public String toString() {
        // safe printing: only count copies, do not trigger lazy load
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", category='" + category + '\'' +
                ", copiesCount=" + (copies != null ? copies.size() : 0) +
                '}';
    }
}
