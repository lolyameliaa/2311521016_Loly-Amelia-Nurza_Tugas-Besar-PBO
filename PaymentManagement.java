package src;

import java.util.*;

// Interface for Payment Management
public interface PaymentManagement {
    void addPayment(Payment payment);
    Payment getPaymentById(int paymentId);
    List<Payment> getAllPayments();
    void updatePayment(int paymentId, double amount);
    void deletePayment(int paymentId);
}