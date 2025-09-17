package com.library.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="book_id", nullable=false)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private ReservationStatus status = ReservationStatus.WAITING;

    @Column(name="created_date", nullable=false)
    private LocalDate createdDate = LocalDate.now();

    public Reservation() {}
    public Reservation(User u, Book b) { this.user = u; this.book = b; this.status = ReservationStatus.WAITING; this.createdDate = LocalDate.now(); }

    // getters/setters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User u) { this.user = u; }
    public Book getBook() { return book; }
    public void setBook(Book b) { this.book = b; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus s) { this.status = s; }
    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate d) { this.createdDate = d; }

    @Override
    public String toString() {
        return "Reservation{" + "id=" + id + ", userId=" + (user!=null ? user.getId() : null) +
                ", bookId=" + (book!=null ? book.getId() : null) + ", status=" + status +
                ", createdDate=" + createdDate + '}';
    }
}
