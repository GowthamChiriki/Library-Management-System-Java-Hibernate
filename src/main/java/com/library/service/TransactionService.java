package com.library.service;

import com.library.dao.BookCopyDAO;
import com.library.dao.TransactionDAO;
import com.library.model.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TransactionService {

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final BookCopyDAO copyDAO = new BookCopyDAO();
    private final com.library.dao.BookDAO bookDAO = new com.library.dao.BookDAO();
    private final com.library.dao.UserDAO userDAO = new com.library.dao.UserDAO();
    private final ReservationService reservationService;

    // Loan policy
    private final int loanPeriodDays = 7; // default 1 week
    private final double finePerDay = 20.0; // 20 rupees/day late

    public TransactionService(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // Issue a book
    public String issueBook(Long userId, Long bookId) {
        User user = userDAO.findById(userId);
        if (user == null) return "User not found";

        Book book = bookDAO.findById(bookId);
        if (book == null) return "Book not found";

        BookCopy available = copyDAO.findAvailableCopyByBookId(bookId);
        if (available == null) {
            reservationService.createReservation(userId, bookId);
            return "No copies available. Reservation added to queue.";
        }

        available.setStatus(BookCopyStatus.ISSUED);
        copyDAO.update(available);

        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(loanPeriodDays);

        Transaction t = new Transaction();
        t.setUser(user);
        t.setBookCopy(available);
        t.setIssueDate(issueDate);
        t.setDueDate(dueDate);
        t.setFine(0.0);

        transactionDAO.save(t);
        return "Book issued. Copy id: " + available.getId() + ", due date: " + dueDate;
    }

    // Return a book
    public String returnBook(Long userId, Long bookCopyId) {
        Transaction active = transactionDAO.findActiveTransactionByUserAndCopy(userId, bookCopyId);
        if (active == null) return "No active transaction found for this user and copy.";

        LocalDate today = LocalDate.now();
        active.setReturnDate(today);

        // fine calculation
        double fine = 0.0;
        if (today.isAfter(active.getDueDate())) {
            long daysLate = ChronoUnit.DAYS.between(active.getDueDate(), today);
            fine = daysLate * finePerDay;
        }
        active.setFine(fine);
        transactionDAO.update(active);

        // mark copy available
        BookCopy copy = copyDAO.findById(bookCopyId);
        copy.setStatus(BookCopyStatus.AVAILABLE);
        copyDAO.update(copy);

        // handle next reservation
        Reservation next = reservationService.peekNextReservation(copy.getBook().getId());
        if (next != null) {
            reservationService.fulfillReservation(next);
            copy.setStatus(BookCopyStatus.ISSUED);
            copyDAO.update(copy);

            Transaction newT = new Transaction();
            newT.setUser(next.getUser());
            newT.setBookCopy(copy);
            newT.setIssueDate(LocalDate.now());
            newT.setDueDate(LocalDate.now().plusDays(loanPeriodDays));
            newT.setFine(0.0);
            transactionDAO.save(newT);

            return "Book returned. Fine: " + fine + ". Next reservation fulfilled and auto-issued to " + next.getUser().getEmail();
        }

        return "Book returned. Fine: " + fine;
    }

    // List all transactions (for admin)
    public List<Transaction> listAllTransactions() {
        return transactionDAO.findAll();
    }

    public List<Transaction> listUserTransactions(Long userId) {
        return transactionDAO.findTransactionsByUser(userId);
    }

    public List<Transaction> listActiveUserTransactions(Long userId) {
        return transactionDAO.findActiveTransactionsByUser(userId);
    }

    // Getter for loan period
    public int getLoanPeriodDays() {
        return loanPeriodDays;
    }

    public double getFinePerDay() {
        return finePerDay;
    }
}
