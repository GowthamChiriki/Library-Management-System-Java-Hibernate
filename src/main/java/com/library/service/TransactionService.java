package com.library.service;

import com.library.dao.BookCopyDAO;
import com.library.dao.TransactionDAO;
import com.library.model.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service for handling book issue, return, and transactions.
 * Supports fines, loan period, and automatic reservation fulfillment.
 */
public class TransactionService {

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final BookCopyDAO copyDAO = new BookCopyDAO();
    private final com.library.dao.BookDAO bookDAO = new com.library.dao.BookDAO();
    private final com.library.dao.UserDAO userDAO = new com.library.dao.UserDAO();
    private final ReservationService reservationService;

    // Loan policy
    private final int loanPeriodDays = 7;      // default 1 week
    private final double finePerDay = 20.0;    // 20 rupees/day late

    public TransactionService(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // -------------------- Issue Book --------------------
    public String issueBook(Long userId, Long bookId) {
        User user = userDAO.findById(userId);
        if (user == null) return "User not found";

        Book book = bookDAO.findById(bookId);
        if (book == null) return "Book not found";

        // Check for available copy
        BookCopy available = copyDAO.findAvailableCopyByBookId(bookId);
        if (available == null) {
            // Enforce maxReservationsPerUser
            long userReservations = reservationService.listReservationsForUser(userId).stream()
                    .filter(r -> r.getBook().getId().equals(bookId) && r.getStatus() == ReservationStatus.WAITING)
                    .count();

            if (userReservations >= book.getMaxReservationsPerUser()) {
                return "No copies available and you have reached the maximum reservations for this book.";
            }

            reservationService.createReservation(userId, bookId);
            return "No copies available. Reservation added to queue.";
        }

        // Issue the available copy
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
        t.setStatus(TransactionStatus.ACTIVE);

        transactionDAO.save(t);
        return "Book issued. Copy id: " + available.getId() + ", due date: " + dueDate;
    }

    // -------------------- Return Book --------------------
    public String returnBook(Long userId, Long bookCopyId) {
        Transaction active = transactionDAO.findActiveTransactionByUserAndCopy(userId, bookCopyId);
        if (active == null) return "No active transaction found for this user and copy.";

        LocalDate today = LocalDate.now();
        active.setReturnDate(today);

        // Fine calculation
        double fine = 0.0;
        if (today.isAfter(active.getDueDate())) {
            long daysLate = ChronoUnit.DAYS.between(active.getDueDate(), today);
            fine = daysLate * finePerDay;
        }
        active.setFine(fine);
        active.setStatus(TransactionStatus.CLOSED);
        transactionDAO.update(active);

        // Mark copy available
        BookCopy copy = active.getBookCopy();
        copy.setStatus(BookCopyStatus.AVAILABLE);
        copyDAO.update(copy);

        String message = "Book returned. Fine: " + fine;

        // Handle next reservation in queue
        Reservation next = reservationService.peekNextReservation(copy.getBook().getId());
        if (next != null) {
            // Check maxReservationsPerUser for next user
            long nextUserReservations = reservationService.listReservationsForUser(next.getUser().getId()).stream()
                    .filter(r -> r.getBook().getId().equals(copy.getBook().getId()) && r.getStatus() == ReservationStatus.WAITING)
                    .count();

            if (nextUserReservations <= copy.getBook().getMaxReservationsPerUser()) {
                // Issue book to next reservation
                copy.setStatus(BookCopyStatus.ISSUED);
                copyDAO.update(copy);

                Transaction newT = new Transaction();
                newT.setUser(next.getUser());
                newT.setBookCopy(copy);
                newT.setIssueDate(LocalDate.now());
                newT.setDueDate(LocalDate.now().plusDays(loanPeriodDays));
                newT.setFine(0.0);
                newT.setStatus(TransactionStatus.ACTIVE);
                transactionDAO.save(newT);

                // Fulfill reservation
                reservationService.fulfillReservation(next);
                message += ". Next reservation fulfilled and auto-issued to " + next.getUser().getEmail();
            }
        }

        return message;
    }

    // -------------------- Transaction Queries --------------------
    public List<Transaction> listAllTransactions() {
        return transactionDAO.findAll();
    }

    public List<Transaction> listUserTransactions(Long userId) {
        return transactionDAO.findTransactionsByUser(userId);
    }

    public List<Transaction> listActiveUserTransactions(Long userId) {
        return transactionDAO.findActiveTransactionsByUser(userId);
    }

    // -------------------- Loan Policy Getters --------------------
    public int getLoanPeriodDays() { return loanPeriodDays; }
    public double getFinePerDay() { return finePerDay; }
}
