package com.library.dao;

import com.library.model.Book;
import com.library.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class BookDAO {

    public void save(Book book) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            org.hibernate.Transaction tx = session.beginTransaction();
            session.save(book);
            tx.commit();
        }
    }

    public void update(Book book) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            org.hibernate.Transaction tx = session.beginTransaction();
            session.update(book);
            tx.commit();
        }
    }

    public void delete(Book book) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            org.hibernate.Transaction tx = session.beginTransaction();
            session.delete(book);
            tx.commit();
        }
    }

    public Book findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Book.class, id);
        }
    }

    public Book findByIsbn(String isbn) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Book where isbn = :isbn", Book.class)
                    .setParameter("isbn", isbn)
                    .uniqueResult();
        }
    }

    public List<Book> searchByTitle(String title) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Book where lower(title) like :t", Book.class)
                    .setParameter("t", "%" + title.toLowerCase() + "%").list();
        }
    }

    public List<Book> searchByAuthor(String author) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Book where lower(author) like :a", Book.class)
                    .setParameter("a", "%" + author.toLowerCase() + "%").list();
        }
    }

    public List<Book> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Book", Book.class).list();
        }
    }
}
