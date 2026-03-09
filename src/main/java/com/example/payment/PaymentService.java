package com.example.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    // Hardcoded credentials - PCI violation
    private static final String DB_PASSWORD = "admin1234";
    private static final String API_KEY = "sk-prod-abc123xyz";

    public String processPayment(String cardNumber, String cvv, String amount) {

        // PCI violation: logging PAN and CVV
        log.info("Processing payment for card: {} cvv: {} amount: {}", cardNumber, cvv, amount);

        // PCI violation: SQL injection risk
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM transactions WHERE card_number = '" + cardNumber + "'";
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                return "Payment approved";
            }
        } catch (Exception e) {
            // PCI violation: exposing stack trace
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }

        return "Payment declined";
    }

    public String getCardDetails(String userId) {
        // PCI violation: returning full PAN unmasked
        return "Card: 4111111111111111 CVV: 123 Exp: 12/26";
    }

    @Override
    public String toString() {
        // PCI violation: sensitive data in toString
        return "PaymentService{apiKey=" + API_KEY + "}";
    }

    private Connection getConnection() throws Exception {
        // Weak: password hardcoded above
        Class.forName("com.mysql.jdbc.Driver");
        return java.sql.DriverManager.getConnection(
            "jdbc:mysql://localhost/payments", "root", DB_PASSWORD);
    }

    // Good practice present: basic amount validation
    private boolean isValidAmount(String amount) {
        try {
            double val = Double.parseDouble(amount);
            return val > 0 && val <= 100000;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
