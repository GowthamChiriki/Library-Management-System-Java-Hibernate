package com.library.dao;

import com.library.model.Reservation;
import com.library.model.ReservationStatus;
import com.library.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class ReservationDAO {

    public void save(Reservation r) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            org.hibernate.Transaction tx = session.beginTransaction();
            session.save(r);
            tx.commit();
        }
    }

    public void update(Reservation r) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            org.hibernate.Transaction tx = session.beginTransaction();
            session.update(r);
            tx.commit();
        }
    }

    public Reservation findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Reservation.class, id);
        }
    }

    // get waiting reservations for a book in FIFO order
    public List<Reservation> findWaitingReservationsByBookId(Long bookId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from Reservation r where r.book.id = :bId and r.status = :st order by r.createdDate asc",
                            Reservation.class)
                    .setParameter("bId", bookId)
                    .setParameter("st", ReservationStatus.WAITING)
                    .list();
        }
    }

    public List<Reservation> findByUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Reservation r where r.user.id = :u order by r.createdDate desc", Reservation.class)
                    .setParameter("u", userId).list();
        }
    }
}
