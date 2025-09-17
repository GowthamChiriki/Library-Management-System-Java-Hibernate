package com.library.model;

public enum TransactionStatus {
    ACTIVE,    // Currently borrowed
    RETURNED,  // Book returned
    OVERDUE, // Overdue (optional, can be calculated dynamically)
    CLOSED // Mark Transaction Closed when returned
}
