package com.library.dao;

import com.library.model.BookCopy;
import com.library.model.BookCopyStatus;
import com.library.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class BookCopyDAO {

    public void save(BookCopy copy) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            org.hibernate.Transaction tx = session.beginTransaction();
            session.save(copy);
            tx.commit();
        }
    }

    public void update(BookCopy copy) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            org.hibernate.Transaction tx = session.beginTransaction();
            session.update(copy);
            tx.commit();
        }
    }

    public BookCopy findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(BookCopy.class, id);
        }
    }

    public BookCopy findAvailableCopyByBookId(Long bookId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<BookCopy> list = session.createQuery("from BookCopy where book.id = :bId and status = :st", BookCopy.class)
                    .setParameter("bId", bookId)
                    .setParameter("st", BookCopyStatus.AVAILABLE)
                    .setMaxResults(1)
                    .list();
            return list.isEmpty() ? null : list.get(0);
        }
    }

    public List<BookCopy> listCopiesByBookId(Long bookId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from BookCopy where book.id = :bId", BookCopy.class)
                    .setParameter("bId", bookId).list();
        }
    }
}
