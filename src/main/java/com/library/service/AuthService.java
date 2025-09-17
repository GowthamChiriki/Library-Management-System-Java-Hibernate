package com.library.service;

import com.library.dao.UserDAO;
import com.library.model.Role;
import com.library.model.User;

import java.util.List;

/**
 * Authentication and simple user management (register/delete/list).
 * In production, passwords must be hashed and security improved.
 */
public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    public User login(String email, String password) {
        User user = userDAO.findByEmail(email);
        if (user == null) return null;
        if (user.getPassword().equals(password)) return user;
        return null;
    }

    public User createUser(String name, String email, String password, Role role) {
        if (userDAO.findByEmail(email) != null) {
            throw new RuntimeException("Email already used");
        }
        User u = new User(name, email, password, role);
        userDAO.save(u);
        return u;
    }

    public void deleteUser(Long userId) {
        User u = userDAO.findById(userId);
        if (u != null) userDAO.delete(u);
    }

    public List<User> listAllUsers() { return userDAO.findAll(); }

    public User findByEmail(String email) { return userDAO.findByEmail(email); }

    public User findById(Long id) { return userDAO.findById(id); }
}
