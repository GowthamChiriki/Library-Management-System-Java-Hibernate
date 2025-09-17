package com.library.dao;

import com.library.model.Reservation;
import com.library.model.ReservationStatus;
import com.library.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

/**
 * DAO for Reservation entity.
 * Provides CRUD operations and specialized queries for reservations.
 */
public class ReservationDAO {

    /**
     * Save a new reservation.
     *
     * @param reservation the reservation to save
     */
    public void save(Reservation reservation) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            org.hibernate.Transaction tx = session.beginTransaction();
            session.save(reservation);
            tx.commit();
        }
    }

    /**
     * Update an existing reservation.
     *
     * @param reservation the reservation to update
     */
    public void update(Reservation reservation) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            org.hibernate.Transaction tx = session.beginTransaction();
            session.update(reservation);
            tx.commit();
        }
    }

    /**
     * Find a reservation by ID.
     *
     * @param id reservation ID
     * @return Reservation object or null if not found
     */
    public Reservation findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Reservation.class, id);
        }
    }

    /**
     * Get all waiting reservations for a book in FIFO order.
     * Uses eager fetching for User and Book to prevent LazyInitializationException.
     *
     * @param bookId book ID
     * @return list of waiting reservations
     */
    public List<Reservation> findWaitingReservationsByBookId(Long bookId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "select r from Reservation r " +
                                    "join fetch r.user " +
                                    "join fetch r.book " +
                                    "where r.book.id = :bookId " +
                                    "and r.status = :status " +
                                    "order by r.createdDate asc",
                            Reservation.class)
                    .setParameter("bookId", bookId)
                    .setParameter("status", ReservationStatus.WAITING)
                    .list();
        }
    }

    /**
     * Get all reservations for a specific user, ordered by creation date descending.
     * Eager fetch user and book to avoid lazy loading issues.
     *
     * @param userId user ID
     * @return list of reservations
     */
    public List<Reservation> findByUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "select r from Reservation r " +
                                    "join fetch r.user " +
                                    "join fetch r.book " +
                                    "where r.user.id = :userId " +
                                    "order by r.createdDate desc",
                            Reservation.class)
                    .setParameter("userId", userId)
                    .list();
        }
    }
}
