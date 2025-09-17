package com.library.util;

import com.library.model.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml"); // your config file

            // Add annotated classes
            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(Book.class);
            configuration.addAnnotatedClass(BookCopy.class);
            configuration.addAnnotatedClass(Transaction.class);
            configuration.addAnnotatedClass(Reservation.class);

            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}
