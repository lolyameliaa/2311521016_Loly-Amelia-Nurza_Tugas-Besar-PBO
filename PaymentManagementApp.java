package src;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PaymentManagementApp {
    public static void main(String[] args) {
        String dbUrl = "jdbc:postgresql://localhost:5432/ukt_management";
        String dbUser = "postgres";
        String dbPassword = "45awinurza";

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             Scanner scanner = new Scanner(System.in)) {

            PaymentManagementImpl paymentManager = new PaymentManagementImpl(connection);

            while (true) {
                System.out.println("\nPilih operasi:");
                System.out.println("1: Tambah Pembayaran (Create)");
                System.out.println("2: Lihat Pembayaran (Read)");
                System.out.println("3: Update Pembayaran (Update)");
                System.out.println("4: Hapus Pembayaran (Delete)");
                System.out.println("5: Keluar");
                System.out.print("Masukkan pilihan: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character

                switch (choice) {
                    case 1:
                        // CREATE: Tambah Pembayaran
                        System.out.print("Masukkan ID Mahasiswa: ");
                        String studentId = scanner.nextLine();
                        System.out.print("Masukkan jumlah pembayaran: ");
                        double amount = scanner.nextDouble();
                        scanner.nextLine(); // Consume newline

                        int newPaymentId = paymentManager.getNextPaymentId();

                        Payment newPayment = new Payment(newPaymentId, studentId, amount, new java.util.Date());
                        paymentManager.addPayment(newPayment);
                        System.out.println("Pembayaran berhasil ditambahkan dengan ID: " + newPaymentId);
                        break;

                    case 2:
                        // READ: Lihat semua pembayaran
                        List<Payment> payments = paymentManager.getAllPayments();
                        if (payments.isEmpty()) {
                            System.out.println("Tidak ada data pembayaran.");
                        } else {
                            payments.forEach(p -> System.out.println(
                                    "ID Pembayaran: " + p.getPaymentId() + 
                                    ", ID Mahasiswa: " + p.getStudentId() + 
                                    ", Jumlah: " + p.getAmount() +
                                    ", Tanggal: " + p.getPaymentDate()));
                        }
                        break;

                    case 3:
                        // UPDATE: Update pembayaran dengan validasi studentId
                        String validStudentId;
                        do {
                            System.out.print("Masukkan ID Mahasiswa yang ingin diupdate: ");
                            validStudentId = scanner.nextLine();
                            if (!paymentManager.isStudentIdExists(validStudentId)) {
                                System.out.println("ID Mahasiswa tidak ditemukan! Masukkan ID yang valid.");
                            }
                        } while (!paymentManager.isStudentIdExists(validStudentId));

                        System.out.print("Masukkan jumlah pembayaran baru: ");
                        double newAmount = scanner.nextDouble();
                        scanner.nextLine(); // Consume newline

                        boolean updateSuccess = paymentManager.updatePayment(validStudentId, newAmount);
                        if (updateSuccess) {
                            System.out.println("Pembayaran berhasil diupdate!");
                        } else {
                            System.out.println("Terjadi kesalahan saat mengupdate pembayaran.");
                        }
                        break;

                    case 4:
                        // DELETE: Hapus pembayaran dengan validasi studentId
                        String studentIdToDelete;
                        do {
                            System.out.print("Masukkan ID Mahasiswa yang ingin dihapus: ");
                            studentIdToDelete = scanner.nextLine();
                            if (!paymentManager.isStudentIdExists(studentIdToDelete)) {
                                System.out.println("ID Mahasiswa tidak ditemukan! Masukkan ID yang valid.");
                            }
                        } while (!paymentManager.isStudentIdExists(studentIdToDelete));

                        boolean deleteSuccess = paymentManager.deletePaymentByStudentId(studentIdToDelete);
                        if (deleteSuccess) {
                            System.out.println("Pembayaran berhasil dihapus!");
                        } else {
                            System.out.println("Terjadi kesalahan saat menghapus pembayaran.");
                        }
                        break;

                    case 5:
                        // EXIT: Keluar
                        System.out.println("Terima kasih! Keluar dari aplikasi.");
                        return;

                    default:
                        System.out.println("Pilihan tidak valid! Coba lagi.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Terjadi kesalahan koneksi database: " + e.getMessage());
        }
    }
}

class PaymentManagementImpl {
    private final Connection connection;

    public PaymentManagementImpl(Connection connection) {
        this.connection = connection;
    }

    public int getNextPaymentId() {
        String sql = "SELECT COALESCE(MAX(paymentid), 0) + 1 AS next_id FROM payments";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return resultSet.getInt("next_id");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching next payment ID: " + e.getMessage());
        }
        return 1;
    }

    public void addPayment(Payment payment) {
        String sql = "INSERT INTO payments (paymentid, studentid, amount, paymentdate) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, payment.getPaymentId());
            statement.setString(2, payment.getStudentId());
            statement.setDouble(3, payment.getAmount());
            statement.setDate(4, new java.sql.Date(payment.getPaymentDate().getTime()));
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding payment: " + e.getMessage());
        }
    }

    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int paymentId = resultSet.getInt("paymentid");
                String studentId = resultSet.getString("studentid");
                double amount = resultSet.getDouble("amount");
                Date paymentDate = resultSet.getDate("paymentdate");
                payments.add(new Payment(paymentId, studentId, amount, paymentDate));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching payments: " + e.getMessage());
        }
        return payments;
    }

    public boolean updatePayment(String studentId, double newAmount) {
        String sql = "UPDATE payments SET amount = ? WHERE studentid = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, newAmount);
            statement.setString(2, studentId);
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error updating payment: " + e.getMessage());
        }
        return false;
    }

    public boolean deletePaymentByStudentId(String studentId) {
        String sql = "DELETE FROM payments WHERE studentid = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting payment: " + e.getMessage());
        }
        return false;
    }

    public boolean isStudentIdExists(String studentId) {
        String sql = "SELECT 1 FROM payments WHERE studentid = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking student ID: " + e.getMessage());
        }
        return false;
    }
}

class Payment {
    private final int paymentId;
    private final String studentId;
    private final double amount;
    private final java.util.Date paymentDate;

    public Payment(int paymentId, String studentId, double amount, java.util.Date paymentDate) {
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

    public java.util.Date getPaymentDate() {
        return paymentDate;
    }
}
