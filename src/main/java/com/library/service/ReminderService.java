package com.library.service;

import com.library.model.Transaction;
import com.library.util.EmailUtil;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Sends daily reminders for due dates.
 */
public class ReminderService {
    private final TransactionService transactionService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public ReminderService(TransactionService transactionService) {
        this.transactionService = transactionService;

        // Schedule daily reminders at 9:00 AM
        scheduleDailyReminder(6, 30);
    }

    private void scheduleDailyReminder(int hour, int minute) {
        Runnable task = this::sendDueDateReminders;

        long initialDelay = computeInitialDelay(hour, minute);
        long period = TimeUnit.DAYS.toSeconds(1);

        scheduler.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
    }

    private long computeInitialDelay(int targetHour, int targetMinute) {
        Calendar nextRun = Calendar.getInstance();
        nextRun.set(Calendar.HOUR_OF_DAY, targetHour);
        nextRun.set(Calendar.MINUTE, targetMinute);
        nextRun.set(Calendar.SECOND, 0);

        Calendar now = Calendar.getInstance();
        if (now.after(nextRun)) {
            nextRun.add(Calendar.DATE, 1);
        }
        return (nextRun.getTimeInMillis() - now.getTimeInMillis()) / 1000;
    }

    /**
     * Sends reminders for all active transactions with due dates.
     */
    private void sendDueDateReminders() {
        List<Transaction> activeTx = transactionService.listAllTransactions();
        int loanDays = transactionService.getLoanPeriodDays();
        LocalDate today = LocalDate.now();

        for (Transaction tx : activeTx) {
            if (tx.getReturnDate() == null) {
                LocalDate dueDate = tx.getIssueDate().plusDays(loanDays);
                long daysLeft = ChronoUnit.DAYS.between(today, dueDate);

                if (daysLeft >= 0) {
                    String subject = "Reminder: Book Due in " + daysLeft + " days";
                    String body = "Dear " + tx.getUser().getName() + ",\n\n"
                            + "This is a reminder for your borrowed book:\n"
                            + "Book: " + tx.getBookCopy().getBook().getTitle() + "\n"
                            + "Issue Date: " + tx.getIssueDate() + "\n"
                            + "Due Date: " + dueDate + "\n\n"
                            + "Please return it on or before the due date.\n"
                            + "Otherwise, a fine of ₹20 per day will be applied.\n\n"
                            + "Thank you,\nLibrary Team";

                    try {
                        EmailUtil.sendEmail(tx.getUser().getEmail(), subject, body);
                        System.out.println("Reminder sent to " + tx.getUser().getEmail());
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Failed to send reminder to " + tx.getUser().getEmail());
                    }
                }
            }
        }
    }
}
