package com.library.service;

import com.library.dao.ReservationDAO;
import com.library.model.Book;
import com.library.model.Reservation;
import com.library.model.ReservationStatus;
import com.library.model.User;

import java.util.List;

/**
 * Service to manage book reservations and queuing.
 * Supports creating reservations, peeking next in queue, fulfilling reservations, and listing reservations.
 */
public class ReservationService {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final UserServiceHelper userHelper = new UserServiceHelper();
    private final BookServiceHelper bookHelper = new BookServiceHelper();

    // Helper classes to avoid circular dependency
    static class UserServiceHelper {
        public User getById(Long id) {
            return new com.library.dao.UserDAO().findById(id);
        }
    }

    static class BookServiceHelper {
        public Book getById(Long id) {
            return new com.library.dao.BookDAO().findById(id);
        }
    }

    /**
     * Creates a new reservation for a book by a user.
     *
     * @param userId the user making the reservation
     * @param bookId the book to reserve
     * @return the created Reservation
     */
    public Reservation createReservation(Long userId, Long bookId) {
        User user = userHelper.getById(userId);
        Book book = bookHelper.getById(bookId);
        if (user == null || book == null) {
            throw new RuntimeException("User or Book not found");
        }

        Reservation reservation = new Reservation(user, book);
        reservation.setStatus(ReservationStatus.WAITING);
        reservationDAO.save(reservation);
        return reservation;
    }

    /**
     * Returns the first waiting reservation for a book (FIFO).
     *
     * @param bookId the book id
     * @return the next reservation in queue or null if none
     */
    public Reservation peekNextReservation(Long bookId) {
        List<Reservation> waitingList = reservationDAO.findWaitingReservationsByBookId(bookId);
        return (waitingList == null || waitingList.isEmpty()) ? null : waitingList.get(0);
    }

    /**
     * Marks a reservation as fulfilled.
     *
     * @param reservation the reservation to fulfill
     */
    public void fulfillReservation(Reservation reservation) {
        reservation.setStatus(ReservationStatus.FULFILLED);
        reservationDAO.update(reservation);
    }

    /**
     * Lists all reservations made by a specific user.
     *
     * @param userId the user's ID
     * @return list of reservations
     */
    public List<Reservation> listReservationsForUser(Long userId) {
        return reservationDAO.findByUserId(userId);
    }
}
