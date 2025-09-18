package com.library.dao;

import com.library.model.Transaction;
import com.library.model.TransactionStatus;
import com.library.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class TransactionDAO {

    // Save a new transaction
    public void save(Transaction t) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            org.hibernate.Transaction tx = session.beginTransaction();
            session.save(t);
            tx.commit();
        }
    }

    // Update existing transaction
    public void update(Transaction t) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            org.hibernate.Transaction tx = session.beginTransaction();
            session.update(t);
            tx.commit();
        }
    }

    // Find transaction by ID with eager fetching
    public Transaction findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "select t from Transaction t " +
                                    "join fetch t.user " +
                                    "join fetch t.bookCopy bc " +
                                    "join fetch bc.book " +
                                    "where t.id = :id", Transaction.class)
                    .setParameter("id", id)
                    .uniqueResult();
        }
    }

    // Active transaction for a specific user and book copy (not yet returned)
    public Transaction findActiveTransactionByUserAndCopy(Long userId, Long bookCopyId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "select t from Transaction t " +
                                    "join fetch t.user " +
                                    "join fetch t.bookCopy bc " +
                                    "join fetch bc.book " +
                                    "where t.user.id = :userId " +
                                    "and t.bookCopy.id = :copyId " +
                                    "and t.returnDate is null",
                            Transaction.class)
                    .setParameter("userId", userId)
                    .setParameter("copyId", bookCopyId)
                    .uniqueResult();
        }
    }

    // All transactions for a user
    public List<Transaction> findTransactionsByUser(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "select t from Transaction t " +
                                    "join fetch t.bookCopy bc " +
                                    "join fetch bc.book " +
                                    "where t.user.id = :userId " +
                                    "order by t.issueDate desc",
                            Transaction.class)
                    .setParameter("userId", userId)
                    .list();
        }
    }

    // All active (not returned) transactions for a user
    public List<Transaction> findActiveTransactionsByUser(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "select t from Transaction t " +
                                    "join fetch t.bookCopy bc " +
                                    "join fetch bc.book " +
                                    "where t.user.id = :userId " +
                                    "and t.returnDate is null " +
                                    "order by t.issueDate desc",
                            Transaction.class)
                    .setParameter("userId", userId)
                    .list();
        }
    }

    // All transactions (for admin) with eager fetching of user and book copy
    public List<Transaction> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "select t from Transaction t " +
                                    "join fetch t.user " +
                                    "join fetch t.bookCopy bc " +
                                    "join fetch bc.book " +
                                    "order by t.issueDate desc",
                            Transaction.class)
                    .list();
        }
    }
    public List<Transaction> findAllActiveTransactions() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from Transaction t where t.status = :active", Transaction.class)
                    .setParameter("active", TransactionStatus.ACTIVE)
                    .list();
        }
    }

    public List<com.library.model.Transaction> getAllActiveTransactions() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Transaction> query = session.createQuery(
                    "FROM Transaction t WHERE t.status = :status",
                    com.library.model.Transaction.class
            );
            query.setParameter("status", TransactionStatus.ACTIVE);
            return query.list();
        }
    }

}
