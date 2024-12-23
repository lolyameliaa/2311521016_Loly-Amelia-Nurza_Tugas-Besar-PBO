package src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PaymentManagementImpl implements PaymentManagement {
    private Connection connection;

    public PaymentManagementImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addPayment(Payment payment) {
        String query = "INSERT INTO payments (paymentId, studentId, amount, paymentDate) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, payment.getPaymentId());
            pstmt.setString(2, payment.getStudentId());
            pstmt.setDouble(3, payment.getAmount());
            pstmt.setDate(4, new java.sql.Date(payment.getPaymentDate().getTime()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding payment: " + e.getMessage());
        }
    }

    @Override
    public Payment getPaymentById(int paymentId) {
        String query = "SELECT * FROM payments WHERE paymentId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, paymentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Payment(
                    rs.getInt("paymentId"),
                    rs.getString("studentId"),
                    rs.getDouble("amount"),
                    rs.getDate("paymentDate")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving payment: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        String query = "SELECT * FROM payments";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                payments.add(new Payment(
                    rs.getInt("paymentId"),
                    rs.getString("studentId"),
                    rs.getDouble("amount"),
                    rs.getDate("paymentDate")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving payments: " + e.getMessage());
        }
        return payments;
    }

    @Override
    public void updatePayment(int paymentId, double amount) {
        String query = "UPDATE payments SET amount = ? WHERE paymentId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, paymentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating payment: " + e.getMessage());
        }
    }

    @Override
    public void deletePayment(int paymentId) {
        String query = "DELETE FROM payments WHERE paymentId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, paymentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting payment: " + e.getMessage());
        }
    }
}