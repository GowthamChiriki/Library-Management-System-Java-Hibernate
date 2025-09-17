package com.library.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_copy_id", nullable = false)
    private BookCopy bookCopy;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(name = "fine_amount")
    private Double fine = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status = TransactionStatus.ACTIVE;

    public Transaction() {}

    public Transaction(User user, BookCopy bookCopy, LocalDate issueDate, int loanPeriodDays) {
        this.user = user;
        this.bookCopy = bookCopy;
        this.issueDate = issueDate;
        this.dueDate = issueDate.plusDays(loanPeriodDays);
        this.fine = 0.0;
        this.status = TransactionStatus.ACTIVE;
    }

    // ===================== Getters & Setters =====================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public BookCopy getBookCopy() { return bookCopy; }
    public void setBookCopy(BookCopy bookCopy) { this.bookCopy = bookCopy; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public Double getFine() { return fine; }
    public void setFine(Double fine) { this.fine = fine; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    // ===================== Table Format =====================
    public static void printHeader() {
        System.out.printf("%-5s | %-15s | %-10s | %-10s | %-10s | %-6s | %-8s%n",
                "ID", "User", "CopyID", "IssueDate", "DueDate", "Fine", "Status");
        System.out.println("----------------------------------------------------------------------");
    }

    public void printRow() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.printf("%-5d | %-15s | %-10d | %-10s | %-10s | %-6.2f | %-8s%n",
                id,
                (user != null ? user.getName() : "N/A"),
                (bookCopy != null ? bookCopy.getId() : 0),
                (issueDate != null ? df.format(issueDate) : "N/A"),
                (dueDate != null ? df.format(dueDate) : "N/A"),
                (fine != null ? fine : 0.0),
                (status != null ? status : "N/A")
        );
    }

    @Override
    public String toString() {
        return "Transaction{" + "id=" + id +
                ", user=" + (user != null ? user.getName() : null) +
                ", copyId=" + (bookCopy != null ? bookCopy.getId() : null) +
                ", issueDate=" + issueDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                ", fine=" + fine +
                ", status=" + status +
                '}';
    }
}
