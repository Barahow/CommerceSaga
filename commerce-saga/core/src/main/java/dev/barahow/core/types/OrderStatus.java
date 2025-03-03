package dev.barahow.core.types;

public enum OrderStatus {
    CREATED,        // Order has been placed but not yet processed
    PENDING,        // Awaiting approval or payment processing
    APPROVED,       // Successfully processed and approved
    REJECTED,       // Order was rejected (e.g., failed payment, invalid details)
    CANCELED,       // Order was canceled by the user
    SHIPPED,        // Order has been shipped
    DELIVERED,      // Order was successfully delivered
    RETURNED,       // Order was returned by the customer
    FAILED          // Order failed due to technical issues or other problems

}
