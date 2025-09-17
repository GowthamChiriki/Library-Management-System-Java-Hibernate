package com.library.service;

import com.library.dao.ReservationDAO;
import com.library.model.Book;
import com.library.model.Reservation;
import com.library.model.ReservationStatus;
import com.library.model.User;

import java.util.List;

/**
 * Manages reservations and queueing for books.
 */
public class ReservationService {
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final UserServiceHelper userHelper = new UserServiceHelper();
    private final BookServiceHelper bookHelper = new BookServiceHelper();

    // convenience helper classes (tiny wrappers to avoid circular imports in this example)
    static class UserServiceHelper {
        public User getById(Long id) { return new com.library.dao.UserDAO().findById(id); }
    }
    static class BookServiceHelper {
        public Book getById(Long id) { return new com.library.dao.BookDAO().findById(id); }
    }

    public Reservation createReservation(Long userId, Long bookId) {
        User u = userHelper.getById(userId);
        Book b = bookHelper.getById(bookId);
        if (u == null || b == null) throw new RuntimeException("User or Book not found");
        Reservation r = new Reservation(u, b);
        reservationDAO.save(r);
        return r;
    }

    // returns first waiting reservation for a book (FIFO) or null
    public Reservation peekNextReservation(Long bookId) {
        List<Reservation> list = reservationDAO.findWaitingReservationsByBookId(bookId);
        return (list == null || list.isEmpty()) ? null : list.get(0);
    }

    public void fulfillReservation(Reservation r) {
        r.setStatus(ReservationStatus.FULFILLED);
        reservationDAO.update(r);
    }

    public List<Reservation> listReservationsForUser(Long userId) {
        return reservationDAO.findByUserId(userId);
    }
}
