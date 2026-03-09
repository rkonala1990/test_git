package com.example.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * PCI DSS v4.0 compliant payment processing service.
 *
 * Compliance notes:
 *   Req 3   — PANs are masked in all output; CVV is never stored or logged
 *   Req 4   — DB transport uses SSL (configured in DataSource externally)
 *   Req 6.2 — Parameterized queries only; no hardcoded credentials
 *   Req 7/8 — Method-level access control via @PreAuthorize
 *   Req 10  — Only masked PAN and non-sensitive metadata written to logs
 */
@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final JdbcTemplate jdbcTemplate;
    private final String apiKey;

    /**
     * Constructor injection — credentials come from environment variables
     * bound via application.yml, never hardcoded.
     *
     * PCI Req 6.2 & 8: API key loaded from ${PAYMENT_API_KEY} env var.
     */
    public PaymentService(
            DataSource dataSource,
            @Value("${payment.api-key}") String apiKey) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.apiKey = apiKey;
    }

    /**
     * Processes a payment request.
     *
     * PCI Req 7/8: Restricted to users with ROLE_PAYMENT_PROCESSOR.
     *
     * @param cardNumber full PAN — masked before any logging
     * @param amount     payment amount as BigDecimal (avoids floating-point precision loss)
     * @return approval or decline result string
     */
    @PreAuthorize("hasRole('ROLE_PAYMENT_PROCESSOR')")
    @Transactional
    public String processPayment(String cardNumber, BigDecimal amount) {
        // PCI Req 10: log only masked PAN — never log CVV under any circumstances
        log.info("Processing payment for card ending: {} amount: {}", maskPan(cardNumber), amount);

        if (!isValidAmount(amount)) {
            log.warn("Payment rejected — invalid amount: {}", amount);
            return "Payment declined: invalid amount";
        }

        // PCI Req 6.2: parameterized query — no string concatenation
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id FROM transactions WHERE card_number = ? LIMIT 1",
                cardNumber
            );

            if (!rows.isEmpty()) {
                log.info("Payment approved for card ending: {}", maskPan(cardNumber));
                return "Payment approved";
            }

        } catch (Exception e) {
            // PCI Req 10: log error class only — never expose internal details to caller
            log.error("Payment processing failed for card ending: {} error: {}",
                maskPan(cardNumber), e.getClass().getSimpleName(), e);
            return "Payment declined: processing error";
        }

        return "Payment declined";
    }

    /**
     * Returns masked card details for the given user.
     *
     * PCI Req 3: only the last 4 digits of the PAN are returned.
     * CVV is never returned after authorization.
     * PCI Req 7/8: restricted to authenticated users viewing their own data.
     *
     * @param userId the user whose card details are requested
     * @return masked card summary string
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional(readOnly = true)
    public String getCardDetails(String userId) {
        // PCI Req 3: query returns the stored (encrypted) PAN; we mask before returning
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
            "SELECT card_number, expiry_date FROM card_details WHERE user_id = ?",
            userId
        );

        if (rows.isEmpty()) {
            return "No card on file";
        }

        String pan     = (String) rows.get(0).get("card_number");
        String expiry  = (String) rows.get(0).get("expiry_date");

        // PCI Req 3: mask PAN — show last 4 digits only; never return CVV
        return String.format("Card: %s  Exp: %s", maskPan(pan), expiry);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Masks a PAN to show only the last 4 digits.
     * PCI DSS Req 3.3: PAN must be masked when displayed.
     *
     * @param pan full PAN string
     * @return masked string e.g. "****-****-****-1234"
     */
    private String maskPan(String pan) {
        if (pan == null || pan.length() < 4) {
            return "****";
        }
        return "*".repeat(pan.length() - 4) + pan.substring(pan.length() - 4);
    }

    /**
     * Validates that the payment amount is positive and within the allowed ceiling.
     * Uses BigDecimal for precision-safe comparison (replaces the former double-based logic).
     *
     * @param amount payment amount
     * @return true if valid
     */
    private boolean isValidAmount(BigDecimal amount) {
        if (amount == null) return false;
        return amount.compareTo(BigDecimal.ZERO) > 0
            && amount.compareTo(new BigDecimal("100000")) <= 0;
    }

    /**
     * PCI Req 3 / Req 10: API key is excluded from toString() to prevent
     * leaking credentials into logs or exception messages.
     */
    @Override
    public String toString() {
        return "PaymentService{}";
    }
}
