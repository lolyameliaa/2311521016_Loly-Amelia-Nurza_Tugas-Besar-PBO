package src;

import java.util.Date;

public class Payment {
    private int paymentId;
    private String studentId;
    private double amount;
    private Date paymentDate;

    public Payment(int paymentId, String studentId, double amount, Date paymentDate) {
        this.paymentId = paymentId;
        this.studentId = studentId;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public double getAmount() {
        return amount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}