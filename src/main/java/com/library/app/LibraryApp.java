package com.library.app;

import com.library.model.*;
import com.library.service.*;
import com.library.util.HibernateUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class LibraryApp {

    private static final Scanner scanner = new Scanner(System.in);
    private static final AuthService authService = new AuthService();
    private static final BookService bookService = new BookService();
    private static final ReservationService reservationService = new ReservationService();
    private static final TransactionService transactionService = new TransactionService(reservationService);

    public static void main(String[] args) {
        createDefaultAdmin();
        System.out.println("=== Library Management System (Console) ===");

        boolean running = true;
        while (running) {
            System.out.println("\n1) Login\n2) Exit");
            System.out.print("Choose: ");
            String opt = scanner.nextLine().trim();
            switch (opt) {
                case "1": handleLogin(); break;
                case "2": running = false; break;
                default: System.out.println("Invalid option");
            }
        }

        System.out.println("Shutting down...");
        HibernateUtil.shutdown();
        scanner.close();
    }

    private static void createDefaultAdmin() {
        String defaultAdminEmail = "gowthamsai167@gmail.com";
        if (authService.findByEmail(defaultAdminEmail) == null) {
            authService.createUser("Admin", defaultAdminEmail, "Gowtham@2929", Role.ADMIN);
            System.out.println("Default admin created: email=" + defaultAdminEmail + " password=Gowtham@2929");
        }
    }

    private static void handleLogin() {
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        User user = authService.login(email, password);
        if (user == null) {
            System.out.println("Invalid credentials.");
            return;
        }

        System.out.println("Logged in as: " + user.getName() + " (" + user.getRole() + ")");
        switch (user.getRole()) {
            case ADMIN: adminMenu(user); break;
            case LIBRARIAN: librarianMenu(user); break;
            case MEMBER: memberMenu(user); break;
            default: System.out.println("Unknown role");
        }
    }

    // -------------------- Admin Menu --------------------
    private static void adminMenu(User admin) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1) Manage Users");
            System.out.println("2) Manage Books");
            System.out.println("3) View All Transactions");
            System.out.println("4) Logout");
            System.out.print("Choose: ");
            String opt = scanner.nextLine().trim();
            switch (opt) {
                case "1": manageUsersMenu(); break;
                case "2": manageBooksMenu(); break;
                case "3": viewAllTransactions(); break;
                case "4": back = true; break;
                default: System.out.println("Invalid option");
            }
        }
    }

    private static void manageUsersMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Manage Users ---");
            System.out.println("1) Create User");
            System.out.println("2) List Users");
            System.out.println("3) Delete User");
            System.out.println("4) Back");
            System.out.print("Choose: ");
            String opt = scanner.nextLine().trim();
            switch (opt) {
                case "1":
                    System.out.print("Name: "); String name = scanner.nextLine().trim();
                    System.out.print("Email: "); String email = scanner.nextLine().trim();
                    System.out.print("Password: "); String pwd = scanner.nextLine().trim();
                    System.out.print("Role (ADMIN/LIBRARIAN/MEMBER): "); String roleStr = scanner.nextLine().trim();
                    try {
                        Role role = Role.valueOf(roleStr.toUpperCase());
                        authService.createUser(name, email, pwd, role);
                        System.out.println("User created.");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case "2":
                    List<User> users = authService.listAllUsers();
                    users.forEach(System.out::println);
                    break;
                case "3":
                    System.out.print("User id to delete: ");
                    try {
                        authService.deleteUser(Long.parseLong(scanner.nextLine()));
                        System.out.println("Deleted if existed.");
                    } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
                    break;
                case "4": back = true; break;
                default: System.out.println("Invalid option");
            }
        }
    }

    private static void manageBooksMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Manage Books ---");
            System.out.println("1) Add Book");
            System.out.println("2) Update Book");
            System.out.println("3) Delete Book");
            System.out.println("4) List All Books");
            System.out.println("5) Back");
            System.out.print("Choose: ");
            String opt = scanner.nextLine().trim();
            switch (opt) {
                case "1":
                    System.out.print("Title: "); String title = scanner.nextLine().trim();
                    System.out.print("Author: "); String author = scanner.nextLine().trim();
                    System.out.print("ISBN: "); String isbn = scanner.nextLine().trim();
                    System.out.print("Category: "); String cat = scanner.nextLine().trim();
                    System.out.print("Number of copies: "); int copies = Integer.parseInt(scanner.nextLine());
                    Book b = bookService.addBook(title, author, isbn, cat, copies);
                    System.out.println("Added book: " + b);
                    break;
                case "2":
                    System.out.print("Book id to update: "); Long id = Long.parseLong(scanner.nextLine());
                    Book book = bookService.findById(id);
                    if (book == null) { System.out.println("Not found"); break; }
                    System.out.print("New title (blank to keep): "); String nt = scanner.nextLine().trim();
                    System.out.print("New author (blank to keep): "); String na = scanner.nextLine().trim();
                    System.out.print("New category (blank to keep): "); String nc = scanner.nextLine().trim();
                    if (!nt.isEmpty()) book.setTitle(nt);
                    if (!na.isEmpty()) book.setAuthor(na);
                    if (!nc.isEmpty()) book.setCategory(nc);
                    bookService.updateBook(book);
                    System.out.println("Updated.");
                    break;
                case "3":
                    System.out.print("Book id to delete: "); Long delId = Long.parseLong(scanner.nextLine());
                    bookService.deleteBook(delId);
                    System.out.println("Deleted if existed.");
                    break;
                case "4":
                    bookService.listAll().forEach(System.out::println);
                    break;
                case "5": back = true; break;
                default: System.out.println("Invalid option");
            }
        }
    }

    // -------------------- Admin: Transactions Table --------------------
    private static void viewAllTransactions() {
        List<Transaction> transactions = transactionService.listAllTransactions();
        int loanDays = transactionService.getLoanPeriodDays();

        long totalTx = transactions.size();
        long activeTx = transactions.stream().filter(t -> t.getReturnDate() == null || t.getReturnDate().isAfter(LocalDate.now())).count();
        double totalFines = transactions.stream().mapToDouble(t -> t.getFine() != null ? t.getFine() : 0.0).sum();

        System.out.println("\n=== Transactions Summary ===");
        System.out.println("Total transactions: " + totalTx);
        System.out.println("Active transactions: " + activeTx);
        System.out.println("Total fines collected: " + totalFines);

        System.out.println("\n=== All Transactions ===");
        System.out.printf("%-5s %-20s %-30s %-7s %-12s %-12s %-12s %-7s%n",
                "ID", "User", "Book", "Copy", "Issue Date", "Due Date", "Return Date", "Fine");

        for (Transaction t : transactions) {
            LocalDate dueDate = t.getIssueDate().plusDays(loanDays);
            System.out.printf("%-5d %-20s %-30s %-7d %-12s %-12s %-12s %-7.2f%n",
                    t.getId(),
                    t.getUser().getName() + " (" + t.getUser().getEmail() + ")",
                    t.getBookCopy().getBook().getTitle(),
                    t.getBookCopy().getId(),
                    t.getIssueDate(),
                    dueDate,
                    t.getReturnDate() != null ? t.getReturnDate() : "-",
                    t.getFine() != null ? t.getFine() : 0.0
            );
        }
    }

    // -------------------- Librarian Menu --------------------
    private static void librarianMenu(User librarian) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Librarian Menu ---");
            System.out.println("1) Issue Book");
            System.out.println("2) Return Book");
            System.out.println("3) List Active Transactions for a User");
            System.out.println("4) Logout");
            System.out.print("Choose: ");
            String opt = scanner.nextLine().trim();
            switch (opt) {
                case "1":
                    System.out.print("Member email: "); String memEmail = scanner.nextLine().trim();
                    User member = authService.findByEmail(memEmail);
                    if (member == null) { System.out.println("Member not found"); break; }
                    System.out.print("Book id or ISBN? (enter 'isbn:xxxxx' or just id): ");
                    String bookInp = scanner.nextLine().trim();
                    Long bookId = bookInp.startsWith("isbn:") ?
                            bookService.findByIsbn(bookInp.substring(5)).getId() :
                            Long.parseLong(bookInp);
                    System.out.println(transactionService.issueBook(member.getId(), bookId));
                    break;
                case "2":
                    System.out.print("Member email: "); String memE = scanner.nextLine().trim();
                    User mem = authService.findByEmail(memE);
                    if (mem == null) { System.out.println("Member not found"); break; }
                    System.out.print("Book copy id to return: "); Long copyId = Long.parseLong(scanner.nextLine());
                    System.out.println(transactionService.returnBook(mem.getId(), copyId));
                    break;
                case "3":
                    System.out.print("Member email: "); String me = scanner.nextLine().trim();
                    User u = authService.findByEmail(me);
                    if (u == null) { System.out.println("Member not found"); break; }
                    List<Transaction> active = transactionService.listActiveUserTransactions(u.getId());
                    active.forEach(System.out::println);
                    break;
                case "4": back = true; break;
                default: System.out.println("Invalid option");
            }
        }
    }

    // -------------------- Member Menu --------------------
    private static void memberMenu(User member) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Member Menu ---");
            System.out.println("1) Search Books by Title");
            System.out.println("2) Search Books by Author");
            System.out.println("3) Borrow Book");
            System.out.println("4) Return Book");
            System.out.println("5) My Transactions and Fines");
            System.out.println("6) My Reservations");
            System.out.println("7) Logout");
            System.out.print("Choose: ");
            String opt = scanner.nextLine().trim();
            switch (opt) {
                case "1":
                    System.out.print("Title search: "); String t = scanner.nextLine().trim();
                    bookService.searchByTitle(t).forEach(b -> {
                        System.out.println(b);
                        bookService.listCopies(b.getId()).forEach(System.out::println);
                    });
                    break;
                case "2":
                    System.out.print("Author search: "); String a = scanner.nextLine().trim();
                    bookService.searchByAuthor(a).forEach(b -> {
                        System.out.println(b);
                        bookService.listCopies(b.getId()).forEach(System.out::println);
                    });
                    break;
                case "3":
                    System.out.print("Book id to borrow: "); Long bid = Long.parseLong(scanner.nextLine());
                    System.out.println(transactionService.issueBook(member.getId(), bid));
                    break;
                case "4":
                    System.out.print("Book copy id to return: "); Long cid = Long.parseLong(scanner.nextLine());
                    System.out.println(transactionService.returnBook(member.getId(), cid));
                    break;
                case "5":
                    double totalFine = 0.0;
                    List<Transaction> txs = transactionService.listUserTransactions(member.getId());
                    for (Transaction tx : txs) {
                        System.out.println(tx);
                        if (tx.getFine() != null) totalFine += tx.getFine();
                    }
                    System.out.println("Total fine (sum of recorded fines): " + totalFine);
                    break;
                case "6":
                    reservationService.listReservationsForUser(member.getId()).forEach(System.out::println);
                    break;
                case "7": back = true; break;
                default: System.out.println("Invalid option");
            }
        }
    }
}
